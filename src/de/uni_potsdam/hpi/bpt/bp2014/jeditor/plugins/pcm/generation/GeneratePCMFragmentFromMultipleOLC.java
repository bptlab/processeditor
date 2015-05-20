package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.ModelDescription;
import com.inubit.research.client.ModelDirectory;
import com.inubit.research.client.ModelDirectoryEntry;
import com.inubit.research.gui.Workbench;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.*;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataObject;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Gateway;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.olc.SynchronizedOLCToActivityCentric;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.synchronize.SynchronizedObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.synchronize.SynchronizedOLCAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by Stpehan on 11.05.2015.
 */
public class GeneratePCMFragmentFromMultipleOLC extends GeneratorPlugin {
    protected Collection<ProcessModel> olcs;
    protected Collection<ObjectLifeCycle> selectedOLCs;
    protected Map<INode, ProcessNode> nodesAndNodesConverted;

    public GeneratePCMFragmentFromMultipleOLC(Workbench wb) {
        super(wb);
    }

    @Override
    protected String getName() {
        return "PCM Fragment from OLCs";
    }

    @Override
    protected void initialize() {
        olcs = new HashSet<>();
        nodesAndNodesConverted = new HashMap<>();
    }

    private ProcessNode createProcessNodeForINode(INode inode) {
        ProcessNode node = null;
        if (inode instanceof DataObject) {
            node = new net.frapu.code.visualization.bpmn.DataObject();
            node.setText(((DataObject)inode).getName());
            node.setProperty(net.frapu.code.visualization.bpmn.DataObject.PROP_STATE,
                    ((DataObject)inode).getState().getName());
        } else if (inode instanceof Activity) {
            node = new Task();
            node.setText(((Activity)inode).getName());
        } else if (inode instanceof Gateway) {
            if (((Gateway)inode).getType().equals(Gateway.Type.AND)) {
                node = new ParallelGateway();
            } else {
                node = new ExclusiveGateway();
            }
        } else if (inode instanceof de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Event) {
            if (((de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Event)inode).getType()
                    .equals(de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Event.Type.START)) {
                node = new StartEvent();
            } else {
                node = new EndEvent();
            }
        }
        return node;
    }

    @Override
    protected Collection<? extends ProcessModel> extractModelsFromDirectory(ModelDirectory directory) {
        olcs.addAll(extractModelsFromSubDirectory(directory));
        GeneratorChooseOLCsDialog chooseDialog = new GeneratorChooseOLCsDialog(true);
        chooseDialog.pack();
        chooseDialog.setVisible(true);
        return selectedOLCs;
    }

    protected Collection<? extends ProcessModel> extractModelsFromSubDirectory(ModelDirectory directory) {
        Collection<ObjectLifeCycle> models = new HashSet<>();
        for (ModelDirectoryEntry modelDirectoryEntry : directory.getEntries()) {
            if (modelDirectoryEntry instanceof ModelDirectory) {
                models.addAll((Collection<? extends ObjectLifeCycle>)
                        extractModelsFromSubDirectory((ModelDirectory) modelDirectoryEntry));
            } else if (modelDirectoryEntry instanceof ModelDescription) {
                try {
                    ProcessModel model = ((ModelDescription) modelDirectoryEntry).getHead().getProcessModel();
                    if (model instanceof ObjectLifeCycle) {
                        models.add((ObjectLifeCycle) model);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return models;
    }

    @Override
    protected Collection<? extends IModel> generateModels(Collection<? extends IModel> wrappedModels) {
        SynchronizedObjectLifeCycle sOLC = (SynchronizedObjectLifeCycle) wrappedModels.iterator().next();
        SynchronizedOLCToActivityCentric sOLC2ACPM = new SynchronizedOLCToActivityCentric();
        ActivityCentricProcessModel acpm = sOLC2ACPM.convert(sOLC);
        Collection<ActivityCentricProcessModel> generatedModels = new ArrayList<>(1);
        generatedModels.add(acpm);
        return generatedModels;
    }

    @Override
    protected Collection<? extends IModel> wrapModels(Collection<? extends ProcessModel> models) {
        SynchronizedObjectLifeCycle sOLC = new SynchronizedOLCAdapter(selectedOLCs);
        Collection<SynchronizedObjectLifeCycle> sOLCs = new ArrayList<>(1);
        sOLCs.add(sOLC);
        return sOLCs;
    }

    @Override
    protected Collection<? extends ProcessModel> convertModels(Collection<? extends IModel> generatedModels) {
        Collection<PCMFragment> fragments = new HashSet<>();
        for (IModel acpm : generatedModels) {
            PCMFragment fragment = new PCMFragment();
            for (IEdge edge : ((ActivityCentricProcessModel)acpm).getEdges()) {
                if (!nodesAndNodesConverted.containsKey(edge.getSource())) {
                    ProcessNode processNode = createProcessNodeForINode(edge.getSource());
                    fragment.addNode(processNode);
                    nodesAndNodesConverted.put(edge.getSource(), processNode);
                }
                if (!nodesAndNodesConverted.containsKey(edge.getTarget())) {
                    ProcessNode processNode = createProcessNodeForINode(edge.getTarget());
                    fragment.addNode(processNode);
                    nodesAndNodesConverted.put(edge.getTarget(), processNode);
                }
                if (edge instanceof ControlFlow) {
                    ProcessEdge sequenceFlow = new SequenceFlow(nodesAndNodesConverted.get(edge.getSource()),
                            nodesAndNodesConverted.get(edge.getTarget()));
                    fragment.addEdge(sequenceFlow);
                } else if (edge instanceof DataFlow) {
                    ProcessEdge association = new Association(nodesAndNodesConverted.get(edge.getSource()),
                            nodesAndNodesConverted.get(edge.getTarget()));
                    fragment.addEdge(association);
                }
            }
            fragments.add(fragment);
        }
        return fragments;
    }

    private class GeneratorChooseOLCsDialog extends JDialog {
        private JList olcList;
        private JScrollPane listScrollPane;
        
        public GeneratorChooseOLCsDialog(boolean modal) {
            super(wb, "Choose OLCs for Fragment Generation", modal);
            this.setLayout(new BorderLayout());
            this.add(getContentPanel(), BorderLayout.CENTER);
            this.setPreferredSize(new Dimension(100, 500));
            this.setSize(this.getPreferredSize());
        }

        private Component getContentPanel() {
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout());
            listScrollPane = new JScrollPane();
            olcList = new JList(olcs.toArray());
            listScrollPane.add(olcList);
            contentPanel.add(olcList, BorderLayout.CENTER);
            JButton convertButton = new JButton("Convert");
            final JDialog thisDialog = this;
            convertButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedOLCs = new HashSet<>(
                            olcList.getSelectedValuesList());
                    thisDialog.dispose();
                }
            });
            contentPanel.add(convertButton, BorderLayout.SOUTH);
            return contentPanel;
        }
    }
}
