/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SimulationConfiguration.java
 *
 * Created on 23.04.2009, 14:49:46
 */

package net.frapu.code.simulation.petrinets;

import net.frapu.code.visualization.Configuration;

/**
 *
 * @author fpu
 */
public class SimulationConfiguration extends javax.swing.JPanel {

	private static final long serialVersionUID = -8179581456325895683L;
	public final static int SIMULATION_SPEED_SLOWEST = 1000;
    public final static int SIMULATION_SPEED_SLOW = 250;
    public final static int SIMULATION_SPEED_MEDIUM = 100;
    public final static int SIMULATION_SPEED_FAST = 25;
    public final static int SIMULATION_SPEED_FASTEST = 0;
    public final static int SIMULATION_SPEED_WARP = -1;

    public final static int SIMULATION_PRECISION_LOW = 10;
    public final static int SIMULATION_PRECISION_MEDIUM = 100;
    public final static int SIMULATION_PRECISION_HIGH = 1000;

    PetriNetSimulationEditor editor = null;

    /** Creates new form SimulationConfiguration */
    public SimulationConfiguration(PetriNetSimulationEditor editor) {
        this.editor = editor;
        initComponents();
        this.setSize(300,200);

        // Set dot location
        Configuration conf = Configuration.getInstance();
        String dotLocation = conf.getProperty(Configuration.PROP_DOT_LOCATION);
        jTextField1.setText(dotLocation);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(300, 100));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Simulation engine"));

        jLabel1.setText("Speed:");

        jLabel2.setText("Precision:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Slowest", "Slow", "Medium", "Fast", "Fastest", "Warp (Event-based)" }));
        jComboBox1.setSelectedIndex(3);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Very low (10 instances)", "Medium (100 instances)", "High (1000 instances)" }));
        jComboBox2.setSelectedIndex(1);
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox2, 0, 171, Short.MAX_VALUE))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(6, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("External tools"));

        jLabel3.setText("Path to dot:");

        jTextField1.setText("/usr/local/bin/dot");
        jTextField1.setEnabled(false);

        jButton1.setText("...");
        jButton1.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1)
                .addComponent(jLabel3)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
	 * @param evt  
	 */
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        switch (jComboBox1.getSelectedIndex()) {
            case 0: editor.setSimulationSpeed(SIMULATION_SPEED_SLOWEST);
                    break;
            case 1: editor.setSimulationSpeed(SIMULATION_SPEED_SLOW);
                    break;
            case 2: editor.setSimulationSpeed(SIMULATION_SPEED_MEDIUM);
                    break;
            case 3: editor.setSimulationSpeed(SIMULATION_SPEED_FAST);
                    break;
            case 4: editor.setSimulationSpeed(SIMULATION_SPEED_FASTEST);
                    break;
            case 5: editor.setSimulationSpeed(SIMULATION_SPEED_WARP);
                    break;
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
	 * @param evt  
	 */
    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        switch (jComboBox2.getSelectedIndex()) {
            case 0: editor.setTokenCount(SIMULATION_PRECISION_LOW);
                    break;
            case 1: editor.setTokenCount(SIMULATION_PRECISION_MEDIUM);
                    break;
            case 2: editor.setTokenCount(SIMULATION_PRECISION_HIGH);
                    break;
        }
    }//GEN-LAST:event_jComboBox2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}