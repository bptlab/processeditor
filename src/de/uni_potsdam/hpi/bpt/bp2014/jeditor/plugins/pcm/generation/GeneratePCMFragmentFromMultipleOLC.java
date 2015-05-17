package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.*;
import com.inubit.research.gui.Workbench;
import com.inubit.research.gui.WorkbenchConnectToServerDialog;
import com.inubit.research.gui.plugins.WorkbenchPlugin;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.*;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataObject;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Gateway;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.olc.SynchronizedOLCToActivityCentric;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.synchronize.SynchronizedObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.SynchronizedOLCAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment;
import net.frapu.code.visualization.Configuration;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.*;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Stpehan on 11.05.2015.
 */
public class GeneratePCMFragmentFromMultipleOLC extends WorkbenchPlugin {
    protected final Workbench wb;
    protected Collection<ObjectLifeCycle> olcs;
    protected Collection<ObjectLifeCycle> selectedOLCs;
    protected Map<INode, ProcessNode> nodesAndNodesConverted;

    public GeneratePCMFragmentFromMultipleOLC(Workbench wb) {
        super(wb);
        this.wb = wb;
    }

    @Override
    public Component getMenuEntry() {
        JMenuItem menuItem = new JMenuItem("PCM Fragment from OLCs");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                olcs = new HashSet<>();
                WorkbenchConnectToServerDialog connectDialog = new WorkbenchConnectToServerDialog(wb, wb, false);
                connectDialog.pack();
                connectDialog.setVisible(true);
                Configuration conf = Configuration.getInstance();
                try {
                    ModelServer server = new ModelServer(new URI(conf.getProperty(WorkbenchConnectToServerDialog.CONF_SERVER_URI)), "/",
                            new UserCredentials(conf.getProperty(WorkbenchConnectToServerDialog.CONF_SERVER_URI),
                                    conf.getProperty(WorkbenchConnectToServerDialog.CONF_SERVER_USER),
                                    conf.getProperty(WorkbenchConnectToServerDialog.CONF_SERVER_PASSWORD)));
                    ModelDirectory directory = server.getDirectory();
                    extractModelsFromDirectory(directory);
                    GeneratorChooseOLCsDialog chooseDialog = new GeneratorChooseOLCsDialog(true);
                    chooseDialog.pack();
                    chooseDialog.setVisible(true);
                    SynchronizedObjectLifeCycle sOLC = new SynchronizedOLCAdapter(selectedOLCs);
                    SynchronizedOLCToActivityCentric sOLC2ACPM = new SynchronizedOLCToActivityCentric();
                    ActivityCentricProcessModel acpm = sOLC2ACPM.convert(sOLC);
                    nodesAndNodesConverted = new HashMap<>();
                    PCMFragment fragment = new PCMFragment();
                    for (IEdge edge : acpm.getEdges()) {
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
                    wb.openNewModel(fragment);
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                } catch (InvalidUserCredentialsException e1) {
                    e1.printStackTrace();
                } catch (XMLHttpRequestException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (XPathExpressionException e1) {
                    e1.printStackTrace();
                } catch (ParserConfigurationException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        return menuItem;
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

    private void extractModelsFromDirectory(ModelDirectory directory) throws Exception {
        for (ModelDirectoryEntry modelDirectoryEntry : directory.getEntries()) {
            if (modelDirectoryEntry instanceof ModelDirectory) {
                extractModelsFromDirectory((ModelDirectory) modelDirectoryEntry);
            } else if (modelDirectoryEntry instanceof ModelDescription) {
                ProcessModel model = ((ModelDescription) modelDirectoryEntry).getHead().getProcessModel();
                if (model instanceof ObjectLifeCycle) {
                    olcs.add((ObjectLifeCycle) model);
                }
            }
        }
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
