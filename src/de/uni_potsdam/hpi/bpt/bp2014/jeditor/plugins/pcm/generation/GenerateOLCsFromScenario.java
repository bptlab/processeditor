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
 * Created by Stpehan on 09.05.2015.
 */
public class GenerateOLCsFromScenario extends GeneratorPlugin {
    private final Workbench wb;
    private Collection<String> fragmentIDs;

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

    private de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition createEdgeFor(ObjectLifeCycle objectLifeCycle, IEdge transition, Map<INode, de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState> processedNodes) {
        de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition edge =
                new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition();
        ProcessNode source = getNodeFor(processedNodes, transition.getSource());
        ProcessNode target = getNodeFor(processedNodes, transition.getTarget());
        if (objectLifeCycle.getFinalStates().contains(transition.getTarget())) {
            target.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState.PROP_IS_FINAL, ProcessNode.TRUE);
        }
        if (objectLifeCycle.getFinalStates().contains(transition.getSource())) {
            source.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState.PROP_IS_FINAL, ProcessNode.TRUE);
        }
        edge.setSource(source);
        edge.setTarget(target);
        edge.setLabel(((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition) transition).getLabel());
        return edge;
    }

    private ProcessNode getNodeFor(Map<INode, de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState> processedNodes, INode node) {
        if (!processedNodes.containsKey(node)) {
            processedNodes.put(node, new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState());
        }
        de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState newNode = processedNodes.get(node);
        newNode.setText(((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState) node).getName());

        markAsStartIfInit(newNode);
        return newNode;
    }

    private boolean markAsStartIfInit(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState state) {
        if (state.getName().equals("init")) {
            state.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState.PROP_IS_START, ProcessNode.TRUE);
            return true;
        }
        return false;
    }

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
                extractModelsFromDirectory((ModelDirectory) modelDirectoryEntry);
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
