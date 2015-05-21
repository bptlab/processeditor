package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.ModelDescription;
import com.inubit.research.client.ModelDirectory;
import com.inubit.research.client.ModelDirectoryEntry;
import com.inubit.research.gui.Workbench;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.ActivityCentricProcessModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.pcm.ScenarioToSynchronizedOLC;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm.ACPMAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragmentNode;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMScenario;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This plugin allows us to generate multiple Object Life Cycles from
 * one PCMScenario.
 * The Scenario has to be opened in the Workbench. In addition all
 * the fragments must be available on a Process Editor Server.
 * The Plugin will connect to the server and fetch all the fragments
 * in order to perform the transformation.
 */
public class GenerateOLCsFromScenario extends GeneratorPlugin {
    /**
     * The workbench, it is necessary in order to determine
     * the opened PCMScenario.
     */
    private final Workbench wb;
    /**
     * A Collection holding the ids of all Fragments from the
     * Scenario to be transformed.
     */
    private Collection<String> fragmentIDs;

    /**
     * Creates a new instance of the Plugin.
     * The workbench which will trigger the plugin and hold the
     * source and the generated Models will be specified by the parameter.
     * @param wb The Workbench which will be used to receive the PCMScenario.
     */
    public GenerateOLCsFromScenario(Workbench wb) {
        super(wb);
        this.wb = wb;
    }

    @Override
    protected String getName() {
        return "OLC from Scenario";
    }

    @Override
    protected void initialize() {
        fragmentIDs = new HashSet<>();
    }

    /**
     * Creates an edge for a ObjectLife  for a given transitions.
     * In order to receive the source and the target all the nodes
     * created must be offered. If the source and target have not been
     * used yet they will be created, else the cached version will
     * be used.
     * @param objectLifeCycle The Object life cycle which contains the edge.
     *                        Necessary to determine final and start nodes.
     * @param transition The transition which is the base for the new ProcessEdge.
     * @param processedNodes The cache of nodes, which will be used to store
     *                       every node once it has been created for reuse.
     * @return The StateTransition which has just been created.
     */
    private de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition createEdgeFor(ObjectLifeCycle objectLifeCycle, IEdge transition, Map<INode, de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState> processedNodes) {
        de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition edge =
                new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition();
        ProcessNode source = getNodeFor(processedNodes, transition.getSource());
        ProcessNode target = getNodeFor(processedNodes, transition.getTarget());
        if (objectLifeCycle.getFinalNodes().contains(transition.getTarget())) {
            target.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState.PROP_IS_FINAL, ProcessNode.TRUE);
        }
        if (objectLifeCycle.getFinalNodes().contains(transition.getSource())) {
            source.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState.PROP_IS_FINAL, ProcessNode.TRUE);
        }
        edge.setSource(source);
        edge.setTarget(target);
        edge.setLabel(((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition) transition).getLabel());
        return edge;
    }

    /**
     * Gets a ProcessNode for a given INode from a cache.
     * If it does not exist in the cache a new one will be created and the cache will be updated.
     * @param processedNodes The cache of ProcessNodes.
     * @param node The INode which must be transformed into a ProcessNode.
     * @return Returns the newly created or cached ProcessNode.
     */
    private ProcessNode getNodeFor(Map<INode, de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState> processedNodes, INode node) {
        if (!processedNodes.containsKey(node)) {
            processedNodes.put(node, new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState());
        }
        de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState newNode = processedNodes.get(node);
        newNode.setText(((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState) node).getName());

        markAsStartIfInit(newNode);
        return newNode;
    }

    /**
     * If a note has the name "init" it is the start state.
     * This method checks weather it is or not and then sets the property of that node.
     * @param state The state which will be checked.
     * @return Returns true, if that state is a start state else it returns false.
     */
    private boolean markAsStartIfInit(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState state) {
        if (state.getName().equals("init")) {
            state.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState.PROP_IS_START, ProcessNode.TRUE);
            return true;
        }
        return false;
    }

    /**
     * Extracts all PCMFragments from the remote repository whichs id is
     * a inside the fragmentIDs collection.
     * @param directory The directory to check.
     * @return The List of ProcessModels which has been extracted.
     */
    @Override
    protected Collection<? extends ProcessModel> extractModelsFromDirectory(ModelDirectory directory) {
        ProcessModel scenario = wb.getSelectedModel();
        if (!(scenario instanceof PCMScenario)) {
            System.err.println("Model has to be a PCM Scenario");
            return new HashSet<>();
        }
        Collection<PCMFragment> fragments = new HashSet<>();
        for (ProcessNode fragment : scenario.getNodesByClass(PCMFragmentNode.class)) {
            fragmentIDs.add(fragment.getProperty(PCMFragmentNode.PROP_FRAGMENT_MID));
        }
        for (ModelDirectoryEntry modelDirectoryEntry : directory.getEntries()) {
            if (modelDirectoryEntry instanceof ModelDirectory) {
                fragments.addAll((Collection<? extends PCMFragment>)
                        extractModelsFromDirectory((ModelDirectory) modelDirectoryEntry));
            } else if (modelDirectoryEntry instanceof ModelDescription) {
                try {
                    ProcessModel model = ((ModelDescription) modelDirectoryEntry).getHead().getProcessModel();
                    if (model instanceof PCMFragment && fragmentIDs.contains(model.getId())) {
                        fragments.add((PCMFragment) model);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return fragments;
    }

    @Override
    protected Collection<? extends IModel> generateModels(Collection<? extends IModel> wrappedModels) {
        ScenarioToSynchronizedOLC converter = new ScenarioToSynchronizedOLC();
        Collection<ObjectLifeCycle> olcs = (converter.convert((Collection<ActivityCentricProcessModel>) wrappedModels))
                .getOLCs();
        return olcs;
    }

    @Override
    protected Collection<? extends IModel> wrapModels(Collection<? extends ProcessModel> models) {
        Collection<ActivityCentricProcessModel> adaptedFragments = new HashSet<>();
        for (ProcessModel fragment : models) {
            adaptedFragments.add(new ACPMAdapter(fragment));
        }
        return adaptedFragments;
    }

    @Override
    protected Collection<? extends ProcessModel> convertModels(Collection<? extends IModel> generatedModels) {
        Collection<ProcessModel> convertedModels = new HashSet<>();
        for (IModel objectLifeCycle : generatedModels) {
            ProcessModel olc = new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle();
            Map<INode, de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState> processedNodes = new HashMap<>();
            for (IEdge transition : ((ObjectLifeCycle)objectLifeCycle).getEdgeOfType(StateTransition.class)) {
                de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition edge =
                        createEdgeFor((ObjectLifeCycle) objectLifeCycle, transition, processedNodes);
                olc.addEdge(edge);
                if (!olc.getNodes().contains(edge.getSource())) {
                    olc.addNode(edge.getSource());
                }
                if (!olc.getNodes().contains(edge.getTarget())) {
                    olc.addNode(edge.getTarget());
                }
            }
            convertedModels.add(olc);

        }
        return convertedModels;
    }
}