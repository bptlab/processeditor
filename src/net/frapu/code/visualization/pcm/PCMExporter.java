package net.frapu.code.visualization.pcm;

import com.inubit.research.server.ProcessEditorServerUtils;
import net.frapu.code.converter.Exporter;
import net.frapu.code.converter.XPDLExporter;
import net.frapu.code.visualization.ProcessEdge;
import net.frapu.code.visualization.ProcessModel;
import net.frapu.code.visualization.ProcessNode;
import net.frapu.code.visualization.bpmn.BPMNModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates a xml File from an existing PCM Scenario Model.
 *
 * @version 29.10.2014.
 * @author Juliane Imme, Stephan Haarmann
 */
public class PCMExporter implements Exporter {
    private PCMScenario model;

    public PCMExporter(ProcessModel selectedModel) {
        model = (PCMScenario)selectedModel;
    }
    public PCMExporter() {};

    @Override
    public void serialize(File f, ProcessModel m) throws Exception {
        model = (PCMScenario)m;
        String name = "";
        f.createNewFile();
        if (f.getName().endsWith(".pcm.xml")) {
            name = f.toString();
        } else {
            name = f.toString() + ".pcm.xml";
        }
        FileOutputStream fos = new FileOutputStream(name);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
        osw.write("<PCM>\n");
        osw.write("<fragments>\n");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        PCMFragmentExporter fragmentExporter = new PCMFragmentExporter();
        for (ProcessModel fragment : model.getModelList()) {
            File tmp = File.createTempFile("tmp", ".bpmn");
            fragmentExporter.serialize(tmp, fragment);
            Document doc = builder.parse(tmp);
            ProcessEditorServerUtils.writeXMLtoStream(osw, doc);
        }
        osw.write("</fragments>\n");
        writeRules(osw);
        writeReferences(osw);
        writeTermination(osw);
        osw.write("</PCM>");
        osw.flush();
        fos.close();
    }

    private void writeTermination(OutputStreamWriter osw) throws IOException {
        osw.write("<termination>\n");
        osw.write("<dataObject name=\"" + model.getProperty(PCMScenario.PROP_TERMINATION_DO) + "\" state=\""
                + model.getProperty(PCMScenario.PROP_TERMINATION_STATE) + "\"/>\n");
        osw.write("</termination>\n");
    }

    private void writeReferences(OutputStreamWriter osw) throws IOException {
        osw.write("<references>\n");
        for(Map.Entry<String, List<String>> entry : model.getReferences().entrySet()) {
            if(entry.getValue().size()>1) {
                osw.write("<activity id=\"" + entry.getKey() + "\">\n");
                for (String fragmentID : entry.getValue()) {
                    osw.write("<fragment id=\"" + fragmentID + "\"/>\n");
                }
                osw.write("</activity>\n");
            }

        }

        osw.write("</references>\n");
    }

    private void writeRules(OutputStreamWriter osw) throws IOException {
        //osw.write("<rules>\n");

        //osw.write("</rules>\n");
    }

    @Deprecated
    private ProcessModel convertToBPMN(ProcessModel fragment) {
        ProcessModel bpmn = new BPMNModel();
        for (ProcessNode node : fragment.getNodes()) {
            bpmn.addNode(node);
        }
        for (ProcessEdge edge : fragment.getEdges()) {
            bpmn.addEdge(edge);
        }
        return bpmn;
    }

    @Override
    public Set<Class<? extends ProcessModel>> getSupportedModels() {
        Set<Class<? extends ProcessModel>> models = new HashSet<Class<? extends ProcessModel>>();
        models.add(PCMScenario.class);
        return models;
    }

    @Override
    public String getDisplayName() {
        return "PCM export";
    }

    @Override
    public String[] getFileTypes() {
       String[] types = {"pcm.xml"};
       return types;
    }
}
