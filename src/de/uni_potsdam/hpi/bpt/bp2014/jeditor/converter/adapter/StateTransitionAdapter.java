package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition;

/**
 * This class adapts a {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition} to a
 * {@link StateTransition}.
 * Therefore, it extends the StateTransition class. In addition it overrides all methods specified by the
 * IEdge interface.
 */
public class StateTransitionAdapter extends StateTransition {

    private final de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition stateTransition;
    private INode source;
    private INode target;

    public StateTransitionAdapter(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition stateTransition) {
        super();
        this.stateTransition = stateTransition;
    }

    @Override
    public INode getSource() {
        return source;
    }

    @Override
    public void setSource(INode source) {
        this.source = source;
    }

    @Override
    public INode getTarget() {
        return target;
    }

    @Override
    public void setTarget(INode target) {
        this.target = target;
    }

    @Override
    public String getLabel() {
        return stateTransition.getLabel();
    }

    @Override
    public void setLabel(String label) {
        stateTransition.setLabel(label);
    }
}
