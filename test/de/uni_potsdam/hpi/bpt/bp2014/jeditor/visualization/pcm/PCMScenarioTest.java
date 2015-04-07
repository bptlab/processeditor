package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.DataObject;
import net.frapu.code.visualization.bpmn.Task;
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
        assert(model.getReferences().isEmpty());

    }

    /**
     * This Test asserts, that the List of supported Nodes and Edges is empty
     * So that elements can be added manually from within a Process Editor
     */
    @Test
    public void testNoSuggestionsForNodesAndEdges() {
        assert(model.getSupportedNodeClasses().isEmpty());
        assert(model.getSupportedEdgeClasses().isEmpty());
    }

    /**
     * Description is useful
     */
    @Test
    public void testUsefulDescription() {
        assert(model.getDescription().contains("PCM"));
        assert(model.getDescription().contains("Scenario"));
    }

    /**
     * Test if References are correct
     * For every Task a reference entry is created
     * The value is a List containing all PCMFragmentIds which include a Task with the given ID
     */
    @Test
    public void testReferences() {
        assert(model.getReferences().isEmpty());
        PCMFragment fragment1 = new PCMFragment();
        PCMFragment fragment2 = new PCMFragment();
        PCMFragment fragment3 = new PCMFragment();
        Task task1 = new Task();
        Task task2 = new Task();
        fragment1.addNode(task1);
        fragment2.addNode(task1);
        fragment3.addNode(task2);
        model.addPCMFragment(fragment1);
        model.addPCMFragment(fragment2);
        model.addPCMFragment(fragment3);
        model.createNodesForFragments();
        assert(2 == model.getReferences().size());
        assert(2 == model.getReferences().get(task1.getId()).size());
        assert(model.getReferences().get(task1.getId()).contains(fragment1.getId()));
        assert(model.getReferences().get(task1.getId()).contains(fragment2.getId()));
        assert(1 == model.getReferences().get(task2.getId()).size());
        assert(model.getReferences().get(task2.getId()).contains(fragment3.getId()));
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
