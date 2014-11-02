package net.frapu.code.visualization.pcm;

import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessObject;
import net.frapu.code.visualization.bpmn.StartEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * An {@link net.frapu.code.visualization.ProcessEditor} for the PCM Scenario Model. It has the option to define
 * a Workspace.
 *
 * @author Stephan Haarmann
 * @version 28.10.2014.
 */
public class PCMScenarioEditor extends ProcessEditor {

    private static final long serialVersionUID = -6660643360607804595L;

    public PCMScenarioEditor() {
        super();
        init();
    }

    public PCMScenarioEditor(ProcessModel model) {
        super(model);
        init();
    }

    /**
     * This methods adds a new component to the menu.
     * This component contains options for choosing tasks from existing Fragments.
     * If you want to link tasks you have to use this ContextMenu
     */
    private void addCopyTaskFromOtherFragmentMenu() {
        JMenuItem menuItem = new JMenuItem("Define Workspace");
        menuItem.addActionListener(new ChooseWorkspaceActionListener(this));
        addCustomContextMenuItem(ProcessObject.class, menuItem);
    }

    private void init() {
        addCopyTaskFromOtherFragmentMenu();
        addExportMenu();
    }

    private void addExportMenu() {
        JMenuItem menuItem = new JMenuItem("Export");
        final PCMExporter exporter = new PCMExporter(getSelectedModel());
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Let user choose the file
                try {
                    exporter.serialize(new File("./export.xpdl"), getSelectedModel());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        addCustomContextMenuItem(ProcessObject.class, menuItem);
    }

    private class ChooseWorkspaceActionListener implements ActionListener {
        ProcessEditor processEditor;

        public ChooseWorkspaceActionListener(PCMScenarioEditor pcmScenarioEditor) {
            processEditor = pcmScenarioEditor;
        }

        /**
         * Opens a file Chooser to define the Workspace.
         *
         * @param event
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            JFileChooser workspaceChooser = new JFileChooser("Choose Workspace");
            workspaceChooser.setCurrentDirectory(new File("."));
            workspaceChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // Do not Display the "All File" Filter
            workspaceChooser.setAcceptAllFileFilterUsed(false);
            int result = workspaceChooser.showOpenDialog(processEditor);
            if (JFileChooser.APPROVE_OPTION == result) {
                ((PCMScenario)processEditor.getSelectedModel()).setWorkspace(workspaceChooser.getSelectedFile());
            }
        }
    }
}
