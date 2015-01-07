package net.frapu.code.visualization.pcm;

import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.ProcessObject;
import net.frapu.code.visualization.bpmn.StartEvent;
import net.frapu.code.visualization.bpmn.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

/**
 * An {@link net.frapu.code.visualization.ProcessEditor} for the PCM Scenario Model. It has the option to define
 * a Workspace.
 *
 * @author Stephan Haarmann, Juliane Imme
 * @version 28.10.2014.
 */
public class PCMScenarioEditor extends ProcessEditor {

    private static final long serialVersionUID = -6660643360607804595L;

    /**
     * Creates a new empty PCMScenarioEditor
     */
    public PCMScenarioEditor() {
        super();
        init();
    }

    /**
     * Creates a new editor and loads a specific Model
     * @param model the model to be loaded.
     */
    public PCMScenarioEditor(ProcessModel model) {
        super(model);
        init();
    }

    /**
     * This methods adds a new component to the menu.
     * This component contains options for choosing tasks from existing Fragments.
     * If you want to link tasks you have to use this ContextMenu
     */
    @Deprecated
    private void addCopyTaskFromOtherFragmentMenu() {
        JMenuItem menuItem = new JMenuItem("Add Fragments");
        //menuItem.addActionListener(new ChooseWorkspaceActionListener(this));
        menuItem.addActionListener(new DefineScenarioFromServerActionListener(this));
        addCustomContextMenuItem(ProcessObject.class, menuItem);
    }

    private void init() {
    //    addCopyTaskFromOtherFragmentMenu();
    //    addExportMenu();
    }

    @Deprecated
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

    /**
     * The Action listener for the Choose Workspace Action (Menu Item)
     */
    @Deprecated
    private class ChooseWorkspaceActionListener implements ActionListener {
        ProcessEditor processEditor;

        /**
         * Creates a new Action Listener for the Choose Workspace Action.
         * @param pcmScenarioEditor the ProcessEditor of the current PCMScenario
         */
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
