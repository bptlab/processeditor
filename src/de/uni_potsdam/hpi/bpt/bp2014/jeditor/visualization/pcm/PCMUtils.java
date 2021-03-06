package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import com.inubit.research.layouter.ProcessLayouter;
import com.inubit.research.layouter.freeSpace.FreeSpaceLayouter;
import com.inubit.research.layouter.gridLayouter.GridLayouter;
import com.inubit.research.layouter.sugiyama.SugiyamaLayoutAlgorithm;
import net.frapu.code.visualization.*;
import net.frapu.code.visualization.bpmn.*;
import net.frapu.code.visualization.layouter.SimpleBPDLayouter;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is the {@link net.frapu.code.visualization.ProcessUtils}class of the PCM Models
 * ({@link PCMFragment} and Scenario).
 *
 * @author Juliane Imme, Stephan Haarmann
 * @version 28.10.2014.
 */
public class PCMUtils extends ProcessUtils {
    List layouters = null;

    /**
     * Determines the default ProcessEdge. Association if one of source and target is a DataObject. Else a SequenceFlow
     * @param source the source of the new edge
     * @param target the target of the new edge
     * @return the created Edge
     */
    @Override
    public ProcessEdge createDefaultEdge(ProcessNode source, ProcessNode target) {
        if (source instanceof DataObject || target instanceof DataObject) {
            return new Association(source, target);
        }
        return new SequenceFlow(source, target);
    }

    /**
     * Because PCM Fragments are BPMN-Models we can use the same Layouters
     *
     * @return a List of all possible {@link com.inubit.research.layouter.ProcessLayouter}
     */
    @Override
    public List<ProcessLayouter> getLayouters() {
        if (null == layouters) {
            layouters = new LinkedList<ProcessLayouter>();
            layouters.add(new GridLayouter(Configuration.getProperties()));
            layouters.add(new SimpleBPDLayouter());
            layouters.add(new SugiyamaLayoutAlgorithm(Configuration.getProperties()));
            layouters.add(new SugiyamaLayoutAlgorithm(false,Configuration.getProperties()));
            layouters.add(new FreeSpaceLayouter());
        }
        return layouters;
    }

    /**
     * Creates a recommendation for the Model. A Start Event is only recomended at the start the EndEvent only if
     * there is at least one element between the EndEvent and the StartEvent.
     * @param model The ProcessModel
     * @param node The ProcessNode used for recommandation
     * @return
     */
    @Override
    public List<Class<? extends ProcessNode>> getNextNodesRecommendation(ProcessModel model, ProcessNode node) {
        List<Class<? extends  ProcessNode>> recommendations = new LinkedList<Class<? extends ProcessNode>>();
        if (!(node instanceof EndEvent)) {
            recommendations.add(Task.class);
            recommendations.add(ExclusiveGateway.class);
            recommendations.add(ParallelGateway.class);
            if (!(node instanceof StartEvent)) {
                recommendations.add(EndEvent.class);
                if (!(node instanceof Gateway)) {
                    recommendations.add(DataObject.class);
                }
            }
        }
        return recommendations;
    }
}
