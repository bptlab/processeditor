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
 * Created by Stpehan on 08.05.2015.
 */
public class GenerateOLCFromFragment extends WorkbenchPlugin {
    private final Workbench wb;

    public GenerateOLCFromFragment(Workbench wb) {
        super(wb);
        this.wb = wb;
    }

    @Override
    public Component getMenuEntry() {
        JMenuItem menuItem = new JMenuItem("Generate OLC");
        menuItem.addActionListener(new ActionListener() {
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
