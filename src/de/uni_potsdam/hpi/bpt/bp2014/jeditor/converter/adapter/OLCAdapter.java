package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle;
import net.frapu.code.visualization.ProcessNode;

import java.util.LinkedList;
import java.util.List;

/**
 * This class can be used as an adapter.
 * It adapts the {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle} to a model compatible
 * to the converter. Therefore, it extends {@link ObjectLifeCycle} and overrides all methods specified in the
 * {@link IModel} interface.
 * It provides the possibility to convert an {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle}
 * into an {@link de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle}.
 */
public class OLCAdapter extends de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle {
    private ObjectLifeCycle olc;
    private List<INode> nodes;
    private List<INode> finalNodes;

    public OLCAdapter(ObjectLifeCycle olc) {
        super(olc.getProcessName());
        this.olc = olc;
    }

    @Override
    public List<INode> getNodes() {
        if (null == nodes) {
            initializeNodes();
        }
        List<INode> result = new LinkedList<>(nodes);
        result.addAll(super.getNodes());
        return result;
    }

    private void initializeNodes() {
        nodes = new LinkedList<>();
        finalNodes = new LinkedList<>();
        for (ProcessNode stateNode : olc.getNodesByClass(DataObjectState.class)) {
            DataObjectStateAdapter node = new DataObjectStateAdapter((DataObjectState) stateNode);
            nodes.add(node);
            if (stateNode.getProperty(DataObjectState.PROP_IS_FINAL)
                    .equals(DataObjectState.TRUE)) {
                finalNodes.add(node);
            }
        }
    }

    @Override
    public <T extends INode> List<T> getNodesOfClass(Class t) {
        List<T> result = new LinkedList<>();
        if (t.isAssignableFrom(de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState.class)) {
                result.addAll((List<T>)getNodes());
        }
        result.addAll((List<T>) super.getNodesOfClass(t));
        return result;
    }

    @Override
    public List<INode> getFinalStates() {
        if (null == nodes) {
            initializeNodes();
        }
        List<INode> result = new LinkedList<>();
        result.addAll(finalNodes);
        result.addAll(super.getFinalStates());
        return result;
    }

    @Override
    public <T extends INode> List<T> getFinalNodesOfClass(Class t) {
        if (null == nodes) {
            initializeNodes();
        }
        List<T> result = new LinkedList<>();
        if (t.isAssignableFrom(de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState.class)) {
            result.addAll((List<T>)finalNodes);
        }
        return result;
    }
}