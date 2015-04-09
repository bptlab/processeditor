package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.editors;

import com.inubit.research.util.SwingUtils;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.editors.PropertyEditor;
import net.frapu.code.visualization.editors.PropertyEditorType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stpehan on 08.04.2015.
 */
public class ReferenceEdgesPropertyEditor extends PropertyEditor {

    private JButton referenceEditButton;
    private List<String> edgeUrls;
    private List<Class<? extends ProcessEdge>> acceptedTypes;

    public ReferenceEdgesPropertyEditor(List<Class<? extends ProcessEdge>> acceptedTypes) {
        super();
        this.acceptedTypes = new ArrayList<>(acceptedTypes);
        init();
    }

    private void init() {
        referenceEditButton = new JButton("Edit references");
        edgeUrls = new LinkedList<>();
        referenceEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReferenceOverview view = new ReferenceOverview(acceptedTypes);
                view.setEdgeList(edgeUrls);
                view.initializeButtons();
                SwingUtils.center(view);
                view.setVisible(true);
                if (view.getEdgeList()!=null) {
                    edgeUrls.clear();
                    edgeUrls.addAll(view.getEdgeList());
                }
            }
        });
    }

    @Override
    public Component getComponent() {
        if (referenceEditButton == null) {
            init();
        }
        return referenceEditButton;
    }




    @Override
    public PropertyEditorType getType() {
        return PropertyEditorType.XSDELEMENT;
    }

    @Override
    public void setValue(String value) {
        if (referenceEditButton == null) {
            init();
        }
        edgeUrls.clear();
        for (String edge : value.split(",")) {
            edgeUrls.add(edge);
        }
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        for (String url : edgeUrls) {
            sb.append(url);
            sb.append(',');
        }
        return sb.toString();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setReadOnly(boolean b) {
        // Not possible
    }

    @Override
    public void free() {
        referenceEditButton = null;
    }
}
