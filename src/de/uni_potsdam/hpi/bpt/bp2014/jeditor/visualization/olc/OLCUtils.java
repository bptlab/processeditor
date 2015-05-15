package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc;

import com.inubit.research.layouter.ProcessLayouter;
import com.inubit.research.layouter.sugiyama.SugiyamaLayoutAlgorithm;
import net.frapu.code.visualization.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Utils class for OLCs.
 * It saves default values and can be used to configure
 * the look and feel of the Object Life Cycle models.
 * Hence, it extends {@link net.frapu.code.visualization.ProcessUtils}.
 */
public class OLCUtils extends ProcessUtils {
    private List<ProcessLayouter> layouters;

    @Override
    public ProcessEdge createDefaultEdge(ProcessNode source, ProcessNode target) {
        return new StateTransition(source, target);
    }

    @Override
    public List<ProcessLayouter> getLayouters() {
        if (layouters == null) {
            layouters = new ArrayList<ProcessLayouter>();
            layouters.add(new SugiyamaLayoutAlgorithm(true, Configuration.getProperties()));
            layouters.add(new SugiyamaLayoutAlgorithm(false,Configuration.getProperties()));
        }
        return layouters;
    }

    @Override
    public List<Class<? extends ProcessNode>>getNextNodesRecommendation(ProcessModel model, ProcessNode node) {
        List<Class<? extends ProcessNode>> nodeTypes = new ArrayList<>(1);
        nodeTypes.add(DataObjectState.class);
        return nodeTypes;
    }
}
