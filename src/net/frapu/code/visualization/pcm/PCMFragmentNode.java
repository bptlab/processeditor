package net.frapu.code.visualization.pcm;

import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.ProcessUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The FragmentNode represents one PCMFragment inside the PCMScenario. It will be added to the PCMFragmentCollection.
 *
 * @author Stephan Haarmann & Juliane Imme
 * @version 28.10.2014.
 */
public class PCMFragmentNode extends ProcessNode {

    /* this String is the key for the property which will contain the modelID of the referenced PCMFragment */
    public static final String PROP_FRAGMENT_MID = "fragment mid";

    /**
     * Draws the PCMFragmentNode (transpernet Body and Border with black label)
     * @param g Graphics context
     */
    @Override
    protected void paintInternal(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        g2.setStroke(PCMUtils.defaultStroke);
        g2.setColor(Color.black);
        PCMUtils.drawText(g2, getPos().x, getPos().y, getSize().width, getText(), PCMUtils.Orientation.CENTER);
    }

    /**
     * Returns a Rectangle which is greater than the text.
     * @return the outline shape
     */
    @Override
    protected Shape getOutlineShape() {
        Rectangle2D shape = new Rectangle2D.Double();
        shape.setFrame(getPos().x - getSize().width/2, getPos().y - getSize().height/2, getSize().width, getSize().height);
        return shape;
    }
}
