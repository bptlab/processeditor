package net.frapu.code.visualization.pcm;

import com.inubit.research.client.*;
import com.inubit.research.gui.WorkbenchConnectToServerDialog;
import net.frapu.code.visualization.Configuration;
import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * This class is an action listener for the plugin _Define Scenario From Server_.
 * This Action listener opens a new window, which allows you to change the set of fragments, that are part of the
 * scenario.
 *
 * @author Stephan Haarmann & Juliane Imme
 * @version 10/12/2014
 */
public class DefineScenarioFromServerActionListener implements ActionListener {
    /* UI Elements for the "choose fragments" Dialog */
    private JTextField serverInput;
    private JTextField userInput;
    private JPasswordField passwordInput;
    private JButton connectButton;
    private JFrame serverConnectionFrame;
    private JButton accept;
    private JButton cancel;
    /* Configurations/Models for the ui-Elements */
    private JList modelList;
    private JList selectedModelList;

    /* Holds the context */
    private ProcessEditor editor;   // The current editor, which contains the Scenario
    private DefaultListModel<ProcessModel> modelRepositoryModel; // All Fragments from the Repository
    private DefaultListModel<ProcessModel> selectedModelModel; // All Fragments which are part of the scenario

    /**
     * Creates a new ActionListener with the given editor as ProcessEditor. This Editor should be responsible for the
     * Scenario.
     *
     * @param editor
     */
    public DefineScenarioFromServerActionListener(ProcessEditor editor) {
        super();
        this.editor = editor;
    }

    /**
     * Creates the JFrame (adding the Elemens, doing Layout ...)
     */
    private JFrame createFrame() {
        JFrame frame = new JFrame("Create Scenario from Server Data");
        frame.setLayout(new BorderLayout());
        frame.add(getServerPanel(), BorderLayout.NORTH);
        frame.add(getSelectionPanel(), BorderLayout.CENTER);
        frame.add(getButtonPanel(), BorderLayout.SOUTH);
        frame.setSize(300, 600);
        connectToServerAction();
        frame.setVisible(true);
        return frame;

    }

    /**
     * Creates a Panel which contains Fields and buttons to connect with the server
     *
     * @return returns the serverPanel
     */
    private Component getServerPanel() {
        initServerInputs();
        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new GridLayout(4, 2));
        serverPanel.add(new Label("Server"));
        serverPanel.add(serverInput);
        serverPanel.add(new Label("User"));
        serverPanel.add(userInput);
        serverPanel.add(new Label("Password"));
        serverPanel.add(passwordInput);
        serverPanel.add(connectButton);
        return serverPanel;
    }

    /**
     * Initializes the Inputs (Default values & ActionListeners)
     */
    private void initServerInputs() {
        serverInput = new JTextField("http://localhost:1205");
        userInput = new JTextField("root");
        passwordInput = new JPasswordField("inubit");
        connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServerAction();
            }
        });
    }

    /**
     * Copied from WorkbenchFetchModelDialog
     */
    private void connectToServerAction() {
        modelRepositoryModel = new DefaultListModel<>();
        selectedModelModel = new DefaultListModel<>();
        selectedModelList.setModel(selectedModelModel);
        modelList.setModel(modelRepositoryModel);
        try {
            URI serverUri = URI.create(serverInput.getText());
            ModelServer server = new ModelServer(serverUri, "/",
                    new UserCredentials(serverUri, userInput.getText(), new String(passwordInput.getPassword())));

            ModelDirectory directory = server.getDirectory();
            updateModelList(directory);
            addExistingFragments();
            Configuration conf = Configuration.getInstance();
            conf.setProperty(WorkbenchConnectToServerDialog.CONF_SERVER_URI, serverInput.getText());
            conf.setProperty(WorkbenchConnectToServerDialog.CONF_SERVER_USER, userInput.getText());
            conf.setProperty(WorkbenchConnectToServerDialog.CONF_SERVER_PASSWORD, new String(passwordInput.getPassword()));
        } catch (Exception e) {
            if (e instanceof InvalidUserCredentialsException) {
                userInput.setBackground(Color.RED);
                passwordInput.setBackground(Color.red);
            } else {
                serverInput.setBackground(Color.RED);
            }
            modelList.removeAll();
            selectedModelList.removeAll();
        }
    }

    /**
     * This Method will be called every time the dialog is opened. It asserts that Fragments which have been
     * added to the scenario will not be removed automatically
     */
    private void addExistingFragments() {
        ProcessModel model = editor.getModel();
        if (!(model instanceof PCMScenario)) {
            return;
        }
        if (((PCMScenario)model).getModelList().isEmpty()) {
            for (ProcessNode node : model.getNodesByClass(PCMFragmentNode.class)) {
                for (int i = 0; i < modelRepositoryModel.getSize(); i++) {
                    ProcessModel m = modelRepositoryModel.get(i);
                    if (node.getText().equals(m.getProcessName())) {
                        selectedModelModel.addElement(m);
                    }
                }
            }
        } else {
            for (ProcessModel m : ((PCMScenario)model).getModelList()) {
                selectedModelModel.addElement(m);
            }
        }
    }

    /**
     * Updates the ModelList: Adds all models of a given directory to the model List
     * @param directory
     */
    private void updateModelList(ModelDirectory directory) {
        if (directory != null) {
            java.util.List<ModelDirectoryEntry> l = directory.getEntries();
            for (ModelDirectoryEntry entry : l) {
                if (entry instanceof ModelDirectory) {
                    updateModelList((ModelDirectory)entry);
                } else if (entry instanceof ModelDescription) {
                    try {
                        ModelDescription description = (ModelDescription)entry;
                        ProcessModel model =  description.getVersionDescription(description.getHeadVersion()).getProcessModel();
                        if (model instanceof PCMFragment) {
                            modelRepositoryModel.addElement(model);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to Fetch ProcessModel from Server");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        serverConnectionFrame = createFrame();
    }

    /**
     *      * @return selectionpanel - A Panel which contains two Lists (available and added Fragments) and two controls (add/remove)
     * which allows you to add/remove fragments from the scenario
     */
    private Component getSelectionPanel() {
        JPanel selectionPanel = new JPanel();
        JScrollPane listPane = new JScrollPane();
        JScrollPane selectedPane = new JScrollPane();
        selectedModelList = new JList();
        modelList = new JList();
        selectedPane.add(selectedModelList);
        listPane.add(modelList);
        selectionPanel.setLayout(new BorderLayout());
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(1, 2));
        listPanel.add(modelList);
        listPanel.add(selectedModelList);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        JButton add = new JButton("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ProcessModel model : (java.util.List<ProcessModel>) modelList.getSelectedValuesList()) {
                    if (!selectedModelModel.contains(model)) {
                        selectedModelModel.addElement(model);
                    }
                }
            }
        });
        JButton remove = new JButton("Remove");
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ProcessModel model : (java.util.List<ProcessModel>)selectedModelList.getSelectedValuesList()) {
                    if (!selectedModelModel.removeElement(model)) {
                        System.err.println("Could not remove Fragment");
                    }
                }
            }
        });
        buttonPanel.add(add);
        buttonPanel.add(remove);
        selectionPanel.setLayout(new BorderLayout());
        selectionPanel.add(listPanel, BorderLayout.CENTER);
        selectionPanel.add(buttonPanel, BorderLayout.SOUTH);
        return selectionPanel;
    }

    /**
     * @return buttonPanel - A JPanel containing two Buttons to Accept & Cancel all changes
     */
    private Component getButtonPanel() {
        initButton();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(accept);
        buttonPanel.add(cancel);
        return buttonPanel;
    }

    /**
     * This Method initializes the Buttons of the ButtonPanel (adding ActionListeners, Labels)
     */
    private void initButton() {
        accept = new JButton("Ok");
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PCMScenario scenario = (PCMScenario)editor.getModel();
                scenario.reset();
                for (int i = 0; i < selectedModelModel.getSize(); i++) {
                    scenario.addPCMFragment((PCMFragment)selectedModelModel.get(i));
                }
                scenario.createNodesForFragments();
                scenario.createDataList();
                serverConnectionFrame.dispose();
            }
        });
        cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverConnectionFrame.dispose();
            }
        });
    }
}