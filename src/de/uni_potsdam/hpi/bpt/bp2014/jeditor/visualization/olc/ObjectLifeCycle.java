package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc;

import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.ProcessUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents and Object Life Cycle.
 * It aggregates {@link DataObjectState} and {@link StateTransition} objects
 * to create a model.
 * This model can be used to describe the states and their transitions of a
 * DataObject.
 * It can be used in a combination for modelling {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMScenario}.
 * The information of the {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm.PCMFragment} and {@link net.frapu.code.visualization.domainModel.DomainModel}
 * can be extended by an OLC.
 */
public class ObjectLifeCycle extends ProcessModel {

    @Override
    public ProcessUtils getUtils() {
        return new OLCUtils();
    }

    @Override
    public String getDescription() {
        return "Object Life Cycle 4 PCM";
    }
    @Override
    public synchronized void addEdge(ProcessEdge edge) {
        if (edge instanceof StateTransition) {
            String text = edge.getLabel();
            if (null == text || text.equals("")) {
                getEditStateNameDialog(edge).setVisible(true);
            }
        }
        super.addEdge(edge);
    }

    @Override
    public List<Class<? extends ProcessNode>> getSupportedNodeClasses() {
        List<Class<? extends ProcessNode>> nodes = new ArrayList<>(1);
        nodes.add(DataObjectState.class);
        return nodes;
    }

    @Override
    public List<Class<? extends ProcessEdge>> getSupportedEdgeClasses() {
        List<Class<? extends ProcessEdge>> edges = new ArrayList<>(1);
        edges.add(StateTransition.class);
        return edges;
    }

    private JDialog getEditStateNameDialog(final ProcessEdge edge) {
        final JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setSize(100, 60);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(dialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JTextField input = new JTextField();
        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                edge.setLabel(input.getText());
                dialog.dispose();
            }
        });
        panel.add(input, BorderLayout.CENTER);
        panel.add(ok, BorderLayout.SOUTH);
        input.requestFocus();
        dialog.add(panel);
        return dialog;
    }
}
