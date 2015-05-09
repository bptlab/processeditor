package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Activity;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.ActivityCentricProcessModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Event;
import net.frapu.code.converter.ConverterHelper;
import net.frapu.code.visualization.ProcessModel;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Stpehan on 09.05.2015.
 */
public class ACPMAdapterTest {
    private final String sequenceModel = "test/de/uni_potsdam/hpi/bpt/bp2014/jeditor/converter/adapter/acpm/SimpleSequence.model";

    @Test
    public void testSimpleSequence() throws Exception {
        List<ProcessModel> models  = ConverterHelper.importModels(new File(sequenceModel));
        assertEquals("There should be only one model " + sequenceModel, 1, models.size());
        ActivityCentricProcessModel model = new ACPMAdapter(models.get(0));
        assertEquals("The adapter should have exactly 3 nodes", 3, model.getNodes().size());
        assertEquals("The adapter should have exactly 1 Activity", 1, model.getNodesOfClass(Activity.class).size());
        assertEquals("The adapter should have exactly 2 Events", 2, model.getNodesOfClass(Event.class).size());
        assertTrue("The start node should be an Event", model.getStartNode() instanceof Event);
        assertEquals("The start node should be an StartEvent", Event.Type.START, ((Event) model.getStartNode()).getType());
        assertEquals("The start node should have only one successor", 1, model.getStartNode().getOutgoingEdges().size());
        assertTrue("The second node should be an Activity", model.getStartNode().getOutgoingEdges().iterator().next() instanceof Activity);
        assertEquals("The Activity have only one successor", 1, model.getNodesOfClass(Activity.class).iterator().next().getOutgoingEdges().size());
        assertTrue("The successor of the Activity should be an Event", model.getNodesOfClass(Activity.class).iterator().next().getOutgoingEdges().iterator().next().getTarget() instanceof Event);
        assertEquals("The successor of the Activity should be an Event", Event.Type.END, ((Event)model.getNodesOfClass(Activity.class).iterator().next().getOutgoingEdges().iterator().next().getTarget()).getType());
        System.out.println("");
    }


}
