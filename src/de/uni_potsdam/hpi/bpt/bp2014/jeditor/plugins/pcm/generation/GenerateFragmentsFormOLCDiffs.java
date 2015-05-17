package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.*;
import com.inubit.research.gui.Workbench;
import com.inubit.research.gui.WorkbenchConnectToServerDialog;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.ActivityCentricProcessModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.ControlFlow;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataFlow;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.olc.SynchronizedOLCToActivityCentric;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.synchronize.SynchronizedObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.SynchronizedOLCAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment;
import net.frapu.code.visualization.Configuration;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.Association;
import net.frapu.code.visualization.bpmn.SequenceFlow;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Stpehan on 17.05.2015.
 */
public class GenerateFragmentsFormOLCDiffs extends GeneratePCMFragmentFromMultipleOLC {
    public GenerateFragmentsFormOLCDiffs(Workbench wb) {
        super(wb);
    }

    @Override
    public Component getMenuEntry() {
        JMenuItem item = new JMenuItem("Create Fragment for Diff");
        item.addActionListener(new ActionListener() {
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
        return item;
    }
}
