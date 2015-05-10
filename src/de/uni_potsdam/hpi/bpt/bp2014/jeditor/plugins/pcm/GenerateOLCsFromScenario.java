package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm;

import com.inubit.research.client.*;
import com.inubit.research.gui.Workbench;
import com.inubit.research.gui.WorkbenchConnectToServerDialog;
import com.inubit.research.gui.plugins.WorkbenchPlugin;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.activity_centric.ActivityCentricToSynchronizedOLC;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.synchronize.SynchronizedObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm.ACPMAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragmentNode;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMScenario;
import net.frapu.code.visualization.Configuration;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Stpehan on 09.05.2015.
 */
public class GenerateOLCsFromScenario extends WorkbenchPlugin {
    private final Workbench wb;
    private Collection<PCMFragment> fragments;
    private Map<String, Collection<ObjectLifeCycle>> olcsPerDataClass;
    private Collection<ObjectLifeCycle> olcs;
    private Collection<String> fragmentIDs;

    public GenerateOLCsFromScenario(Workbench wb) {
        this.wb = wb;
    }

    public Component getMenuEntry() {
        JMenuItem menuItem = new JMenuItem("OLC from Scenario");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fragments = new HashSet<>();
                olcsPerDataClass = new HashMap<>();
                olcs = new HashSet<>();
                fragmentIDs = new HashSet<>();
                ProcessModel model = wb.getSelectedModel();
                if (!(model instanceof PCMScenario)) {
                    System.err.println("Model has to be a PCM Scenario");
                    return;
                }
                for (ProcessNode fragment : model.getNodesByClass(PCMFragmentNode.class)) {
                    fragmentIDs.add(fragment.getProperty(PCMFragmentNode.PROP_FRAGMENT_MID));
                }
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
                    generateObjectLifeCycles();
                    integrateStates();
                    for (ObjectLifeCycle objectLifeCycle : olcs) {
                        ProcessModel olc = new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle();
                        Map<INode, de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState> processedNodes = new HashMap<>();
                        for (IEdge transition : objectLifeCycle.getEdgeOfType(StateTransition.class)) {
                            de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition edge = new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition();
                            if (!processedNodes.containsKey(transition.getSource())) {
                                processedNodes.put(transition.getSource(), new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState());
                            }
                            de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState source = processedNodes.get(transition.getSource());
                            source.setProperty(ProcessNode.PROP_TEXT,
                                    ((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState)transition.getSource()).getName());
                            edge.setSource(source);
                            if (objectLifeCycle.getFinalStates().contains(transition.getSource())) {
                                source.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState.PROP_IS_FINAL, ProcessNode.TRUE);
                            }
                            if (!processedNodes.containsKey(transition.getTarget())) {
                                processedNodes.put(transition.getTarget(), new de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState());
                            }
                            de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState target = processedNodes.get(transition.getTarget());
                            target.setProperty(ProcessNode.PROP_TEXT,
                                    ((de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState)transition.getTarget()).getName());
                            if (objectLifeCycle.getFinalStates().contains(transition.getTarget())) {
                                target.setProperty(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState.PROP_IS_FINAL, ProcessNode.TRUE);
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
                        wb.openNewModel(olc);

                    }
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

    private void integrateStates() {
        for (Map.Entry<String, Collection<ObjectLifeCycle>> olcsAndName : olcsPerDataClass.entrySet()) {
            Map<DataObjectState, Collection<StateTransition>> successors = new HashMap<>();
            Map<String, DataObjectState> states = new HashMap<>();
            for (ObjectLifeCycle olc : olcsAndName.getValue()) {
                for (IEdge transition : olc.getEdgeOfType(StateTransition.class)) {
                    String sourceName = ((DataObjectState)transition.getSource()).getName();
                    String targetName = ((DataObjectState)transition.getTarget()).getName();
                    if (!states.containsKey(sourceName)) {
                        states.put(sourceName, new DataObjectState(sourceName));
                    }
                    if (!states.containsKey(targetName)) {
                        states.put(targetName, new DataObjectState(targetName));
                    }
                    if (!successors.containsKey(states.get(sourceName))) {
                        successors.put(states.get(sourceName), new HashSet<StateTransition>());
                    }
                    if (!successorExists(successors.get(states.get(sourceName)), (StateTransition) transition)) {
                        StateTransition newTransition = new StateTransition(
                                states.get(sourceName),
                                states.get(targetName),
                                ((StateTransition) transition).getLabel());
                        successors.get(states.get(sourceName)).add(newTransition);
                        states.get(sourceName).addOutgoingEdge(newTransition);
                        states.get(targetName).addIncomingEdge(newTransition);
                    }
                }
            }
            ObjectLifeCycle olc = new ObjectLifeCycle(olcsAndName.getKey());
            for (DataObjectState dataObjectState : states.values()) {
                olc.addNode(dataObjectState);
                if (dataObjectState.getName().equals("i")) {
                    olc.setStartNode(dataObjectState);
                }
                if (dataObjectState.getOutgoingEdges().isEmpty()) {
                    olc.addFinalNode(dataObjectState);
                }
            }
            olcs.add(olc);
        }
    }

    private boolean successorExists(Collection<StateTransition> stateTransitions, StateTransition transition) {
        for (StateTransition stateTransition : stateTransitions) {
            if (((DataObjectState)stateTransition.getSource()).getName().equals(
                    ((DataObjectState)transition.getSource()).getName()) &&
                    ((DataObjectState)stateTransition.getTarget()).getName().equals(
                            ((DataObjectState)transition.getTarget()).getName()) &&
                    stateTransition.getLabel().equals(transition.getLabel())) {
                return true;
            }
        }
        return false;
    }

    private void extractModelsFromDirectory(ModelDirectory directory) throws Exception {
        for (ModelDirectoryEntry modelDirectoryEntry : directory.getEntries()) {
            if (modelDirectoryEntry instanceof ModelDirectory) {
                extractModelsFromDirectory((ModelDirectory) modelDirectoryEntry);
            } else if (modelDirectoryEntry instanceof ModelDescription) {
                ProcessModel model = ((ModelDescription) modelDirectoryEntry).getHead().getProcessModel();
                if (model instanceof PCMFragment && fragmentIDs.contains(model.getId())) {
                    fragments.add((PCMFragment) model);
                }
            }
        }
    }

    private void generateObjectLifeCycles() {
        for (PCMFragment fragment : fragments) {
            ACPMAdapter modelAdapted = new ACPMAdapter(fragment);
            ActivityCentricToSynchronizedOLC acpm2solc = new ActivityCentricToSynchronizedOLC();
            SynchronizedObjectLifeCycle solc = (SynchronizedObjectLifeCycle) acpm2solc.convert(modelAdapted);
            for (ObjectLifeCycle objectLifeCycle : solc.getOLCs()) {
                if (!olcsPerDataClass.containsKey(objectLifeCycle.getLabel())) {
                    olcsPerDataClass.put(objectLifeCycle.getLabel(), new HashSet<ObjectLifeCycle>());
                }
                olcsPerDataClass.get(objectLifeCycle.getLabel()).add(objectLifeCycle);
            }
        }
    }
}
