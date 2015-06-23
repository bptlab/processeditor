package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.ModelDescription;
import com.inubit.research.client.ModelDirectory;
import com.inubit.research.client.ModelDirectoryEntry;
import com.inubit.research.gui.Workbench;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.olc.FragmentsFromOLCVersions;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.synchronize.SynchronizedDiff;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.OLCAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragmentNode;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMScenario;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.Association;
import net.frapu.code.visualization.bpmn.DataObject;
import net.frapu.code.visualization.bpmn.Task;

import java.util.*;

/**
 * This class extends the {@link GeneratePCMFragmentFromMultipleOLC} Plugin.
 * This plugin will extract the two latest versions of multiple OLCs and use
 * the differences to update the Scenario currently opend by the PE.
 */
public class UpdateScenarioFromOLCDiff extends GeneratePCMFragmentFromMultipleOLC {
    /**
     * A Map which holds two versions of every object life cycle.
     * The latest one is the key and the one before is the value.
     */
    Map<ObjectLifeCycle, ObjectLifeCycle> predecessors;
    /**
     * A map holding the same information as {@link #predecessors}
     * but key and value are adapted OLCs.
     */
    Map<OLCAdapter, OLCAdapter> wrappedPredecessors;
    /**
     * The scenario that will be updated by this plugin.
     */
    PCMScenario scenario;
    /**
     * The list of fragments that have to be opened/created/updated.
     */
    private Collection<PCMFragment> fragments;

    public UpdateScenarioFromOLCDiff(Workbench wb) {
        super(wb);
    }

    @Override
    protected String getName() {
        return "Update Scenario From OLC Versions";
    }

    /**
     * This method extract all Fragments of the scenario, and all Object Life Cycles, which
     * have at least two versions.
     * The Fragments will be saved in {@link #fragments}.
     * The Versions will be saved in {@link #predecessors}
     * @param directory The root directory of the Process editor server.
     * @return The latest versions of the Object Life Cycles will be returned.
     * These are the keys of {@link #predecessors}.
     */
    @Override
    protected Collection<? extends ProcessModel> extractModelsFromSubDirectory(ModelDirectory directory) {
        if (!(wb.getSelectedModel() instanceof PCMScenario)) {
            System.err.println("The model should be a PCMScenario");
            return new ArrayList<>(0);
        }
        scenario = (PCMScenario) wb.getSelectedModel();
        Collection<String> fragmentIds = new HashSet<>();
        for (ProcessNode processNode : scenario.getNodes()) {
            fragmentIds.add(processNode.getProperty(PCMFragmentNode.PROP_FRAGMENT_MID));
        }
        for (ModelDirectoryEntry directoryEntry : directory.getEntries()) {
            if (directoryEntry instanceof ModelDirectory) {
                extractModelsFromSubDirectory((ModelDirectory) directoryEntry);
            } else if (directoryEntry instanceof ModelDescription) {
                try {
                    ProcessModel model = ((ModelDescription) directoryEntry).getHead().getProcessModel();
                    if (model instanceof ObjectLifeCycle) {
                        if (!((ModelDescription) directoryEntry).getHead().getPredecessors().isEmpty()) {
                            ProcessModel predecessor = ((ModelDescription) directoryEntry).getPredecessors(
                                    ((ModelDescription) directoryEntry).getHead()).iterator().next().getProcessModel();
                            predecessors.put((ObjectLifeCycle) model, (ObjectLifeCycle) predecessor);
                        }
                    } else if (model instanceof PCMFragment && fragmentIds.contains(model.getId())) {
                        fragments.add((PCMFragment) model);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return predecessors.keySet();
    }

    @Override
    protected Collection<? extends IModel> generateModels(Collection<? extends IModel> wrappedModels) {
        FragmentsFromOLCVersions ffOLCv = new FragmentsFromOLCVersions();
        return ffOLCv.convert(new SynchronizedDiff(wrappedPredecessors.keySet(), wrappedPredecessors.values()))
                .getFragments();
    }

    @Override
    protected Collection<? extends ProcessModel> convertModels(Collection<? extends IModel> generatedModels) {
        Collection<PCMFragment> smallFragments = (Collection<PCMFragment>) super.convertModels(generatedModels);
        Collection<PCMFragment> newFragments = new HashSet<>(smallFragments);
        for (ProcessModel smallFragment : smallFragments) {
            chooseFragmentForAndFrom(smallFragment, newFragments);
        }
        return newFragments;
    }

    /**
     * This method determines weather or not a generated Fragment is only an update for an existing
     * one or a completely new.
     * It will update the other fragments if possible else it will use the generated one.
     * If the fragment (smallFragment) is an update it will be removed from the collection of newFragments
     * and add the updated fragments.
     * Be aware that this method writes all results to the newFragments collection.
     * @param smallFragment The Fragment generated. If it contains only activities which already exists
     *                      its contents will be added to the other fragments.
     * @param newFragments The Collection of fragments which have been generated/updated.
     *                     The result of this method will be written to that collection.
     */
    private void chooseFragmentForAndFrom(ProcessModel smallFragment, Collection<PCMFragment> newFragments) {
        for (ProcessNode processNode : smallFragment.getNodesByClass(Task.class)) {
            for (PCMFragment fragment : fragments) {
                for (ProcessNode node : fragment.getNodeByName(processNode.getName())) {
                    newFragments.remove(smallFragment);
                    for (ProcessEdge processEdge : smallFragment.getIncomingEdges(Association.class, processNode)) {
                        Association newEdge = new Association(getNodeForFrom(processEdge.getSource(), fragment), node);
                        if (!fragment.getPrecedingNodes(Association.class, node).contains(newEdge.getSource())) {
                            fragment.addEdge(newEdge);
                        }
                    }
                    for (ProcessEdge processEdge : smallFragment.getOutgoingEdges(Association.class, processNode)) {
                        Association newEdge = new Association(node, getNodeForFrom(processEdge.getTarget(), fragment));
                        if (!fragment.getPrecedingNodes(Association.class, newEdge.getTarget()).contains(node)) {
                            fragment.addEdge(newEdge);
                        }
                    }
                }
                if (!newFragments.contains(smallFragment)) {
                    newFragments.add(fragment);
                }
            }
        }
    }

    /**
     * This method checks if a node already exists inside a fragment.
     * And returns the existing one or adds the specified and returns it then.
     * @param node The node which potentially should be added.
     * @param fragment The fragment which should contain that node.
     * @return The node added to fragment/ available to the fragment.
     */
    private ProcessNode getNodeForFrom(ProcessNode node, PCMFragment fragment) {
        for (ProcessNode processNode : fragment.getNodeByName(node.getName())) {
            if (processNode.getProperty(DataObject.PROP_STATE).equals(node.getProperty(DataObject.PROP_STATE))) {
                return processNode;
            }
        }
        fragment.addNode(node);
        return node;
    }

    @Override
    protected Collection<? extends IModel> wrapModels(Collection<? extends ProcessModel> models) {
        for (ProcessModel olc : models) {
            wrappedPredecessors.put(new OLCAdapter((ObjectLifeCycle) olc),
                    new OLCAdapter(predecessors.get(olc)));
        }
        return wrappedPredecessors.keySet();
    }


    @Override
    protected void initialize() {
        super.initialize();
        predecessors = new HashMap<>();
        wrappedPredecessors = new HashMap<>();
        fragments = new HashSet<>();
    }
}