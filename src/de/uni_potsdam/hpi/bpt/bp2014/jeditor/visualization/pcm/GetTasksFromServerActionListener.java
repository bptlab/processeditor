package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import com.inubit.research.client.*;
import com.inubit.research.gui.WorkbenchConnectToServerDialog;
import net.frapu.code.visualization.Configuration;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.ProcessObject;
import net.frapu.code.visualization.bpmn.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * @author Stephan Haarmann & Juliane Imme
 * @version 03.12.2014
 */
public class GetTasksFromServerActionListener implements ActionListener {
    private JTextField serverInput;
    private JTextField userInput;
    private JPasswordField passwordInput;
    private PCMFragmentEditor editor;
    private JComboBox selectModelChooser;
    private JComboBox selectTaskChooser;
    private JButton accept;
    private JButton cancel;
    private JButton connectButton;
    private JFrame serverConnectionFrame;

    public GetTasksFromServerActionListener(PCMFragmentEditor pcmFragmentEditor) {
        editor = pcmFragmentEditor;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        serverConnectionFrame = new JFrame("Get Tasks from Server");
        //serverConnectionFrame.setLayout(new GridLayout(4, 1));
        serverConnectionFrame.setLayout(new BorderLayout());
        serverConnectionFrame.add(getServerPanel(), BorderLayout.NORTH);
        serverConnectionFrame.add(getSelectionPanel(), BorderLayout.CENTER);
        serverConnectionFrame.add(getButtonPanel(), BorderLayout.SOUTH);
        serverConnectionFrame.setSize(600, 300);
        serverConnectionFrame.setVisible(true);
        serverConnectionFrame.setResizable(false);
        connectToServerAction();
    }

    private Component getServerPanel() {
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

    private Component getSelectionPanel() {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(2, 1));
        selectionPanel.add(getSelectModelChooser());
        selectionPanel.add(getSelectTaskChooser());
        return selectionPanel;
    }

    private Component getSelectModelChooser() {
        selectModelChooser = new JComboBox();
        selectModelChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessModel model = (ProcessModel) ((JComboBox) e.getSource()).getSelectedItem();
                for (ProcessNode node : model.getNodesByClass(Task.class)) {
                    if (node.getProperty("global").equals(ProcessObject.TRUE)) {
                        selectTaskChooser.addItem(node);
                    }
                }
            }
        });
        return selectModelChooser;
    }

    private Component getSelectTaskChooser() {
        selectTaskChooser = new JComboBox();
        return selectTaskChooser;
    }

    private Component getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        accept = new JButton("Ok");
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessNode node = (ProcessNode)selectTaskChooser.getSelectedItem();
                if (node != null) {
                    editor.getModel().addNode(node);
                    serverConnectionFrame.dispose();
                }
            }
        });
        cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverConnectionFrame.dispose();
            }
        });
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(accept);
        buttonPanel.add(cancel);
        return buttonPanel;
    }

    /**
     * Copied from WorkbenchFetchModelDialog
     */
    private void connectToServerAction() {
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
            selectModelChooser.removeAllItems();
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
                            selectModelChooser.addItem(model);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to Fetch ProcessModel from Server");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
