package net.frapu.code.visualization.pcm;

import com.inubit.research.client.*;
import com.inubit.research.gui.WorkbenchConnectToServerDialog;
import net.frapu.code.visualization.Configuration;
import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.ProcessModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * @author Stephan
 * @version 10/12/2014
 */
public class DefineScenarioFromServerActionListener implements ActionListener {
    private JTextField serverInput;
    private JTextField userInput;
    private JPasswordField passwordInput;
    private JButton connectButton;
    private JFrame serverConnectionFrame;

    private ProcessEditor editor;
    private JButton accept;
    private JButton cancel;
    private JList modelList;
    private JList selectedModelList;
    private DefaultListModel<ProcessModel> modelRepositoryModel;
    private DefaultListModel<ProcessModel> selectedModelModel;

    public DefineScenarioFromServerActionListener(ProcessEditor editor) {
        super();
        this.editor = editor;
    }

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
        addExistingFragments(selectedModelModel);
        selectedModelList.setModel(selectedModelModel);
        modelList.setModel(modelRepositoryModel);
        try {
            URI serverUri = URI.create(serverInput.getText());
            ModelServer server = new ModelServer(serverUri, "/",
                    new UserCredentials(serverUri, userInput.getText(), new String(passwordInput.getPassword())));

            ModelDirectory directory = server.getDirectory();
            updateModelList(directory);
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

    private void addExistingFragments(DefaultListModel<ProcessModel> selectedModelModel) {
        ProcessModel model = editor.getModel();
        if (!(model instanceof PCMScenario)) {
            return;
        }
        for (ProcessModel m : ((PCMScenario)model).getModelList()) {
            selectedModelModel.addElement(m);
        }
    }

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

    public Component getSelectionPanel() {
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
        //listPanel.add(listPane);
        //listPanel.add(selectedPane);
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

    public Component getButtonPanel() {
        initButton();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(accept);
        buttonPanel.add(cancel);
        return buttonPanel;
    }

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