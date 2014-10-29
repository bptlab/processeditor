package net.frapu.code.visualization.pcm;

import net.frapu.code.visualization.Cluster;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.Set;

/**
 * A Cluster for a number of PCMFractalNodes. There should be only one PCMFragmentCollection node for each Scenario.
 *
 * @author Stephan Haarmann
 * @version 28.10.2014.
 */
public class PCMFragmentCollection extends Cluster {

    public PCMFragmentCollection() {
        super();
        setSize(200, 500);
    }

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

    @Override
    protected Shape getOutlineShape() {
        RoundRectangle2D outline = new RoundRectangle2D.Double();
        outline.setRoundRect(getPos().x - getSize().width / 2,
                getPos().y - getSize().height / 2,
                getSize().width, getSize().height, 10, 10);
        return outline;
    }

    @Override
    public Set<Point> getDefaultConnectionPoints() {
        Set<Point> connectionPoints = new HashSet<Point>();
       /* connectionPoints.add(new Point(getPos().x, getPos().y - getSize().height));
        connectionPoints.add(new Point(getPos().x, getPos().y + getSize().height));
        connectionPoints.add(new Point(getPos().x - getSize().width, getPos().y));
        connectionPoints.add(new Point(getPos().x + getSize().width, getPos().y));*/
        return connectionPoints;
    }
}
