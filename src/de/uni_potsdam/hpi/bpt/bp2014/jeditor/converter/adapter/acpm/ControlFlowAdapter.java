package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.ControlFlow;
import net.frapu.code.visualization.ProcessEdge;

/**
 * Created by Stpehan on 07.05.2015.
 */
public class ControlFlowAdapter extends ControlFlow {
    private ProcessEdge edge;

    public ControlFlowAdapter(INode source, INode target) {
        super(source, target);
    }

    public ControlFlowAdapter(INode source, INode target, ProcessEdge processEdge) {
        super(source, target);
        edge = processEdge;
    }
}
