package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc;

import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.editors.ReferenceEdgesPropertyEditor;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.editors.StringListPropertyEditor;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.editors.BooleanPropertyEditor;
import net.frapu.code.visualization.editors.PropertyEditor;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class represents a StateTransition of an OLC.
 * Therefore it extends the {@link net.frapu.code.visualization.ProcessEdge} class
 * it can be used inside an ProcessModel and will be displayed as an simple arc, with
 * an additional and optional label.
 */
public class StateTransition extends ProcessEdge {
    public static final String PROP_SYNCHRONIZED = "synchronized_with";
    public static final String PROP_IS_BATCH = "is batch activity";
    public static final String PROP_CONNECTED = "connected transitions";
    public static final String PROP_GROUPING_CHARACTERISTICS = "grouping characteristics";

    protected final static Polygon transitionArrow = OLCUtils.standardArrowFilled;

    public StateTransition(ProcessNode source, ProcessNode target) {
        super(source, target);
        init();
    }

    public StateTransition() {
        super();
        init();
    }

    private void init() {
        java.util.List<Class<? extends ProcessEdge>> acceptedEdges =
                new ArrayList<>(1);
        acceptedEdges.add(StateTransition.class);
        setProperty(PROP_SYNCHRONIZED, "");
        setPropertyEditor(PROP_SYNCHRONIZED,
                new ReferenceEdgesPropertyEditor(acceptedEdges));
        setProperty(PROP_IS_BATCH, FALSE);
        setPropertyEditor(PROP_IS_BATCH, new BooleanPropertyEditor());
        setProperty(PROP_CONNECTED, "");
        setPropertyEditor(PROP_CONNECTED, new ReferenceEdgesPropertyEditor(acceptedEdges));
        setProperty(PROP_GROUPING_CHARACTERISTICS, "");
        setPropertyEditor(PROP_GROUPING_CHARACTERISTICS, new StringListPropertyEditor(","));
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
        if (getProperty(PROP_IS_BATCH).equals(FALSE)) {
            return OLCUtils.defaultStroke;
        } else {
            return OLCUtils.boldStroke;
        }
    }

    @Override
    public boolean isDockingSupported() {
        return true;
    }
}
