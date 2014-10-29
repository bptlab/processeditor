package net.frapu.code.visualization.pcm;

import net.frapu.code.converter.ConverterHelper;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.DataObject;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 * This class describes a PCM Scenario Model. It inherits from Process Model.
 * A Scenario is a collection of PCM Fragments.
 *
 * @author Stephan Haarmann
 * @version 28.10.2014
 */
public class PCMScenario extends ProcessModel {

    private File workspace;
    private List<ProcessModel> pcmFragments;
    private List<ProcessNode> dataObjects;

    public PCMScenario() {
        super();
        processUtils = new PCMUtils();
        dataObjects = new LinkedList<ProcessNode>();
        ProcessNode node = new PCMFragmentCollection();
        pcmFragments = new LinkedList<ProcessModel>();
        addNode(node);
        node = new PCMDataObjectCollection();
        node.setPos(500,500);
        addNode(node);
    }

    @Override
    public String getDescription() {
        return "PCM Scenario Model";
    }

    /**
     * Assure that one can not add a PCMFragmentCollection node
     *
     * @param node the node to be added
     */
    @Override
    public synchronized void addNode(ProcessNode node) {
        if (node instanceof PCMFragmentCollection) {
            List<ProcessNode> fragmentCollections = new LinkedList<ProcessNode>();
            for (ProcessNode n : getNodes()) {
                if (n instanceof PCMFragmentCollection) {
                    fragmentCollections.add(n);
                }
            }
            if (!fragmentCollections.isEmpty()) {
                return;
            }
        }
        else if (node instanceof PCMDataObjectCollection) {
            List<ProcessNode> dataCollections = new LinkedList<ProcessNode>();
            for (ProcessNode n : getNodes()) {
                if (n instanceof PCMDataObjectCollection) {
                    dataCollections.add(n);
                }
            }
            if (!dataCollections.isEmpty()) {
                return;
            }
        }
        super.addNode(node);
    }


    /**
     * Make sure, that no one deletes the PCMFragmentCollection node
     *
     * @param node
     */
    @Override
    public synchronized void removeNode(ProcessNode node) {
        if (!(node instanceof  PCMFragmentCollection || node instanceof PCMDataObjectCollection)) {
            super.removeNode(node);
        }
    }

    @Override
    public List<Class<? extends ProcessNode>> getSupportedNodeClasses() {
        List<Class<? extends ProcessNode>> nodes = new LinkedList<Class<? extends ProcessNode>>();
        nodes.add(PCMFragmentCollection.class);
        nodes.add(PCMFragmentNode.class);
        return nodes;
    }

    @Override
    public List<Class<? extends ProcessEdge>> getSupportedEdgeClasses() {
        return null;
    }

    /**
     * Set a new Workspace. Update all PCMFragments and Dataobjects of the scenario.
     * @param workspace
     */
    public void setWorkspace(File workspace) {
        this.workspace = workspace;
        createModelList();
        createDataList();
    }

    private void createDataList() {
        for (ProcessModel pm : pcmFragments) {
            for (ProcessNode pn : pm.getNodes()) {
                if (pn instanceof DataObject) {
                    if (!dataObjects.contains(pn)) {
                        dataObjects.add(pn);
                    }
                }
            }
        }
        createNotesForDataObjects();
    }

    private void createNotesForDataObjects() {
        PCMDataObjectCollection dataColl = null;
        for (ProcessNode node : getNodes()) {
            if (node instanceof PCMDataObjectCollection) {
                dataColl = (PCMDataObjectCollection)node;
            } else if (node instanceof PCMDataObjectNode) {
                removeNode(node);
            }
        }
        int i = dataObjects.size();
        for (ProcessNode dataObject : dataObjects) {
            ProcessNode node = new PCMFragmentNode();
            node.setText(dataObject.getText());
            node.setSize(getSize().width - 2, 20);
            node.setPos(dataColl.getPos().x, dataColl.getPos().y - dataColl.getSize().height / 2 + (20 * i));
            addNode(node);
            i--;
        }

    }

    /**
     * Add all PCMFragments of the Workspace to the List of Models
     */
    private void createModelList() {
        // Reset the FragmentList
        pcmFragments.clear();
        // Create a filter to select only models
        FileFilter modelFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getPath().endsWith(".model");
            }
        };

        // Get all model-Files
        File[] arrayOfModelFiles = workspace.listFiles(modelFilter);
        // open the models
        for (File modelFile : arrayOfModelFiles) {
            pcmFragments.addAll(getPCMModelsFromFile(modelFile));
        }
        createNodesForFragments();
    }

    /**
     * Creates a PCMFragmentNode for each Fragment of the Scenario.
     */
    private void createNodesForFragments() {
        PCMFragmentCollection fragColl = null;
        for (ProcessNode node : getNodes()) {
            if (node instanceof PCMFragmentCollection) {
                fragColl = (PCMFragmentCollection)node;
            } else if (node instanceof PCMFragmentNode) {
                removeNode(node);
            }
        }
        int i = pcmFragments.size();
        for (ProcessModel model : pcmFragments) {
            ProcessNode node = new PCMFragmentNode();
            node.setText(model.getProcessName());
            node.setSize(getSize().width - 2, 20);
            node.setPos(fragColl.getPos().x, fragColl.getPos().y - fragColl.getSize().height / 2 + (20 * i));
            addNode(node);
            i--;
        }
    }

    /**
     * Opens all models saved in modelFile and selects the PCMFragments
     *
     * @param modelFile the file to be opened
     * @return a List of all PCMFragments saved modelFile
     */
    private List<ProcessModel> getPCMModelsFromFile(File modelFile) {
        try {
            // Import all Models
            List<ProcessModel> models = ConverterHelper.importModels(modelFile);
            // Check for Errors
            if (models == null) {
                throw new Exception("Model type not recognized");
            }
            // Select the PCMFragments
            List<ProcessModel> pcmModels = new LinkedList<ProcessModel>();
            for (ProcessModel process : models) {
                process.setProcessModelURI(modelFile.getAbsolutePath());
                if (process instanceof PCMFragment) {
                    pcmModels.add(process);
                }
            }
            return  pcmModels;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Could not open process model: " + modelFile.getAbsolutePath());
        }
        return null;
    }
}
