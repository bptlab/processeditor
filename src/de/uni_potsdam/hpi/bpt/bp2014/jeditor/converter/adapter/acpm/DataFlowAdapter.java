package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataFlow;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataObject;
import net.frapu.code.visualization.ProcessEdge;

/**
 * Created by Stpehan on 07.05.2015.
 */
public class DataFlowAdapter extends DataFlow {
    private ProcessEdge edge;

    public DataFlowAdapter(DataObject source, Activity target) {
        super(source, target);
    }
    public DataFlowAdapter(Activity target, DataObject source) {
        super(source, target);
    }

    public DataFlowAdapter(DataObject source, Activity target, ProcessEdge processEdge) {
        super(source, target);
        this.edge = processEdge;
    }



    public DataFlowAdapter(Activity source, DataObject target, ProcessEdge processEdge) {
        super(source, target);
        this.edge = processEdge;
    }
}
