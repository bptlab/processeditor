package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class describes a PCM Fragment Model.
 * Currently a PCM Fragment is a small BPMN-Model using
 * the following subset of the BPMN notation:
 * <ul>
 *     <li>Start Event</li>
 *     <li>Task</li>
 *     <li>Gateways (XOR, AND)</li>
 *     <li>Data Objects with state</li>
 *     <li>Sequence flow</li>
 *     <li>Associations</li>
 *     <li>End Event</li>
 * </ul>
 *
 * The PCMFragment is a subclass of ProcessModel
 *
 * For more information about PCM see
 * <a href="http://bpt.hpi.uni-potsdam.de/pub/Public/AndreasMeyer/Implementation_Framework_for_Production_Case_Management_--_Modeling_and_Execution.pdf">
 *     Implementation Framework for Productive Case Management: Modeling and Execution - Meyer, Andreas; Herzberg, Nico; Puhlmann, Frank; Weske, Mathias:</a>
 *
 * @see net.frapu.code.visualization.bpmn.BPMNModel
 * @see net.frapu.code.visualization.ProcessModel
 * @version 28.10.2014.
 * @author Juliane Imme, Stephan Haarmann
 */
public class PCMFragment extends ProcessModel {

    private Map<ProcessNode, Map<ProcessModel, ProcessNode>> references;

    public PCMFragment() {
        super();
        references = new LinkedHashMap<ProcessNode, Map<ProcessModel, ProcessNode>>();
        processUtils = new PCMUtils();
    }

    @Override
    public String getDescription() {
        return "PCM Fragment Model";
    }

    @Override
    public List<Class<? extends ProcessNode>> getSupportedNodeClasses() {
        List<Class<? extends ProcessNode>> nodes = new LinkedList<Class<? extends ProcessNode>>();
        nodes.add(Task.class);
        nodes.add(DataObject.class);
        nodes.add(StartEvent.class);
        nodes.add(EndEvent.class);
        nodes.add(Gateway.class);
        return nodes;
    }

    @Override
    public List<Class<? extends ProcessEdge>> getSupportedEdgeClasses() {
        List<Class<? extends ProcessEdge>> edges = new LinkedList<Class<? extends ProcessEdge>>();
        edges.add(SequenceFlow.class);
        edges.add(Association.class);
        return edges;
    }

    public void addReference(ProcessNode node, Map<ProcessModel, ProcessNode> reference) {
        references.put(node, reference);
    }

    public Map<ProcessNode, Map<ProcessModel, ProcessNode>> getReferences() {
        return references;
    }
}
