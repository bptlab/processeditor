package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Event;
import net.frapu.code.visualization.ProcessNode;

/**
 * Created by Stpehan on 07.05.2015.
 */
public class EventAdapter extends Event {
    private net.frapu.code.visualization.bpmn.Event event;

    public EventAdapter(net.frapu.code.visualization.bpmn.Event processNode) {
        event = processNode;
    }
}
