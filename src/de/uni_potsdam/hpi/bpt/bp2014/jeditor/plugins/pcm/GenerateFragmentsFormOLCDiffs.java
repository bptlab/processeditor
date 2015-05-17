package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm;

import com.inubit.research.gui.plugins.WorkbenchPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Stpehan on 17.05.2015.
 */
public class GenerateFragmentsFormOLCDiffs extends WorkbenchPlugin {
    @Override
    public Component getMenuEntry() {
        JMenuItem item = new JMenuItem("Create Fragment for Diff");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        return item;
    }
}
