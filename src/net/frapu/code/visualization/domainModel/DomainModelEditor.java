package net.frapu.code.visualization.domainModel;

import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.ProcessModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class extends the process editor.
 * It is used to model domain Models.
 * If you do not want a attribute Editor,
 * you do not need this Editor.
 */
public class DomainModelEditor extends ProcessEditor {
    private static final long serialVersionUID = -6660643360483752373L;

    /**
     * Create a new DomainModelEditor for an empty model.
     */
    public DomainModelEditor() {
        super();
        init();
    }

    /**
     * Create a new DomainModelEditor for an existing model.
     * @param model The given Model
     */
    public DomainModelEditor(ProcessModel model) {
        super(model);
        init();
    }

    /**
     * This Method initializes the DomainModelEditor.
     * It adds entries for all the available Plugins.
     */
    private void init() {
        addEditAttributesMenu();
    }

    /**
     * This methods adds a new Menu-item to the Context-Menu.
     * It allows to open and Edit the Attributes,
     * by doing a right click on the Task.
     */
    private void addEditAttributesMenu() {
        JMenuItem editAttributesMenu = new JMenuItem("Edit attributes");
        final ProcessEditor thisEditor = this;
        editAttributesMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new AttributeEditor(thisEditor);
                dialog.setVisible(true);
            }
        });
        addCustomContextMenuItem(DomainClass.class, editAttributesMenu);
    }
}
