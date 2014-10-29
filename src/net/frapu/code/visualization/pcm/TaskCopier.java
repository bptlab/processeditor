package net.frapu.code.visualization.pcm;

import net.frapu.code.converter.ConverterHelper;
import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.Task;
import net.frapu.code.visualization.epk.*;
import net.frapu.code.visualization.processmap.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.lang.System;
import java.util.*;
import java.util.List;

/**
 * This class is responsible to create copies and references to other tasks.
 *
 * @author Stephan Haarmann
 * @version 28.10.2014.
 */
public class TaskCopier {

    // Holds the current Workspace
    private File workspace;
    // Maps PCMFragments to its tasks
    private Map<ProcessModel, List<Task>> tasksOfModels;
    private ProcessEditor processEditor;

    public TaskCopier(ProcessEditor processEditor) {
        this.processEditor = processEditor;
    }

    /**
     * This methods sets the workspace.
     * Whenever the workspace changes a List of all tasks in the workspaces has to be created.
     *
     * @param workspace the Workspace of the related PCM Scenario
     */
    public void setWorkspace(File workspace) {
        this.workspace = workspace;
        tasksOfModels = new LinkedHashMap<ProcessModel, List<Task>>();
        createTaskList();
    }


    /**
     * This Method creates a list of all tasks from all PCM Fragments inside the workspace
     */
    private void createTaskList() {
        // Reset the TaskList
        tasksOfModels.clear();
        // Create a filter to select only models
        FileFilter modelFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getPath().endsWith(".model");
            }
        };

        // Get all model-Files
        File[] arrayOfModelFiles = workspace.listFiles(modelFilter);
        // open the models
        for (File modelFile : arrayOfModelFiles) {
            List<ProcessModel> models = getPCMModelsFromFile(modelFile);
            addTasksOfModels(models);
        }
        openCopyTaskDialog();

    }

    /**
     * Opens a dialog to choose a task from existing ProcessFragments
     */
    private void openCopyTaskDialog() {
        if (tasksOfModels.isEmpty()) {
            System.out.println("No task found");
            return;
        }
        final JDialog chooseMenu = new ChooseMenu();
        chooseMenu.setSize(chooseMenu.getPreferredSize());
        chooseMenu.setVisible(true);
        chooseMenu.setLocationRelativeTo(processEditor);
        chooseMenu.setLocation(processEditor.getWidth() / 2 - 250,
                processEditor.getHeight() / 2 - 75);
    }

    /**
     * Adds the process models with their tasks to the tasksOfModels map.
     *
     * @param models a List of PCMModels
     */
    private void addTasksOfModels(List<ProcessModel> models) {
        for (ProcessModel model : models) {
            List<Task> tasks = new LinkedList<Task>();
            List<ProcessNode> nodes = model.getNodes();
            for (ProcessNode node : nodes) {
                if (node instanceof Task) {
                    tasks.add((Task)node);
                }
            }
            // We don't need entries for models without tasks
            if (!(tasks.isEmpty())) {
                tasksOfModels.put(model, tasks);
            }
        }
    }

    /**
     * Opens all models saved in modelFile and selects the PCMFragments
     *
     * @param modelFile the file to be opened
     * @return a List of all PCMFragments saved modelFile
     */
    private List<ProcessModel> getPCMModelsFromFile(File modelFile) {
        try {
            // Import all Models
            List<ProcessModel> models = ConverterHelper.importModels(modelFile);
            // Check for Errors
            if (models == null) {
                throw new Exception("Model type not recognized");
            }
            // Select the PCMFragments
            List<ProcessModel> pcmModels = new LinkedList<ProcessModel>();
            for (ProcessModel process : models) {
                process.setProcessModelURI(modelFile.getAbsolutePath());
                if (process instanceof PCMFragment) {
                    pcmModels.add(process);
                }
            }
            return  pcmModels;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Could not open process model: " + modelFile.getAbsolutePath());
        }
        return null;
    }

    /**
     * ChooseMenu is a Dialog for Choosing a Task to copy and refer
     */
    private class ChooseMenu extends JDialog {
        private JComboBox<ProcessModel> chooseProcessBox;
        private JComboBox<Task> chooseTaskBox;

        public ChooseMenu() {
            super();
            setTitle("Choose Task");
            setLayout(new GridLayout(3, 2));
            initializeComponents();
            setPreferredSize(new Dimension(500, 150));
        }

        /**
         * Initializes and adds all the necessary Components
         */
        private void initializeComponents() {
            add(new JLabel("Choose Process:"));
            chooseProcessBox = chooseProcessBox();
            add(chooseProcessBox);
            add(new JLabel("Choose Task:"));
            chooseTaskBox = chooseTaskBox();
            add(chooseTaskBox);
            initActionListener();
            add(acceptButton());
            add(cancelButton());
        }

        /**
         * Initializes the ActionListener for the chooseProcessBox
         */
        private void initActionListener() {
            chooseProcessBox.addActionListener(new ActionListener() {
                /**
                 * If a Process has been chosen set the options of the taskComboBox to the specific Tasks
                 * @param event
                 */
                @Override
                public void actionPerformed(ActionEvent event) {
                    JComboBox processBox = (JComboBox) event.getSource();
                    chooseTaskBox.removeAllItems();
                    List<Task> tasksOfSelectedProcess = tasksOfModels.get(processBox.getSelectedItem());
                    for (Task task : tasksOfSelectedProcess) {
                        chooseTaskBox.addItem(task);
                    }
                }
            });
        }

        /**
         * Creates a JComboBox for choosing the Task
         *
         * @return JComboBox
         */
        private JComboBox<Task> chooseTaskBox() {
            JComboBox<Task> selectTask = new JComboBox<Task>();
            return selectTask;
        }

        /**
         * Creates a JComboBox containing with all PCM Fragments of the workspace which have Tasks as an Option
         *
         * @return the JComboBox
         */
        private JComboBox<ProcessModel> chooseProcessBox() {
            JComboBox<ProcessModel> processComboBox = new JComboBox<ProcessModel>();
            Set<ProcessModel> models = tasksOfModels.keySet();
            for (ProcessModel model : models) {
                processComboBox.addItem(model);
            }
            return processComboBox;
        }

        private JButton acceptButton() {
            JButton accept = new JButton("Ok");
            accept.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (null != chooseTaskBox.getSelectedItem()) {
                        processEditor.getSelectedModel().addNode((ProcessNode) chooseTaskBox.getSelectedItem());
                        updateReferences();
                    }
                    setVisible(false);
                }

                private void updateReferences() {
                    Map<ProcessModel, ProcessNode> references = ((PCMFragment)chooseProcessBox.getSelectedItem())
                            .getReferences()
                            .get(chooseTaskBox.getSelectedItem());
                    references.put((ProcessModel)chooseProcessBox.getSelectedItem(), (ProcessNode)chooseTaskBox.getSelectedItem());
                    ((PCMFragment)processEditor.getSelectedModel())
                            .addReference((ProcessNode)chooseTaskBox.getSelectedItem(), references);
                }
            });
            return accept;
        }

        private JButton cancelButton() {
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            return cancel;
        }
    }
}
