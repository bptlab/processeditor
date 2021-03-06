package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.editors.ModelReferencePropertyEditor;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.domainModel.DomainClass;
import net.frapu.code.visualization.editors.ReferenceChooserRestriction;
import net.frapu.code.visualization.editors.ReferencePropertyEditor;

/**
 * This class represents the DataObjectNodes of a PCMScenario. Every PCMDataObject node refers to one DataObject inside
 * a PCMFragment.
 *
 * @author Stephan Haarmann & Juliane Imme
 */
public class PCMDataObjectNode extends ProcessNode {
    public static final String PROP_CLASS = "Data class";
    public static final String PROP_OLC = "Object Lfie Cycle";

    public PCMDataObjectNode() {
        super();
        init();
    }

    private void init() {
        setProperty(PROP_CLASS, "");
        java.util.List<Class> nodeRestriction = new LinkedList<>();
        nodeRestriction.add(DomainClass.class);
        java.util.List<String> sterotypeRestrictions = new LinkedList<>();
        sterotypeRestrictions.add("");
        sterotypeRestrictions.add(DomainClass.STEREOTPYE_ROOT_INSTANCE);
        setPropertyEditor(PROP_CLASS, new ReferencePropertyEditor(
                new ReferenceChooserRestriction(sterotypeRestrictions, nodeRestriction)));

        setProperty(PROP_OLC, "");
        setPropertyEditor(PROP_OLC, new ModelReferencePropertyEditor(ObjectLifeCycle.class));
    }

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
