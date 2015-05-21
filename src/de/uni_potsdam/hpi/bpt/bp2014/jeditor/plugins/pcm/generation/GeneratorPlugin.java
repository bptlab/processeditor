package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.*;
import com.inubit.research.gui.Workbench;
import com.inubit.research.gui.WorkbenchConnectToServerDialog;
import com.inubit.research.gui.plugins.WorkbenchPlugin;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import net.frapu.code.visualization.Configuration;
import net.frapu.code.visualization.ProcessModel;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * This clas
 */
public abstract class GeneratorPlugin extends WorkbenchPlugin {
    protected Workbench wb;

    public GeneratorPlugin(Workbench wb) {
        this.wb = wb;
    }

    @Override
    public Component getMenuEntry() {
        JMenuItem menuItem = new JMenuItem(getName());
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initialize();
                WorkbenchConnectToServerDialog connectDialog = new WorkbenchConnectToServerDialog(wb, wb, false);
                connectDialog.pack();
                connectDialog.setVisible(true);
                Configuration conf = Configuration.getInstance();
                try {
                    ModelServer server = new ModelServer(new URI(conf.getProperty(WorkbenchConnectToServerDialog.CONF_SERVER_URI)), "/",
                            new UserCredentials(conf.getProperty(WorkbenchConnectToServerDialog.CONF_SERVER_URI),
                                    conf.getProperty(WorkbenchConnectToServerDialog.CONF_SERVER_USER),
                                    conf.getProperty(WorkbenchConnectToServerDialog.CONF_SERVER_PASSWORD)));
                    ModelDirectory directory = server.getDirectory();
                    openModels(convertModels(generateModels(wrapModels(extractModelsFromDirectory(directory)))));
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                } catch (InvalidUserCredentialsException e1) {
                } catch (XMLHttpRequestException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (XPathExpressionException e1) {
                    e1.printStackTrace();
                } catch (ParserConfigurationException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        return menuItem;
    }

    protected abstract String getName();
    protected abstract void initialize();
    protected abstract Collection<? extends ProcessModel> extractModelsFromDirectory(ModelDirectory directory);
    protected abstract Collection<? extends IModel> generateModels(Collection<? extends IModel> wrappedModels);
    protected abstract Collection<? extends IModel> wrapModels(Collection<? extends ProcessModel> models);
    protected abstract Collection<? extends ProcessModel> convertModels(Collection<? extends IModel> generatedModels);

    protected void openModels(Collection<? extends ProcessModel> convertedModel) {
        for (ProcessModel processModel : convertedModel) {
            wb.openNewModel(processModel);
        }
    }
}
