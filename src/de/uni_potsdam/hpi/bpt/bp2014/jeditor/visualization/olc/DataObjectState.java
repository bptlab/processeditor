package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc;

import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.editors.BooleanPropertyEditor;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * This class represents a state of a data object.
 * It is used for object life cycles. There might be {@link StateTransition}
 * between two DataObjectStates.
 */
public class DataObjectState extends ProcessNode {

    public static final String PROP_IS_FINAL = "is_final_state";
    public static final String PROP_IS_START = "is start state";

    public DataObjectState() {
        super();
        initializeProperties();
        setSize(60, 60);
    }

    private void initializeProperties() {
        setProperty(PROP_IS_FINAL, FALSE);
        setPropertyEditor(PROP_IS_FINAL, new BooleanPropertyEditor());
        setProperty(PROP_IS_START, FALSE);
        setPropertyEditor(PROP_IS_START, new BooleanPropertyEditor());
    }

    @Override
    protected void paintInternal(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Shape outline = getOutlineShape();
        if (getProperty(PROP_IS_FINAL).equals(TRUE)) {
            g2.setStroke(OLCUtils.doubleLinedStroke);
        } else {
            g2.setStroke(OLCUtils.defaultStroke);
        }
        g2.setPaint(getBackground());

        g2.fill(outline);
        g2.setPaint(Color.BLACK);
        g2.draw(outline);
        if (getProperty(PROP_IS_START).equals(TRUE)) {
            g2.fill(new Ellipse2D.Double(
                    getPos().x - (Math.cos(Math.toRadians(45.)) * (getSize().width / 2)) - 10,
                    getPos().y - (Math.sin(Math.toRadians(45.)) * (getSize().width / 2)) - 10,
                    15,
                    15));
        }

        // Count text lines
        g2.setFont(OLCUtils.defaultFont);
        g2.setPaint(Color.BLACK);
        OLCUtils.drawText(g2, getPos().x, getPos().y, getSize().width - 8, getText(), OLCUtils.Orientation.CENTER);
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
                if (width < 30) value = "30";
            } catch (Exception e) {
                value = "60";
            }
        }
        super.setProperty(key, value);
    }

    @Override
    protected Shape getOutlineShape() {
        Ellipse2D outline = new Ellipse2D.Double(getPos().x - (getSize().width / 2),
                getPos().y - (getSize().width / 2), getSize().width, getSize().width);
        return outline;
    }
}
