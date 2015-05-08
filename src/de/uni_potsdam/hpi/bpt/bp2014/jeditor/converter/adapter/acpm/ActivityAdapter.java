package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.ControlFlow;
import net.frapu.code.visualization.bpmn.Task;

import java.util.Collection;
import java.util.List;

/**
 * Created by Stpehan on 07.05.2015.
 */
public class ActivityAdapter extends Activity {

    private Task task;
    private Collection<ControlFlow> wrappedIncomingCF;
    private Collection<ControlFlow> wrappedOutgoingCF;

    public ActivityAdapter(Task task) {
        super(task.getName());
        this.task = task;
    }
}
