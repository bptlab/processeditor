package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.ControlFlow;
import net.frapu.code.visualization.ProcessEdge;

/**
 * This class wraps a ControlFLow edge.
 * The original edge will be saved, source and target
 * have to be added as a INode.
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
