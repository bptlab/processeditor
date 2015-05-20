package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.*;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Event;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Gateway;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.*;
import net.frapu.code.visualization.bpmn.DataObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class is an adapter for {@link BPMNModel} and {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment}.
 * It adapts such a model to {@link ActivityCentricProcessModel}.
 * If necessary it wraps elements like edges and nodes into adapters. The result of this wrapping
 * will be cached.
 */
public class ACPMAdapter extends ActivityCentricProcessModel {
    /**
     * This field holds the model which will be wrapped by this adapter.
     */
    private ProcessModel model;
    /**
     * A Map of all nodes.
     * The ProcessNodes will be mapped to the INodes, the ones wrapped in an adapter.
     */
    private Map<ProcessNode, INode> nodes;
    /**
     * A Collection holding all wrapped edges of the process model.
     */
    private Collection<IEdge> edges;
    /**
     * The states are safed inside this map.
     * The first key is the name of the data object.
     * The second key is the name of the state.
     * This states must be reused inside one olc.
     */
    private Map<String, Map<String, DataObjectState>> stateStore;

    /**
     * Creates a new Adapter for a given model.
     * @param model The Model which will be wrapped.
     *              Should be of type BPMNModel or PCMFragment
     */
    public ACPMAdapter(ProcessModel model) {
        super();
        this.model = model;
        initialize();
    }

    /**
     * Wrapes a node specified by the parameter.
     * Therefore it checks the type and creates a special
     * wrapper.
     * The result will be cached in order to create only one
     * adapter per node.
     * @param raw The node which should be wrapped.
     * @return The adapter wrapping this node, either a cached one or a new one.
     */
    private INode wrapNode(ProcessNode raw) {
        INode node = null;
        if (nodes.containsKey(raw)) {
            return nodes.get(raw);
        }
        if (raw instanceof Task) {
            node = new ActivityAdapter((Task) raw);
        } else if (raw instanceof EndEvent) {
            node = new EventAdapter((net.frapu.code.visualization.bpmn.Event) raw);
            ((Event)node).setType(Event.Type.END);
            addFinalNode(node);
        } else if (raw instanceof StartEvent) {
            node = new EventAdapter((net.frapu.code.visualization.bpmn.Event) raw);
            ((Event)node).setType(Event.Type.START);
            setStartNode(node);
        } else if (raw instanceof ExclusiveGateway) {
            node = new GatewayAdapter((net.frapu.code.visualization.bpmn.Gateway) raw);
            ((Gateway)node).setType(Gateway.Type.XOR);
        } else if (raw instanceof ParallelGateway) {
            node = new GatewayAdapter((net.frapu.code.visualization.bpmn.Gateway) raw);
            ((Gateway)node).setType(Gateway.Type.AND);
        } else if (raw instanceof DataObject) {
            updateStateStore(raw);
            node = new DataObjectAdapter(raw,
                    stateStore.get(raw.getName()));
        }
        nodes.put(raw, node);
        return node;
    }

    /**
     * Updates the state store for a given DataObject.
     * Which means if no corresponding DataObjectStates Objects
     * are store inside the {@link #stateStore} it will be added.
     * @param dataObject The data object whichs state might be added.
     */
    private void updateStateStore(ProcessNode dataObject) {
        if (!stateStore.containsKey(dataObject.getName())) {
            stateStore.put(dataObject.getName(),
                    new HashMap<String, DataObjectState>());
        }
        if (!stateStore.get(dataObject.getName())
                .containsKey(dataObject.getProperty(DataObject.PROP_STATE))) {
            stateStore.get(dataObject.getName())
                    .put(dataObject.getProperty(DataObject.PROP_STATE),
                            new DataObjectState(dataObject.getProperty(DataObject.PROP_STATE)));
        }
    }

    /**
     * Initializes the adapter.
     * Which means all edges and their nodes will be wrapped
     * in a specific adapter. The nodes and edges will be add to the specific list
     * by calling the super functions.
     */
    private void initialize() {
        edges = new HashSet<>();
        nodes = new HashMap<>();
        stateStore = new HashMap<>();
        for (ProcessEdge processEdge : model.getEdges()) {
            if (processEdge instanceof SequenceFlow) {
                INode source = wrapNode(processEdge.getSource());
                INode target = wrapNode(processEdge.getTarget());
                ControlFlow cf = new ControlFlowAdapter(source, target, processEdge);
                edges.add(cf);
                source.addOutgoingEdge(cf);
                target.addIncomingEdge(cf);
            } else if (processEdge instanceof Association) {
                INode source = wrapNode(processEdge.getSource());
                INode target = wrapNode(processEdge.getTarget());
                DataFlow df;
                if (source instanceof de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity) {
                    df = new DataFlowAdapter((de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity)source, (de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataObject)target, processEdge);
                } else {
                    df = new DataFlowAdapter((de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataObject)source, (de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity)target, processEdge);
                }
                edges.add(df);
                source.addOutgoingEdge(df);
                target.addIncomingEdge(df);
            }
        }
        for (INode node : nodes.values()) {
            super.addNode(node);
        }
    }
}
