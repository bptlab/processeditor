package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc;

import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessNode;

import java.awt.*;

/**
 * This class represents a StateTransition of an OLC.
 * Therefore it extends the {@link net.frapu.code.visualization.ProcessEdge} class
 * it can be used inside an ProcessModel and will be displayed as an simple arc, with
 * an additional and optional label.
 */
public class StateTransition extends ProcessEdge {

    protected final static Polygon transitionArrow = OLCUtils.standardArrowFilled;

    public StateTransition(ProcessNode source, ProcessNode target) {
        super(source, target);
    }

    @Override
    public Shape getSourceShape() {
        return null;
    }

    @Override
    public Shape getTargetShape() {
        return transitionArrow;
    }

    @Override
    public Stroke getLineStroke() {
        return OLCUtils.defaultStroke;
    }

    @Override
    public boolean isDockingSupported() {
        return true;
    }
}
