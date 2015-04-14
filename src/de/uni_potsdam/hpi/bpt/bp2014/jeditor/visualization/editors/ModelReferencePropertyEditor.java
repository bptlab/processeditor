package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.editors;

import com.inubit.research.gui.WorkbenchFetchModelDialog;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessObject;
import net.frapu.code.visualization.SwingUtils;
import net.frapu.code.visualization.editors.PropertyEditor;
import net.frapu.code.visualization.editors.PropertyEditorType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 *
 */
public class ModelReferencePropertyEditor extends PropertyEditor {
    private final Class<? extends ProcessModel> processModelRestriction;
    private JTextField referenceTextField;
    private JButton referencePickButton;
    private JPanel defaultEditor;

    public ModelReferencePropertyEditor(Class<? extends ProcessModel> processModelRestriction) {
        super();
        this.processModelRestriction = processModelRestriction;
        init();
    }

    private void init() {
        referenceTextField = new JTextField();
        referencePickButton = new JButton("...");
        referencePickButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                WorkbenchFetchModelDialog fmd = new WorkbenchFetchModelDialog(null, true);
                fmd.setOnlyHeadVersions(true);
                SwingUtils.center(fmd);
                fmd.setVisible(true);
                if (null != processModelRestriction &&
                        !processModelRestriction.isInstance(fmd.getSelectedProcessModel())) {
                    JOptionPane.showMessageDialog(defaultEditor,
                            "The selected Model is not allowed. Please select a model of type " +
                                    processModelRestriction, "Message",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    URI uri = fmd.getSelectedProcessModelUri();
                    if (uri != null) {
                        referenceTextField.setText(uri.toASCIIString());
                    }
                }
            }
        });

        defaultEditor = new JPanel();
        defaultEditor.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 95;
        gbc.weighty = 100;
        gbc.gridx = 0;
        gbc.gridy = 0;
        defaultEditor.add(referenceTextField, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        defaultEditor.add(referencePickButton, gbc);
        referenceTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessObject po = getProcessObject();
                if (po != null) {
                    po.setProperty(getPropertyKey(), getValue());
                }
            }
        });
    }

    @Override
    public Component getComponent() {
        if (defaultEditor == null) {
            init();
        }
        return defaultEditor;

    }

    @Override
    public String getValue() {
        if (defaultEditor == null) {
            init();
        }
        return referenceTextField.getText();

    }

    @Override
    public void setValue(String value) {
        if (defaultEditor == null) {
            init();
        }
        // Set color in TextField
        referenceTextField.setText(value);

    }

    @Override
    public PropertyEditorType getType() {
        return PropertyEditorType.XSDELEMENT;
    }

    @Override
    public boolean isReadOnly() {
        if (defaultEditor == null) {
            init();
        }
        return !defaultEditor.isEnabled();
    }

    @Override
    public void setReadOnly(boolean b) {
        if (defaultEditor == null) {
            init();
        }
        defaultEditor.setEnabled(!b);
        referencePickButton.setEnabled(!b);
        referenceTextField.setEnabled(!b);
    }

    @Override
    public void free() {
        defaultEditor = null;
    }
}
