package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.DataObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class can be used as an adapter.
 * It adapts the {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle} to a model compatible
 * to the converter. Therefore, it extends {@link ObjectLifeCycle} and overrides all methods specified in the
 * {@link IModel} interface.
 * It provides the possibility to convert an {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle}
 * into an {@link de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle}.
 */
public class OLCAdapter extends de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle {
    /**
     * The ObjectLifeCycle object which will be wrapped
     * by this adapter.
     */
    private ObjectLifeCycle olc;

    /**
     * Creates a new adapter for a given ObjectLifeCycle.
     * The nodes and edges will be wrapped automatically.
     * @param olc The ObjectLifeCycle which will be wrapped.
     */
    public OLCAdapter(ObjectLifeCycle olc) {
        super(olc.getProcessName());
        this.olc = olc;

        initializeNodes();
    }

    /**
     * This method initializes the Nodes and edges.
     * It will wrap each DataObjectState node and each State Transition
     * in order to create a model for the transformation operations.
     */
    private void initializeNodes() {
        Map<ProcessNode, DataObjectStateAdapter> nodesAndTheirAdapters = new HashMap<>();
        for (ProcessNode processNode : olc.getNodes()) {
            nodesAndTheirAdapters.put(processNode,
                    new DataObjectStateAdapter((DataObjectState) processNode));
            addNode(nodesAndTheirAdapters.get(processNode));
            if (processNode.getProperty(DataObjectState.PROP_IS_FINAL)
                    .equals(DataObjectState.TRUE)) {
                addFinalNode(nodesAndTheirAdapters.get(processNode));
            }
            if (processNode.getProperty(DataObjectState.PROP_IS_START)
                    .equals(DataObjectState.TRUE)) {
                setStartNode(nodesAndTheirAdapters.get(processNode));
            }
        }
        for (ProcessEdge processEdge : olc.getEdges()) {
            DataObjectStateAdapter source = nodesAndTheirAdapters.get(processEdge.getSource());
            DataObjectStateAdapter target = nodesAndTheirAdapters.get(processEdge.getTarget());
            StateTransitionAdapter adaptedEdge = new StateTransitionAdapter((StateTransition) processEdge);
            adaptedEdge.setSource(source);
            adaptedEdge.setTarget(target);
            source.addOutgoingEdge(adaptedEdge);
            target.addIncomingEdge(adaptedEdge);
        }
    }
}