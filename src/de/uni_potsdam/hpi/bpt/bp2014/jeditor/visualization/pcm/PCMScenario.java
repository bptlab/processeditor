package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import net.frapu.code.converter.ConverterHelper;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.DataObject;
import net.frapu.code.visualization.bpmn.Task;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class describes a PCM Scenario Model. It inherits from Process Model.
 * A Scenario is a collection of PCM Fragments.
 *
 * @author Stephan Haarmann & Juliane Imme
 * @version 28.10.2014
 */
public class PCMScenario extends ProcessModel {

    /**
     * Defines the name of the propertie, which defines the corresponding domain Model.
     */
    public static final String DOMAIN_MODEL_URI_PROP = "domainModelURI";
    private File workspace;
    private List<ProcessModel> pcmFragments;
    private List<ProcessNode> dataObjects;
    public static final String PROP_TERMINATIONCONDITION = "Termination condition";
    private Map<String, List<String>> references;

    /**
     * Creates a new PCMScenario. A PCMFragmentCollection and PCMDataObjectCollection is added automatically.
     * The Lists, fields and properties will be initialized.
     */
    public PCMScenario() {
        super();
        initProperties();
        processUtils = new PCMUtils();
        dataObjects = new LinkedList<ProcessNode>();
        ProcessNode node = new PCMFragmentCollection();
        pcmFragments = new LinkedList<ProcessModel>();
        references = new LinkedHashMap<String, List<String>>();
        setProperty(PROP_TERMINATIONCONDITION, "");
        node.setPos(700, 500);
        addNode(node);
        node = new PCMDataObjectCollection();
        node.setPos(500,500);
        addNode(node);
    }

    /**
     * This method initializes properties which are specific for the PCMScenario.
     * For example the domainModelURI.
     */
    private void initProperties() {
        setProperty(DOMAIN_MODEL_URI_PROP, "");
    }

    /**
     * The description of the Model, it will be used to represent the Model in the "New" menu
     * @return a String containing the Description
     */
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
            if (!getNodesByClass(PCMFragmentCollection.class).isEmpty()) {
                return;
            }
        } else if (node instanceof PCMDataObjectCollection) {
            if (!getNodesByClass(PCMDataObjectCollection.class).isEmpty()) {
                return;
            }
        } else if (node instanceof PCMDataObjectNode) {
            List<String> dataObjectNames = new LinkedList<String>();
            for (ProcessNode n : getNodesByClass(PCMDataObjectNode.class)) {
                dataObjectNames.add(n.getName());
            }
            if (dataObjectNames.contains(node.getName())) {
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

    /**
     * No nodes should be added manually. Hence the getSupportedNodeClasses return an empty List.
     * @return an empty List
     */
    @Override
    public List<Class<? extends ProcessNode>> getSupportedNodeClasses() {
        List<Class<? extends ProcessNode>> nodes = new LinkedList<Class<? extends ProcessNode>>();
        return nodes;
    }

    /**
     * No edges can be added
     * @return an empty List of ProcessEdges;
     */
    @Override
    public List<Class<? extends ProcessEdge>> getSupportedEdgeClasses() {
        return new LinkedList<Class<? extends ProcessEdge>>();
    }

    /**
     * Set a new Workspace. Update all PCMFragments and Dataobjects of the scenario.
     * @param workspace
     */
    @Deprecated
    public void setWorkspace(File workspace) {
        this.workspace = workspace;
        createModelList();
        createDataList();
    }

    /**
     * This method updates the references. First they will be cleared task a new reference will be added.
     * A Reference contains a Task and all Models which contain this task.
     */
    private void createReferences() {
        references.clear();
        for(ProcessModel m : pcmFragments){
            for(ProcessNode n: m.getNodesByClass(Task.class)){
                if(references.containsKey(n.getId())) {
                    references.get(n.getId()).add(m.getId());
                } else {
                    List<String> referenceModels = new LinkedList<String>();
                    referenceModels.add(m.getId());
                    references.put(n.getId(), referenceModels);
                }
            }
        }
    }

    /**
     * Creates the DataList. It checks every added pcmFragment for DataObjects and adds them to the list of Data Objects.
     */
    public void createDataList() {
        for (ProcessModel pm : pcmFragments) {
            for (ProcessNode pn : pm.getNodesByClass(DataObject.class)) {
                if (!dataObjects.contains(pn)) {
                    dataObjects.add(pn);
                }
            }
        }
        createNodesForDataObjects();
    }

    /**
     * PCMDataObjectNodes will be added. It will be assumed that dataObjects have a unique name. Which means, if a dataObject
     * "invoice" is used in two fragments or twice in one fragment it will be added only once
     */
    private void createNodesForDataObjects() {
        PCMDataObjectCollection dataColl = (PCMDataObjectCollection)getNodesByClass(PCMDataObjectCollection.class).get(0);
        List<ProcessNode> dataNodes = getNodesByClass(PCMDataObjectNode.class);
        for (ProcessNode node : dataNodes) {
            removeNode(node);
        }

        int i = dataObjects.size();
        for (ProcessNode dataObject : dataObjects) {
            ProcessNode node = new PCMDataObjectNode();
            node.setText(dataObject.getText());
            node.setSize(100, 20);
            node.setPos(dataColl.getPos().x, dataColl.getPos().y - dataColl.getSize().height / 2 + (20 * i));
            dataColl.addProcessNode(node);
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
     * Adds a Fragment to the List of all Fragments
     *
     * @param fragment the new Model to be added
     */
    public synchronized void addPCMFragment(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment fragment) {
        pcmFragments.add(fragment);
    }



    /**
     * Creates a PCMFragmentNode for each Fragment of the Scenario.
     */
    public synchronized void createNodesForFragments() {
        PCMFragmentCollection fragColl = (PCMFragmentCollection)getNodesByClass(PCMFragmentCollection.class).get(0);
        List<ProcessNode> fragmentNodes = getNodesByClass(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragmentNode.class);
        for (ProcessNode node : fragmentNodes) {
            removeNode(node);
        }

        int i = pcmFragments.size();
        for (ProcessModel model : pcmFragments) {
            ProcessNode node = new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragmentNode();
            node.setText(model.getProcessName());
            node.setSize(100, 20);
            node.setPos(fragColl.getPos().x, fragColl.getPos().y - fragColl.getSize().height / 2 + (20 * i));
            node.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragmentNode.PROP_FRAGMENT_MID, model.getId());
            fragColl.addProcessNode(node);
            addNode(node);
            i--;
        }
        createReferences();
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
                if (process instanceof de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment) {
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

    public List<ProcessModel> getModelList() {
        return pcmFragments;
    }

    public Map<String, List<String>> getReferences() {
        return references;
    }

    /**
     * Deletes all existing Models and updates the Nodes
     */
    public synchronized void reset() {
        pcmFragments.clear();
        dataObjects.clear();
        references.clear();
        createNodesForFragments();
        createNodesForDataObjects();
    }
}
