/**
 *
 * Process Editor - inubit Workbench Plugin Package
 *
 * (C) 2009, 2010 inubit AG
 * (C) 2014 the authors
 *
 */
package com.inubit.research.gui.plugins;

import java.awt.GridLayout;
import net.frapu.code.visualization.ProcessEditor;

/**
 *
 * @author fpu
 */
public class WorkbenchBirdviewDialog extends javax.swing.JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1166786682021507741L;
	private ProcessEditor previewEditor = new ProcessEditor();

    /** Creates new form WorkbenchBirdviewDialog */
    public WorkbenchBirdviewDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        previewPane.setLayout(new GridLayout(1,1));
        previewPane.add(previewEditor);
        previewEditor.setEditable(false);
        previewEditor.setEnabled(false);
    }

    public void setEditor(ProcessEditor editor) {
        previewEditor.setModel(editor.getModel());
        previewEditor.setScale(0.3);
        repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        previewPane = new javax.swing.JPanel();

        setTitle("Birdview");

        javax.swing.GroupLayout previewPaneLayout = new javax.swing.GroupLayout(previewPane);
        previewPane.setLayout(previewPaneLayout);
        previewPaneLayout.setHorizontalGroup(
            previewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 378, Short.MAX_VALUE)
        );
        previewPaneLayout.setVerticalGroup(
            previewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(previewPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                WorkbenchBirdviewDialog dialog = new WorkbenchBirdviewDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel previewPane;
    // End of variables declaration//GEN-END:variables

}