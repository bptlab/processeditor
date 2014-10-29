package net.frapu.code.visualization.pcm;

import net.frapu.code.visualization.Cluster;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jule on 29.10.2014.
 */
public class PCMDataObjectCollection extends Cluster{

    public PCMDataObjectCollection() {
        super();
        setSize(200, 500);
    }

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

    @Override
    protected Shape getOutlineShape() {
        Rectangle2D outline = new Rectangle2D.Double();
        outline.setRect(getPos().x - getSize().width / 2,
                getPos().y - getSize().height / 2,
                getSize().width, getSize().height);
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
