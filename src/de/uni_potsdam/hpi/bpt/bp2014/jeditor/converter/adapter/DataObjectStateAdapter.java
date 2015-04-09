package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is an adapter.
 * It adapts {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState}
 * to {@link de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState}.
 * It allows us to use the DataObjectState nodes from the Processeditor
 * as a node of the converter.
 * In addition it is possible to create a new {@link de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState}
 * with the data from the processeditor object.
 */
public class DataObjectStateAdapter extends de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState {

    private DataObjectState dataObjectState;
    private List<IEdge> incomingEdges;
    private List<IEdge> outgoingEdges;

    public DataObjectStateAdapter(DataObjectState dataObjectState) {
        super(dataObjectState.getName());
        this.dataObjectState = dataObjectState;
    }

    @Override
    public void addIncomingEdge(IEdge edge) {
        if (null == incomingEdges) {
            incomingEdges = new LinkedList<>();
        }
        incomingEdges.add(edge);
    }

    @Override
    public void addOutgoingEdge(IEdge edge) {
        if (null == outgoingEdges) {
            outgoingEdges = new LinkedList<>();
        }
        outgoingEdges.add(edge);
    }

    /**
     * Returns a List with all incoming Edges.
     * Be aware that these information can not be extracted from a {@link DataObjectState}
     * Therefore it is necessary to initialize them with the edges.
     * You might want to use {@link OLCAdapter}.
     *
     * @return null          - If the List has not been initialized
     *         List of edges - With all added incoming edges.
     *         The list will be a new list, altering the list will not affect the node.
     */
    @Override
    public List<IEdge> getIncomingEdges() {
        if (null == incomingEdges) {
            return null;
        }
        return new ArrayList<>(incomingEdges);
    }



    /**
     * Returns a list with all outgoing Edges.
     * Be aware that these information can not be extracted from a {@link DataObjectState}
     * Therefore it is necessary to initialize them with the edges.
     * You might want to use {@link OLCAdapter}.
     *
     * @return null          - If the List has not been initialized
     *         List of edges - With all added outgoing edges.
     *         The list will be a new list, altering the list will not affect the node.
     */
    @Override
    public List<IEdge> getOutgoingEdges() {
        if (null == outgoingEdges) {
            return null;
        }
        return new ArrayList<>(outgoingEdges);
    }

    @Override
    public <T extends IEdge> List<T> getOutgoingEdgesOfType(Class t) {
        if (null == outgoingEdges) {
            return null;
        }
        List<T> edges = new ArrayList<>();
        for (IEdge edge : outgoingEdges) {
            if (t.isInstance(edge)) {
                edges.add((T)edge);
            }
        }
        return edges;
    }

    @Override
    public <T extends IEdge> List<T> getIncomingEdgesOfType(Class t) {
        if (null == incomingEdges) {
            return null;
        }
        List<T> edges = new ArrayList<>();
        for (IEdge edge : incomingEdges) {
            if (t.isInstance(edge)) {
                edges.add((T)edge);
            }
        }
        return edges;
    }

    @Override
    public String getName() {
        return dataObjectState.getName();
    }

    @Override
    public void setName(String name) {
        dataObjectState.setText(name);
    }
}