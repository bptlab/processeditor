package net.frapu.code.visualization.pcm;

import com.inubit.research.layouter.freeSpace.FreeSpaceLayouter;
import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessObject;
import net.frapu.code.visualization.layouter.LayoutMenuitemActionListener;

import javax.swing.*;

/**
 * A a simple {@link net.frapu.code.visualization.ProcessEditor} which allows to reuse
 * {@link net.frapu.code.visualization.bpmn.Task}s from existing PCM Fragments.
 * Also the {@link com.inubit.research.layouter.freeSpace.FreeSpaceLayouter} is initialized.
 *
 * @author Stephan Haarmann
 * @version 28.10.2014.
 */
public class PCMFragmentEditor extends ProcessEditor {

    private static final long serialVersionUID = -6660643360605974595L;

    public PCMFragmentEditor() {
        super();
        init();
    }

    public PCMFragmentEditor(ProcessModel model) {
        super(model);
        init();
    }

    private void init() {
        addCopyTaskFromOtherFragmentMenu();
        addFreeSpaceLayouterContextMenu();
    }

    /**
     * This methods adds a new component to the menu.
     * This component contains options for choosing tasks from existing Fragments.
     * If you want to link tasks you have to use this ContextMenu
     */
    private void addCopyTaskFromOtherFragmentMenu() {
        TaskCopier copier = new TaskCopier(this);
        JMenuItem menuItem = new JMenuItem("Copy and refer task");
        menuItem.addActionListener(new TaskCopierActionListener(this, copier));
        addCustomContextMenuItem(ProcessObject.class, menuItem);
    }

    /**
     * Copied from BPMNEditor
     * @see net.frapu.code.visualization.bpmn.BPMNEditor
     */
    private void addFreeSpaceLayouterContextMenu() {
        //Also add the layouting menu here
        FreeSpaceLayouter l = new FreeSpaceLayouter();
        JMenuItem menuItem = new JMenuItem(l.getDisplayName());
        menuItem.addActionListener(new LayoutMenuitemActionListener(this, l));
        addCustomContextMenuItem(ProcessObject.class, menuItem);
    }
}
