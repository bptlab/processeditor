package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import com.inubit.research.client.*;
import com.inubit.research.gui.WorkbenchConnectToServerDialog;
import com.inubit.research.gui.WorkbenchFetchModelDialog;
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
 * This Action Listener is an action listener which allows us
 * to catch all {@link Task} from any {@link PCMFragment}.
 * Such a fragment can then be copied and referred.
 *
 * @author Stephan Haarmann & Juliane Imme
 * @version 03.12.2014
 */
public class GetTasksFromServerActionListener implements ActionListener {
    /**
     * Text field for the server address.
     */
    private JTextField serverInput;
    /**
     * Input field for the user name.
     */
    private JTextField userInput;
    /**
     * Input field for the password.
     */
    private JPasswordField passwordInput;
    /**
     * The editor which hold this instant,
     * necessary to add the elements.
     */
    private PCMFragmentEditor editor;
    /**
     * Field for the model selection.
     */
    private JComboBox selectModelChooser;
    /**
     * Field for the task selection.
     */
    private JComboBox selectTaskChooser;
    /**
     * A Button to accept the selection.
     */
    private JButton accept;
    /**
     * A Button to cancel the action.
     */
    private JButton cancel;
    /**
     * A Button to start the connect action.
     * The dialog will try to connect to the server.
     */
    private JButton connectButton;
    /**
     * A Frame to hold the elements all the swing
     * elements.
     */
    private JFrame serverConnectionFrame;

    /**
     * Creates a new instance of the Action Listener for a given
     * PCMFragmentEditor.
     * The field will be initialized with the default values.
     *
     * @param pcmFragmentEditor The process editor which will trigger this action.
     */
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

    /**
     * If the action is triggered The serverConnectionFrame
     * will be initialized and displayed.
     *
     * @param e the event information will be ignored.
     */
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

    /**
     * Returns the content panel of the connect to server frame.
     * The panel contains the elements needed to connect to the
     * server.
     * @return The panel with the server input elements.
     */
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

    /**
     * Creates a JPanel for the selectors.
     * Contains the {@link #selectModelChooser}
     * and the {@link #selectTaskChooser} this gives
     * us the possibility to choose a task.
     *
     * @return The JPanel containing the JChooser.
     */
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

    /**
     * Creates a Panel contiaining the three buttons,
     * {@link #connectButton}
     * {@link #accept}
     * {@link #cancel}
     * Those buttons will be initialized.
     *
     * @return A JPanel containing the buttons.
     */
    private Component getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        accept = new JButton("Ok");
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessNode node = (ProcessNode) selectTaskChooser.getSelectedItem();
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
     * Copied from {@link WorkbenchFetchModelDialog}
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

    /**
     * Iterates over all elements inside a given directory.
     * If the element describes a PCMFragment its head version will
     * be added to the model list.
     *
     * @param directory The directory to be searched.
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
