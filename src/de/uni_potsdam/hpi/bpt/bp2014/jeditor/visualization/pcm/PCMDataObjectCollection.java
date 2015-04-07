package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import net.frapu.code.visualization.Cluster;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

/**
 * The PCMDataobjectCollection inherits from Cluster. It is a node represented by a green Area with a black border and
 * rounded corners. It is part of a PCMScenario. If one or more Fragments of a Scenario have DataObjects, they will be
 * listet (PCMDataObjectNode) inside this cluster.
 *
 * @author Stephan Haarmann & Juliane Imme
 * @version 29.10.2014.
 */
public class PCMDataObjectCollection extends Cluster{

    /**
     * Create a new PCMObjectCollection with defaultSize (200px x 500px)
     */
    public PCMDataObjectCollection() {
        super();
        setSize(200, 500);
    }

    /**
     * Paints the internals:
     *     Green fill Color
     *     Rounded black border
     *     Text above the Node
     * @param g
     */
    @Override
    protected void paintInternal(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        Shape outline = getOutlineShape();
        g2.setPaint(Color.GREEN);
        g2.fill(outline);
        g2.setStroke(PCMUtils.defaultStroke);
        g2.setColor(Color.BLACK);
        g2.draw(outline);
        PCMUtils.drawText(g2, getPos().x - getSize().width / 2,
                getPos().y - getSize().height / 2,
                getSize().width, getText(), PCMUtils.Orientation.TOP);
    }

    /**
     * Returns the OutlineShape
     * @return shape - The outline (Rectangle2D)
     */
    @Override
    protected Shape getOutlineShape() {
        Rectangle2D outline = new Rectangle2D.Double();
        outline.setRect(getPos().x - getSize().width / 2,
                getPos().y - getSize().height / 2,
                getSize().width, getSize().height);
        return outline;
    }

    /**
     * The PCMDataObjectCollection should be neither target nor source of an edge. Therefore it has no connectionPoints
     * @return empty HashSet of return values
     */
    @Override
    public Set<Point> getDefaultConnectionPoints() {
        Set<Point> connectionPoints = new HashSet<Point>();
        return connectionPoints;
    }
}
