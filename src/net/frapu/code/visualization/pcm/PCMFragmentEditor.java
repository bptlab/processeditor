package net.frapu.code.visualization.pcm;

import com.inubit.research.ISConverter.exporter.ISBPDExporter;
import com.inubit.research.layouter.freeSpace.FreeSpaceLayouter;
import net.frapu.code.visualization.ProcessEditor;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.ProcessObject;
import net.frapu.code.visualization.bpmn.BPMNModel;
import net.frapu.code.visualization.layouter.LayoutMenuitemActionListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * A a simple {@link net.frapu.code.visualization.ProcessEditor} which allows to reuse
 * {@link net.frapu.code.visualization.bpmn.Task}s from existing PCM Fragments.
 * Also the {@link com.inubit.research.layouter.freeSpace.FreeSpaceLayouter} is initialized.
 *
 * @author Stephan Haarmann & Juliane Imme
 * @version 28.10.2014.
 */
public class PCMFragmentEditor extends ProcessEditor {

    private static final long serialVersionUID = -6660643360605974595L;

    /**
     * Creates and initalizes a new PCMFragmentEditor for an new PCMFramgent.
     */
    public PCMFragmentEditor() {
        super();
        init();
    }

    /**
     * Creates a new PCMFragmentEditor for a given Model
     * @param model
     */
    public PCMFragmentEditor(ProcessModel model) {
        super(model);
        init();
    }

    /**
     * Initializes the FragmentEditor. It adds Context-Menu Entries for the addCopyTaskFromOtherFragmentOnServer and
     * the BPMNLayouter.
     * For Offline Modeling you could use the CopyTaskFromOtherFragment plugin
     */
    private void init() {
        //addCopyTaskFromOtherFragmentMenu();
        addCopyTaskFromOtherFragmentOnServerMenu();
        addFreeSpaceLayouterContextMenu();
    }

    /**
     * This methods adds a new component to the menu.
     * This component contains options for choosing tasks from existing Fragments.
     * If you want to link tasks you have to use this ContextMenu
     */
    @Deprecated
    private void addCopyTaskFromOtherFragmentMenu() {
        TaskCopier copier = new TaskCopier(this);
        JMenuItem menuItem = new JMenuItem("Copy and refer task");
        menuItem.addActionListener(new TaskCopierActionListener(this, copier));
        addCustomContextMenuItem(ProcessObject.class, menuItem);
    }

    /**
     * This method adds a new component to the menu.
     * This component contains options for choosing tasks from existing Fragments.
     * The existing Fragments are fetched from the server.
     */
    private void addCopyTaskFromOtherFragmentOnServerMenu() {
        JMenuItem menuItem = new JMenuItem("Copy and refer task");
        menuItem.addActionListener(new GetTasksFromServerActionListener(this));
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
