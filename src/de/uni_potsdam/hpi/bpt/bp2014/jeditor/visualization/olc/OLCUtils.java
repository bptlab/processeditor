package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc;

import com.inubit.research.layouter.ProcessLayouter;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.ProcessUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Utils class for OLCs.
 * It saves default values and can be used to configure
 * the look and feel of the Object Life Cycle models.
 * Hence, it extends {@link net.frapu.code.visualization.ProcessUtils}.
 */
public class OLCUtils extends ProcessUtils {
    @Override
    public ProcessEdge createDefaultEdge(ProcessNode source, ProcessNode target) {
        return new StateTransition(source, target);
    }

    @Override
    public List<ProcessLayouter> getLayouters() {
        return new ArrayList<>(0);
    }

    @Override
    public List<Class<? extends ProcessNode>>getNextNodesRecommendation(ProcessModel model, ProcessNode node) {
        List<Class<? extends ProcessNode>> nodeTypes = new ArrayList<>(1);
        nodeTypes.add(DataObjectState.class);
        return nodeTypes;
    }
}
