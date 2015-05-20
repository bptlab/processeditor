package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataObject;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState;
import net.frapu.code.visualization.ProcessNode;

import java.util.Map;

/**
 * This class adapts a {@link net.frapu.code.visualization.bpmn.DataObject}.
 * It will search for the corresponding DataObjectState and adds it.
 * Edges must be set from outside.
 */
public class DataObjectAdapter extends DataObject {
    /**
     * Stores the {@link DataObject} wrapped.
     */
    private ProcessNode dataObject;

    /**
     * Creates a new instance wrapping the specified node and searching for
     * the right state inside the stateStorage.
     *
     * @param raw          The node that will be wrapped by this adapter.
     * @param stateStorage The State storage, which will be used to extract the {@link DataObjectState}.
     */
    public DataObjectAdapter(ProcessNode raw, Map<String, DataObjectState> stateStorage) {
        super(raw.getName(),
                stateStorage.get(raw.getProperty(net.frapu.code.visualization.bpmn.DataObject.PROP_STATE)));
        dataObject = raw;
    }
}
