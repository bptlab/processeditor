package net.frapu.code.visualization.pcm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This ActionListener allows to define the workspace for an PCM Fragment and informs the
 * {@link net.frapu.code.visualization.pcm.TaskCopier}
 *
 * @see net.frapu.code.visualization.pcm.TaskCopier
 * @see net.frapu.code.visualization.pcm.PCMFragment
 * @see net.frapu.code.visualization.pcm.PCMFragmentEditor
 * @version 28.10.2014.
 * @author Stephan Haarmann
 */
public class TaskCopierActionListener implements ActionListener {
    private final PCMFragmentEditor processEditor;
    private TaskCopier copier = null;

    /**
     * Creates a new instance referring to the given TaskCopier
     * @param pcmFragmentEditor the Process Editor
     * @param copier the TaskCopier responsible for copying and referring tasks
     */
    public TaskCopierActionListener(PCMFragmentEditor pcmFragmentEditor, TaskCopier copier) {
        this.processEditor = pcmFragmentEditor;
        this.copier = copier;
    }

    /**
     * This event opens a FileChooser which allows to define a Workspace.
     *
     * @param e the occurred event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        createFileChooser();
    }

    /**
     * This method creates a FileChooser for defining the Workspace
     */
    private void createFileChooser() {
        // Create a JFileChoose for choosing the Workspace
        JFileChooser fileChooser = new JFileChooser("Choose Workspace");
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // Do not Display the "All File" Filter
        fileChooser.setAcceptAllFileFilterUsed(false);
        int result = fileChooser.showOpenDialog(processEditor);
        if (JFileChooser.APPROVE_OPTION == result) {
            copier.setWorkspace(fileChooser.getSelectedFile());
        }
    }
}
