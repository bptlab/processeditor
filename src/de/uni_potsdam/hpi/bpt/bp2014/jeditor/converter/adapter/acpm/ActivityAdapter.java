package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.ControlFlow;
import net.frapu.code.visualization.bpmn.Task;

import java.util.Collection;
import java.util.List;

/**
 * This class is an adapter for {@link Task}.
 * It wil adapt them to the Activity.
 * The edges have to be set from outside the
 * adapter.
 */
public class ActivityAdapter extends Activity {

    private Task task;

    public ActivityAdapter(Task task) {
        super(task.getName());
        this.task = task;
    }
}
