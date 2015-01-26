package net.frapu.code.visualization.pcm;

import net.frapu.code.visualization.Cluster;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.Set;

/**
 * A Cluster for a number of PCMFragmentNodes. There should be only one PCMFragmentCollection node for each Scenario.
 *
 * @author Stephan Haarmann & Juliane Imme
 * @version 28.10.2014.
 */
public class PCMFragmentCollection extends Cluster {

    /**
     * Creates a new PCMFragmentCollection with the default size (200px x 500px). If you change the default size,
     * you might want to change the default size of PCMDataObjectNode as well.
     */
    public PCMFragmentCollection() {
        super();
        setSize(200, 500);
    }

    /**
     * Draws the Node. It as a white fill color, a black border and rounded corners. The text will be placed above the
     * node
     * @param g the graphics Context
     */
    @Override
    protected void paintInternal(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        Shape outline = getOutlineShape();
        g2.setPaint(getBackground());
        g2.fill(outline);
        g2.setStroke(PCMUtils.defaultStroke);
        g2.setColor(Color.BLACK);
        g2.draw(outline);
        PCMUtils.drawText(g2, getPos().x - getSize().width / 2,
                getPos().y - getSize().height / 2,
                getSize().width, getText(), PCMUtils.Orientation.TOP);
    }

    /**
     * Returns the outline shape. A rounded Rectangle
     * @return outline shape
     */
    @Override
    protected Shape getOutlineShape() {
        RoundRectangle2D outline = new RoundRectangle2D.Double();
        outline.setRoundRect(getPos().x - getSize().width / 2,
                getPos().y - getSize().height / 2,
                getSize().width, getSize().height, 10, 10);
        return outline;
    }

    /**
     * The PCMFragmentCollection should be neither target nor source of a ´n edge. Hence it is has no
     * defaultConnectionPoints
     * @return an Empty Set of Points
     */
    @Override
    public Set<Point> getDefaultConnectionPoints() {
        Set<Point> connectionPoints = new HashSet<Point>();
        return connectionPoints;
    }
}
