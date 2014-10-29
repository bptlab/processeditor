package net.frapu.code.visualization.pcm;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import net.frapu.code.visualization.ProcessNode;

/**
 * Created by Jule on 29.10.2014.
 */
public class PCMDataObjectNode extends ProcessNode {

    @Override
    protected void paintInternal(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //g2.setPaint(getBackground());
        //g2.fill(getOutlineShape());
        g2.setStroke(PCMUtils.defaultStroke);
        g2.setColor(Color.black);
        PCMUtils.drawText(g2, getPos().x, getPos().y, getSize().width, getText(), PCMUtils.Orientation.CENTER);
    }

    @Override
    protected Shape getOutlineShape() {
        Rectangle2D shape = new Rectangle2D.Double();
        shape.setFrame(getPos().x - getSize().width/2, getPos().y - getSize().height/2, getSize().width, getSize().height);
        return shape;
    }
}
