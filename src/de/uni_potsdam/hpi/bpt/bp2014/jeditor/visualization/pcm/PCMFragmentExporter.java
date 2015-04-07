package de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.pcm;

import net.frapu.code.converter.Exporter;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.Association;
import net.frapu.code.visualization.bpmn.DataObject;
import net.frapu.code.visualization.bpmn.EndEvent;
import net.frapu.code.visualization.bpmn.ExclusiveGateway;
import net.frapu.code.visualization.bpmn.ParallelGateway;
import net.frapu.code.visualization.bpmn.SequenceFlow;
import net.frapu.code.visualization.bpmn.StartEvent;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This Exporter exports PCMFragments to the BPMN exchange Format.
 * There for it uses the camunda-xml-Model and camunda-bpmn-model lib.
 *
 * @version 07.11.2014.
 * @author Juliane Imme, Stephan Haarmann
 * @Deprecated The Server uses another representation
 */
@Deprecated
public class PCMFragmentExporter implements Exporter {
    private BpmnModelInstance modelInstance;
    private Map<String, BpmnModelElementInstance> nodes = new HashMap<>();

    /**
     * Saves a given model to a given File
     * @param f the file where the model should be saved
     * @param m the model to be serialized
     * @throws Exception
     */
    @Override
    public void serialize(File f, ProcessModel m) throws Exception {
        f.createNewFile();
        String name = "";
        if (f.getName().endsWith(".bpmn")) {
            name = f.toString();
        } else {
            name = f.toString() + ".bpmn";
        }
        modelInstance = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://bpt.hpi.uni-potsdam.de/pcm");
        modelInstance.setDefinitions(definitions);

        org.camunda.bpm.model.bpmn.instance.Process process = createElement(definitions, m.getProcessName(),
                org.camunda.bpm.model.bpmn.instance.Process.class);
        String processName = m.getProcessName();
        if (null == processName || processName.isEmpty()) {
            processName = "Process";
        }
        process.setName(processName);

        FileOutputStream fos = new FileOutputStream(name);
        for (ProcessNode n : m.getNodes()) {
            createNode(process, n);
        }
        for (ProcessEdge e : m.getEdges()) {
            createEdge(process, e);
        }
        Bpmn.writeModelToStream(fos, modelInstance);
    }

    /**
     * First Decides about the correct subtype of the Edge and then adds the edge to the Process. The Edge will
     * correctly refer the source and target
     *
     * @param process The Process which contains the Edge
     * @param e The Edge which have to be added to the Process
     */
    private void createEdge(Process process, ProcessEdge e) {
        if (e instanceof SequenceFlow) {
            FlowNode source = ((FlowNode)nodes.get(e.getSource().getId()));
            FlowNode target = ((FlowNode)nodes.get(e.getTarget().getId()));
            org.camunda.bpm.model.bpmn.instance.SequenceFlow sf = createSequenceFlow(process, source, target);
        } else if (e instanceof Association) {
            BaseElement source = ((BaseElement)nodes.get(e.getSource().getId()));
            BaseElement target = ((BaseElement)nodes.get(e.getTarget().getId()));
            String identifier = source.getId() + "-" + target.getId();
            org.camunda.bpm.model.bpmn.instance.Association association = createElement(process, identifier,  org.camunda.bpm.model.bpmn.instance.Association.class);
            process.addChildElement(association);
            association.setSource(source);
            association.setTarget(target);
        }
    }

    /**
     * Creates a new node for a camunda bpmn model. It converts nodes of the ProcessEditor to nodes of the Camunda
     * BPMN Model
     * @param process
     * @param n the (ProcessEditor)node which is the blueprint for the new (Camunda) bpmn model node
     */
    private void createNode(Process process, ProcessNode n) {
        String nodeName = n.getName();
        if (null == nodeName || "".equals(nodeName)) {
            nodeName = "Node";
        }
        if (n instanceof net.frapu.code.visualization.bpmn.Task) {
            UserTask task = createElement(process, "n" + n.getId(), UserTask.class);
            task.setName(nodeName);
            nodes.put(n.getId(), task);
        } else if (n instanceof StartEvent) {
            org.camunda.bpm.model.bpmn.instance.StartEvent startEvent = createElement(process,"n" +  n.getId(), org.camunda.bpm.model.bpmn.instance.StartEvent.class);
            startEvent.setName(nodeName);
            nodes.put(n.getId(), startEvent);
        } else if (n instanceof EndEvent) {
            org.camunda.bpm.model.bpmn.instance.EndEvent endEvent = createElement(process,"n" +  n.getId(), org.camunda.bpm.model.bpmn.instance.EndEvent.class);
            endEvent.setName(nodeName);
            nodes.put(n.getId(), endEvent);
        } else if (n instanceof ExclusiveGateway) {
            org.camunda.bpm.model.bpmn.instance.ExclusiveGateway gateway = createElement(process,"n" +  n.getId(), org.camunda.bpm.model.bpmn.instance.ExclusiveGateway.class);
            gateway.setName(nodeName);
            nodes.put(n.getId(), gateway);
        } else if (n instanceof ParallelGateway) {
            org.camunda.bpm.model.bpmn.instance.ParallelGateway gateway = createElement(process,"n" +  n.getId(), org.camunda.bpm.model.bpmn.instance.ParallelGateway.class);
            gateway.setName(nodeName);
            nodes.put(n.getId(), gateway);
        } else if (n instanceof DataObject) {
            org.camunda.bpm.model.bpmn.instance.DataObject dataObject = createElement(process, "n" + n.getId(), org.camunda.bpm.model.bpmn.instance.DataObject.class);
            dataObject.setName(nodeName);
            DataState state = createElement(dataObject, "n" + n.getProperty("state").replace(' ', '_'), DataState.class);
            state.setName(n.getProperty("state"));
            dataObject.setDataState(state);
            dataObject.setCollection("1".equals(n.getProperty("collection")));
            nodes.put(n.getId(), dataObject);
        }
    }

    /**
     * Creates a new Sequence flow for the bpmn model.
     *
     * @param process the process which contains the sequence flow
     * @param from the source of the sequence flow
     * @param to the target of the sequence flow
     * @return the created sequenceFlow
     */
    private org.camunda.bpm.model.bpmn.instance.SequenceFlow createSequenceFlow(Process process, FlowNode from, FlowNode to) {
        String identifier = from.getId() + "-" + to.getId();
        org.camunda.bpm.model.bpmn.instance.SequenceFlow sequenceFlow = createElement(process, identifier, org.camunda.bpm.model.bpmn.instance.SequenceFlow.class);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);
        return sequenceFlow;
    }

    /**
     * a generic method to ereate a new element of an (Camunda) BPMN-Model. It is used by createEdge and createNode
     * @param parentElement the new Element will be added to the parentElement
     * @param id the id of the new Element
     * @param elementClass The class of the element
     * @param <T> The Genereric return Type (see elementClass)
     * @return the just created element
     */
    protected <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String id, Class<T> elementClass) {
        T element = modelInstance.newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        parentElement.addChildElement(element);
        return element;
    }

    /**
     * This Exporter can only be used to export PCMFragments or highly restricted BPMN-Models
     *
     * @return a List containing only the PCMFragment.class
     */
    @Override
    public Set<Class<? extends ProcessModel>> getSupportedModels() {
        Set<Class<? extends ProcessModel>> models = new HashSet<>();
        models.add(PCMFragment.class);
        return models;
    }

    /**
     * The Name of the exporter for better human readability
     * @return The Name to be shown in the export dialog
     */
    @Override
    public String getDisplayName() {
        return "PCMFragment (BPMN)";
    }


    /**
     * The Supported FileTypes
     * @return an StringArray which contains only ".bpmn" as a file ending
     */
    @Override
    public String[] getFileTypes() {
        return new String[]{".bpmn"};
    }
}
