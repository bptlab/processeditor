package net.frapu.code.visualization.pcm;

import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.DataObject;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stephan
 * @version 15.12.2014.
 */
public class PCMScenarioTest {

    PCMScenario model;

    @Before
    public void setUp() {
        model = new PCMScenario();
    }

    //Tests
    @Test
    public void testEmptyScenarioAtBegin() {
        assert(model.getNodesByClass(PCMFragmentNode.class).isEmpty());
        assert(model.getNodesByClass(PCMDataObjectNode.class).isEmpty());
    }

    @Test
    public void testMaxLimitOfCollectionNodes() {
        for (int i = 0; i < 2; i++) {
            model.addNode(new PCMFragmentCollection());
            model.addNode(new PCMDataObjectCollection());
            assert (1 == model.getNodesByClass(PCMFragmentCollection.class).size());
            assert (1 == model.getNodesByClass(PCMDataObjectCollection.class).size());
        }
    }

    @Test
    public void testMinLimitOfCollectionNodes() {
        assert(1 == model.getNodesByClass(PCMFragmentCollection.class).size());
        assert(1 == model.getNodesByClass(PCMDataObjectCollection.class).size());
        model.removeNode(model.getNodesByClass(PCMFragmentCollection.class).get(0));
        model.removeNode(model.getNodesByClass(PCMDataObjectCollection.class).get(0));
        assert(1 == model.getNodesByClass(PCMFragmentCollection.class).size());
        assert(1 == model.getNodesByClass(PCMDataObjectCollection.class).size());
    }

    @Test
    public void testOnlyUniqueDataObjects() {
        ProcessNode data1 = new PCMDataObjectNode();
        data1.setProperty(ProcessNode.PROP_TEXT, "data");
        model.addNode(data1);
        assert(1 == model.getNodesByClass(PCMDataObjectNode.class).size());
        ProcessNode data2 = new PCMDataObjectNode();
        data2.setProperty(ProcessNode.PROP_TEXT, "data");
        model.addNode(data2);
        assert(1 == model.getNodesByClass(PCMDataObjectNode.class).size());
    }

    @Test
    public void testCreatesNodeForEveryFragment() {
        assert(model.getNodesByClass(PCMFragmentNode.class).isEmpty());
        addFragmentNodeToModel("Test1");
        assert(0 == model.getNodesByClass(PCMFragmentNode.class).size());
        model.createNodesForFragments();
        assert(1 == model.getNodesByClass(PCMFragmentNode.class).size());
        addFragmentNodeToModel("Test2");
        addFragmentNodeToModel("Test3");
        model.createNodesForFragments();
        assert(3 == model.getNodesByClass(PCMFragmentNode.class).size());
    }

    @Test
    public void testReset() {
        PCMFragment fragment = new PCMFragment();
        fragment.setProcessName("Test");
        ProcessNode data = new DataObject();
        data.setProperty(ProcessNode.PROP_TEXT, "Test Data");
        fragment.addNode(data);
        model.addPCMFragment(fragment);
        model.createNodesForFragments();
        assert(!model.getNodesByClass(PCMFragmentNode.class).isEmpty());
        model.createDataList();
        assert(!model.getNodesByClass(PCMDataObjectNode.class).isEmpty());
        model.reset();
        assert(model.getNodesByClass(PCMFragmentNode.class).isEmpty());
        assert(model.getNodesByClass(PCMDataObjectNode.class).isEmpty());
        assert(model.getModelList().isEmpty());

    }

    // Support Functions
    /**
     * Adds a node for a fragment with the given name to the scenario
     * @param name
     */
    private void addFragmentNodeToModel(String name) {
        PCMFragment fragment = new PCMFragment();
        fragment.setProcessName(name);
        model.addPCMFragment(fragment);
    }
}
