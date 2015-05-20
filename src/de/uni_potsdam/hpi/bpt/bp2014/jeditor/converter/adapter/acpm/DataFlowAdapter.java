package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataFlow;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.DataObject;
import net.frapu.code.visualization.ProcessEdge;

/**
 * This class works as a wrapper for a DataFlowObject.
 * Source and target must be set from the outside.
 */
public class DataFlowAdapter extends DataFlow {
    /**
     * This field holds the {@link net.frapu.code.visualization.bpmn.Association}
     * Object wrapped by this adapter.
     */
    private ProcessEdge edge;

    /**
     * Creates a new instance of an incoming data flow.
     * @param source The source an instance of a DataObject.
     * @param target The target of the DataFlow must be an instance of Activity.
     * @param processEdge The edge which will be wrapped by this adapter.
     */
    public DataFlowAdapter(DataObject source, Activity target, ProcessEdge processEdge) {
        super(source, target);
        this.edge = processEdge;
    }

    /**
     * Creates a new instance of an outgoing data flow.
     * @param source The source an instance of a Activity.
     * @param target The target of the DataFlow must be an instance of DataObject.
     * @param processEdge The edge which will be wrapped by this adapter.
     */
    public DataFlowAdapter(Activity source, DataObject target, ProcessEdge processEdge) {
        super(source, target);
        this.edge = processEdge;
    }
}
