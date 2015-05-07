package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.editors;

import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMScenario;
import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.domainModel.DomainClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class TerminationEditor extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton addButton;
    private JTextField textField1;
    private JScrollPane scrollPane1;
    private JPanel innerPanel;
    private java.util.List<String> terminationConditions;
    private ProcessEditor editor;

    public TerminationEditor() {
        terminationConditions = new LinkedList<String>();
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        setResizable(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText() != null && !textField1.getText().isEmpty()) {
                    JPanel row = new JPanel();
                    row.setLayout(new GridLayout(1, 2));
                    row.add(new JLabel(textField1.getText()));
                    row.add(new RemoveButton(row, textField1.getText()));
                    row.setVisible(true);
                    innerPanel.add(row);
                    innerPanel.revalidate();
                    innerPanel.repaint();
                    terminationConditions.add(textField1.getText());
                }
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public TerminationEditor(ProcessEditor editor) {
        this.editor = editor;
        this.setSize(editor.getWidth() / 2, editor.getHeight() / 2);
        this.setResizable(false);
        terminationConditions = new LinkedList<String>();
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        setResizable(true);

        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField1.getText() != null && !textField1.getText().isEmpty()) {
                    JPanel row = new JPanel();
                    row.setLayout(new GridLayout(1, 2));
                    row.add(new JLabel(textField1.getText()));
                    row.add(new RemoveButton(row, textField1.getText()));
                    row.setVisible(true);
                    innerPanel.add(row);
                    innerPanel.revalidate();
                    innerPanel.repaint();
                    terminationConditions.add(textField1.getText());
                }
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        initTerminationConditions();
    }

    private void initTerminationConditions() {
        String bla = editor.getSelectedModel().getProperty(PCMScenario.PROP_TERMINATIONCONDITION);
        System.out.println("EDITOR:"+editor.getName());
        System.out.println("LASTSELECTEDNODE:"+editor.getLastSelectedNode());
        System.out.println("PROPERTY:"+bla);
        System.out.println("SPLIT:"+bla.split("; "));
        String tcs[] = editor.getSelectedModel().getProperty(PCMScenario.PROP_TERMINATIONCONDITION).split(";");
        for (String tc : tcs) {
            if (!tc.isEmpty()) {
                terminationConditions.add(tc.replaceAll("\\{\\d\\}\\+", ""));
            }
        }
        initializeList();
    }

    private void initializeList() {
        for (String tc : terminationConditions) {
            JPanel row = new JPanel();
            row.setLayout(new GridLayout(1, 2));
            row.add(new JLabel(tc));
            row.add(new RemoveButton(row, tc));
            row.setVisible(true);
            innerPanel.add(row);
            innerPanel.revalidate();
            innerPanel.repaint();
        }
    }

    private void onOK() {
        editor.getSelectedModel().setProperty(PCMScenario.PROP_TERMINATIONCONDITION, buildPropertyString());
        dispose();
    }

    private String buildPropertyString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terminationConditions.size(); i++) {
            sb.append(terminationConditions.get(i));
            sb.append(";");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        TerminationEditor dialog = new TerminationEditor();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        scrollPane1 = new JScrollPane(innerPanel);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addButton = new JButton();
        addButton.setText("Add");
        panel5.add(addButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField1 = new JTextField();
        panel5.add(textField1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel4.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private class RemoveButton extends JButton {
        JPanel toBeRemoved;
        String attrToBeRemoved;

        public RemoveButton(JPanel dynamicAttribute, String attributeToBeRemoved) {
            toBeRemoved = dynamicAttribute;
            attrToBeRemoved = attributeToBeRemoved;
            this.setText("Delete");
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toBeRemoved.removeAll();
                    innerPanel.remove(toBeRemoved);
                    innerPanel.revalidate();
                    innerPanel.repaint();
                    terminationConditions.remove(attrToBeRemoved);
                }
            });
        }
    }
}
