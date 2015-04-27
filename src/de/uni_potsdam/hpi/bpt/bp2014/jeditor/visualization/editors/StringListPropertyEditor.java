package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.editors;

import com.inubit.research.util.SwingUtils;
import net.frapu.code.visualization.editors.PropertyEditor;
import net.frapu.code.visualization.editors.PropertyEditorType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stpehan on 22.04.2015.
 */
public class StringListPropertyEditor extends PropertyEditor {


    private JButton editListButton;
    private List<String> values;
    private String separator;

    public StringListPropertyEditor(String separator) {
        super();
        this.separator = separator != null ? separator : ",";
        init();
    }

    private void init() {
        editListButton = new JButton("Edit list");
        values = new LinkedList<>();
        editListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringListView view = new StringListView();
                view.setStringList(values);
                view.initializeButtons();
                SwingUtils.center(view);
                view.pack();
                view.setVisible(true);
                if (view.getStringList() != null) {
                    values.clear();
                    values.addAll(view.getStringList());
                }
            }
        });
    }

    @Override
    public Component getComponent() {
        if (editListButton == null) {
            init();
        }
        return editListButton;
    }




    @Override
    public PropertyEditorType getType() {
        return PropertyEditorType.XSDELEMENT;
    }

    @Override
    public void setValue(String valueList) {
        if (editListButton == null) {
            init();
        }
        values.clear();
        for (String value : valueList.split(separator)) {
            values.add(value);
        }
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        for (String url : values) {
            sb.append(url);
            sb.append(separator);
        }
        if (sb.toString().isEmpty()) {
            return "";
        } else {
            return sb.toString().substring(0, sb.length() - separator.length());
        }
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
        editListButton = null;
    }

}
