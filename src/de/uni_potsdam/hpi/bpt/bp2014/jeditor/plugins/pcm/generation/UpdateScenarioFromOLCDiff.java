package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.ModelDescription;
import com.inubit.research.client.ModelDirectory;
import com.inubit.research.client.ModelDirectoryEntry;
import com.inubit.research.gui.Workbench;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.olc.FragmentsFromOLCVersions;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.OLCAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragmentNode;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMScenario;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.Association;
import net.frapu.code.visualization.bpmn.Task;

import java.util.*;


public class UpdateScenarioFromOLCDiff extends GeneratePCMFragmentFromMultipleOLC {
    Map<ObjectLifeCycle, ObjectLifeCycle> predecessors;
    Map<OLCAdapter, OLCAdapter> wrappedPredecessors;
    PCMScenario scenario;
    private Collection<PCMFragment> fragments;

    public UpdateScenarioFromOLCDiff(Workbench wb) {
        super(wb);
    }

    @Override
    protected String getName() {
        return "Update Scenario From OLC Versions";
    }

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
                    if (!((ModelDescription) directoryEntry).getHead().getPredecessors().isEmpty()) {
                        ProcessModel model = ((ModelDescription) directoryEntry).getHead().getProcessModel();
                        if (model instanceof ObjectLifeCycle) {
                            ProcessModel predecessor = ((ModelDescription) directoryEntry).getPredecessors(
                                    ((ModelDescription) directoryEntry).getHead()).iterator().next().getProcessModel();
                            predecessors.put((ObjectLifeCycle) model, (ObjectLifeCycle) predecessor);
                        } else if (model instanceof PCMFragment && fragmentIds.contains(model.getId())) {
                            fragments.add((PCMFragment) model);
                        }
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
        return ffOLCv.convert(wrappedPredecessors.keySet(), wrappedPredecessors.values());
    }

    @Override
    protected Collection<? extends ProcessModel> convertModels(Collection<? extends IModel> generatedModels) {
        Collection<PCMFragment> smallFragments = (Collection<PCMFragment>) super.convertModels(generatedModels);
        Collection<PCMFragment> newFragments = new HashSet<>(smallFragments);
        for (ProcessModel smallFragment : smallFragments) {
            for (ProcessNode processNode : smallFragment.getNodesByClass(Task.class)) {
                for (PCMFragment fragment : fragments) {
                    for (ProcessNode node : fragment.getNodeByName(processNode.getName())) {
                        newFragments.remove(smallFragment);
                        for (ProcessEdge processEdge : smallFragment.getIncomingEdges(Association.class, processNode)) {
                            Association newEdge = new Association(processEdge.getSource(), node);
                            fragment.addEdge(newEdge);
                            fragment.addNode(processEdge.getSource());
                        }
                        for (ProcessEdge processEdge : smallFragment.getOutgoingEdges(Association.class, processNode)) {
                            Association newEdge = new Association(node, processEdge.getTarget());
                            fragment.addEdge(newEdge);
                            fragment.addNode(processEdge.getSource());
                        }
                    }
                }
            }
        }
        return newFragments;
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