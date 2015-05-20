package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm;

import com.inubit.research.gui.Workbench;
import com.inubit.research.gui.plugins.WorkbenchPlugin;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.SwingUtils;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMScenario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is a workbench Plugin which allows us to create a variant of an existing model.
 * The model has to be a pcm scenario.
 * This plugin is not used any longer because it is easier to just save the model as a
 * new file/ model on the server.
 */
public class CreateVariantOfModel extends WorkbenchPlugin {
    private final Workbench wb;

    /**
     * Creates a new instance of the plugin.
     * @param workbench
     */
    public CreateVariantOfModel(Workbench workbench) {
        super(workbench);
        this.wb = workbench;
    }

    /**
     * Creates and returns the menu item triggering this plugin.
     * @return The newly created menuItem.
     */
    @Override
    public Component getMenuEntry() {
        JMenuItem menuItem = new JMenuItem("Create Variant");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println("Workbench" + wb);
                ProcessModel model = wb.getSelectedModel();
                if (!(model instanceof PCMScenario)) {
                    System.err.println("Model has to be a PCM Scenario");
                    return;
                }
                JDialog chooseProcess = new CopyProcessDialog((PCMScenario)model);
                chooseProcess.setSize(chooseProcess.getPreferredSize());
                SwingUtils.center(chooseProcess);
                chooseProcess.setVisible(true);
            }
        });
        return menuItem;
    }

    /**
     * ChooseMenu is a Dialog for Choosing a PCM Fragment to Copy
     */
    private class CopyProcessDialog extends JDialog {
        private JComboBox<ProcessModel> chooseProcessBox;
        private PCMScenario model;

        public CopyProcessDialog(PCMScenario model) {
            super();
            this.model = model;
            setTitle("Choose Task");
            setLayout(new GridLayout(2, 2));
            initializeComponents();
            setPreferredSize(new Dimension(500, 100));
        }

        /**
         * Initializes and adds all the necessary Components
         */
        private void initializeComponents() {
            add(new JLabel("Choose Process:"));
            chooseProcessBox = chooseProcessBox();
            add(chooseProcessBox);
            add(acceptButton());
            add(cancelButton());
        }

        /**
         * Creates a JComboBox containing with all PCM Fragments of the workspace which have Tasks as an Option
         *
         * @return the JComboBox
         */
        private JComboBox<ProcessModel> chooseProcessBox() {
            JComboBox<ProcessModel> processComboBox = new JComboBox<ProcessModel>();
            System.out.println("model: " + model);
            if (null == model.getModelList()) {
                return processComboBox;
            }
            for (ProcessModel m : model.getModelList()) {
                processComboBox.addItem(m);
            }
            return processComboBox;
        }

        /**
         * Creates a Button to accept the selection.
         * The Button will have the label "ok".
         * @return THe newly created Button.
         */
        private JButton acceptButton() {
            JButton accept = new JButton("Ok");
            accept.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ProcessModel newModel = ((ProcessModel)chooseProcessBox.getSelectedItem()).clone();
                    newModel.setProcessName(newModel.getProcessName() + "(Clone)");
                    workbench.processModelOpened(newModel);
                    //workbench.openNewModel(((ProcessModel)chooseProcessBox.getSelectedItem()).clone());
                    setVisible(false);
                }
            });
            return accept;
        }


        /**
         * Creates a Button to discard the selection.
         * The Button will have the create label "cancel".
         * @return The newly created Button.
         */
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