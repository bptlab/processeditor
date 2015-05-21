package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.gui.Workbench;
import com.inubit.research.gui.plugins.WorkbenchPlugin;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.activity_centric.ActivityCentricToSynchronizedOLC;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.synchronize.SynchronizedObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm.ACPMAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * This class allows us t o generate multiple {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle}
 * based on a single {@link PCMFragment}.
 * It will automatically assume that the PCMFragment which will be transformed is the active
 * model inside the workbench.
 * An error will be thrown if the the model is not a PCMFragment.
 * In addition their are some assumptions.
 * For example must the fragment have exactly one start and end event
 * and uncontrolled flow is not supported.
 */
public class GenerateOLCFromFragment extends WorkbenchPlugin {
    /**
     * Saves the workbench, which triggered the plugin.
     * It is a mandatory field because the PCMFragment will be determined by this object
     * and the generated OLCs will be opened inside this workbench.
     */
    private final Workbench wb;

    /**
     * Creates a new Object of the plugin and initializes
     * the {@link #wb} workbench.
     * @param wb The workbench triggering this plugin.
     */
    public GenerateOLCFromFragment(Workbench wb) {
        super(wb);
        this.wb = wb;
    }

    @Override
    public Component getMenuEntry() {
        JMenuItem menuItem = new JMenuItem("Generate OLC");
        menuItem.addActionListener(new ActionListener() {
            /**
             * This function triggers the conversion.
             * It will check if the current model is a PCMFragment
             * than wrap it
             * and run a converter to generate a synchronized OLC.
             * Every OLC from this synchronized OLC will then be transformed
             * into a model which can be opened by the Process Editor before it will
             * be opened.
             * @param e The event it will not be used.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Workbench" + wb);
                ProcessModel model = wb.getSelectedModel();
                if (!(model instanceof PCMFragment)) {
                    System.err.println("Model has to be a PCM Fragment");
                    return;
                }
                ACPMAdapter modelAdapted = new ACPMAdapter(model);
                ActivityCentricToSynchronizedOLC acpm2solc = new ActivityCentricToSynchronizedOLC();
                SynchronizedObjectLifeCycle solc = (SynchronizedObjectLifeCycle) acpm2solc.convert(modelAdapted);
                for (ObjectLifeCycle objectLifeCycle : solc.getOLCs()) {
                    ProcessModel olc = new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle();
                    Map<INode, DataObjectState> processedNodes = new HashMap<>();
                    for (IEdge transition : objectLifeCycle.getEdgeOfType(de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition.class)) {
                        StateTransition edge = new StateTransition();
                        if (!processedNodes.containsKey(transition.getSource())) {
                            processedNodes.put(transition.getSource(), new DataObjectState());
                        }
                        DataObjectState source = processedNodes.get(transition.getSource());
                        source.setProperty(ProcessNode.PROP_TEXT,
                                ((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState)transition.getSource()).getName());
                        edge.setSource(source);
                        if (objectLifeCycle.getFinalNodes().contains(transition.getSource())) {
                            source.setProperty(DataObjectState.PROP_IS_FINAL, ProcessNode.TRUE);
                        }
                        if (!processedNodes.containsKey(transition.getTarget())) {
                            processedNodes.put(transition.getTarget(), new DataObjectState());
                        }
                        DataObjectState target = processedNodes.get(transition.getTarget());
                        target.setProperty(ProcessNode.PROP_TEXT,
                                ((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState)transition.getTarget()).getName());
                        if (objectLifeCycle.getFinalNodes().contains(transition.getTarget())) {
                            target.setProperty(DataObjectState.PROP_IS_FINAL, ProcessNode.TRUE);
                        }
                        edge.setTarget(target);
                        edge.setLabel(((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition)transition).getLabel());
                        olc.addEdge(edge);
                        if (!olc.getNodes().contains(source)) {
                            olc.addNode(source);
                        }
                        if (!olc.getNodes().contains(target)) {
                            olc.addNode(target);
                        }
                    }
                    wb.addModel(olc.getProcessName(), olc);
                }
            }
        });
        return menuItem;
    }
}
