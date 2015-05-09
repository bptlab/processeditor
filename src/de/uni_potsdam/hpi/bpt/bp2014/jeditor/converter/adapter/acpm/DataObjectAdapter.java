package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataObject;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState;
import net.frapu.code.visualization.ProcessNode;

import java.util.Map;

/**
 * Created by Stpehan on 09.05.2015.
 */
public class DataObjectAdapter extends DataObject {
    private ProcessNode dataObject;

    public DataObjectAdapter(ProcessNode raw, Map<String, DataObjectState> stateStorage) {
        super(raw.getName(),
                stateStorage.get(raw.getProperty(net.frapu.code.visualization.bpmn.DataObject.PROP_STATE)));
    }
}
