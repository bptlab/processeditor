package net.frapu.code.visualization.pcm;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import net.frapu.code.visualization.ProcessNode;

/**
 * This class represents the DataObjectNodes of a PCMScenario. Every PCMDataObject node refers to one DataObject inside
 * a PCMFragment.
 *
 * @author Stephan Haarmann & Juliane Imme
 */
public class PCMDataObjectNode extends ProcessNode {

    /**
     * The visual representation is a transparent node, with a black label and without a border
     * @param g the Graphics Context
     */
    @Override
    protected void paintInternal(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        g2.setStroke(PCMUtils.defaultStroke);
        g2.setColor(Color.black);
        PCMUtils.drawText(g2, getPos().x, getPos().y, getSize().width, getText(), PCMUtils.Orientation.CENTER);
    }

    /**
     * Returns the outlineShape. A default Rectangle which is bigger thant the Text.
     * @return the outlineShape
     */
    @Override
    protected Shape getOutlineShape() {
        Rectangle2D shape = new Rectangle2D.Double();
        shape.setFrame(getPos().x - getSize().width/2, getPos().y - getSize().height/2, getSize().width, getSize().height);
        return shape;
    }
}
