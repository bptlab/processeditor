package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc;

import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.BPMNUtils;
import net.frapu.code.visualization.editors.BooleanPropertyEditor;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * This class represents a state of a data object.
 * It is used for object life cycles. There might be {@link StateTransition}
 * between two DataObjectStates.
 */
public class DataObjectState extends ProcessNode {

    public static final String PROP_IS_FINAL = "is_final_state";

    public DataObjectState() {
        super();
        initializeProperties();
        setSize(100, 60);
    }

    private void initializeProperties() {
        setProperty(PROP_IS_FINAL, FALSE);
        setPropertyEditor(PROP_IS_FINAL, new BooleanPropertyEditor());
    }

    @Override
    protected void paintInternal(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Shape outline = getOutlineShape();
        g2.setStroke(OLCUtils.defaultStroke);
        g2.setPaint(getBackground());

        g2.fill(outline);
        g2.setPaint(Color.BLACK);
        g2.draw(outline);
        if (getProperty(PROP_IS_FINAL).equals(TRUE)) {
            g2.draw(getFinalShape());
        }

        // Count text lines
        g2.setFont(OLCUtils.defaultFont);
        g2.setPaint(Color.BLACK);
        BPMNUtils.drawText(g2, getPos().x, getPos().y, getSize().width - 8, getText(), BPMNUtils.Orientation.CENTER);
    }

    @Override
    public String toString() {
        return "OLC Data Object State (" + getText() + ")";
    }


    @Override
    public void setProperty(String key, String value) {
        if (key.equals(PROP_HEIGHT)) {
            try {
                int height = Integer.parseInt(value);
                if (height < 30) value = "30";
            } catch (Exception e) {
                value = "60";
            }
        }
        if (key.equals(PROP_WIDTH)) {
            try {
                int width = Integer.parseInt(value);
                if (width < 30) value = "40";
            } catch (Exception e) {
                value = "100";
            }
        }
        super.setProperty(key, value);
    }

    @Override
    protected Shape getOutlineShape() {
        RoundRectangle2D outline = new RoundRectangle2D.Float(getPos().x - (getSize().width / 2),
                getPos().y - (getSize().height / 2), getSize().width, getSize().height, 15, 15);
        return outline;
    }

    private Shape getFinalShape() {
        RoundRectangle2D outline = new RoundRectangle2D.Float(getPos().x - (getSize().width / 2.2f),
                getPos().y - (getSize().height / 2.2f), getSize().width, getSize().height, 15, 15);
        return outline;
    }
}
