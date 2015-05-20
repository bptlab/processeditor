package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Event;
import net.frapu.code.visualization.ProcessNode;

/**
 * This class is a wrapper for an Event it can either
 * be an Start or an End event.
 */
public class EventAdapter extends Event {
    /**
     * Holds the event wrapped by this adapter.
     */
    private net.frapu.code.visualization.bpmn.Event event;

    /**
     * Creates a new adapter for a given Event
     * the edges and Type have to be set from the outside.
     * @param processNode The node which will be wrapped by this adapter.
     */
    public EventAdapter(net.frapu.code.visualization.bpmn.Event processNode) {
        event = processNode;
    }
}
