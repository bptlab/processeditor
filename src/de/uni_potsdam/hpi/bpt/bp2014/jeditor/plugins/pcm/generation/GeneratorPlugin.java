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
 * This class is an abstract implementation of a Generator Plugin.
 * It extends the workbench plugin in order to be used as such.
 * In addition it implements the getMenuEntry method of that class
 * with a template method which can be used for the generation.
 *
 * Every generation follows these steps:
 * 0. initialize all necessary Objects and Collections
 * 1. connect to the server
 * 2. extract the Models from the server
 * 3. wrap the Models in an adapter
 * 4. generate new models using the BPMConverter module
 * 5. convert those models in a way that they can be opened in the workbench
 * 6. open the models inside the workbench
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

    /**
     * This method is called in order to determine the label of the menu item.
     * It should describe the transformation performed by the concrete implementation.
     * @return A String representing the short description of this plugin.
     */
    protected abstract String getName();

    /**
     * Initializes all necessary Objects and collections.
     */
    protected abstract void initialize();

    /**
     * Extracts all Models needed from the root directory of the server.
     * @param directory The root directory of the server.
     * @return A Collection of process models. The models extracted.
     */
    protected abstract Collection<? extends ProcessModel> extractModelsFromDirectory(ModelDirectory directory);

    /**
     * This models performs the transformation. It means it will generate the new models.
     * @param wrappedModels The wrapped models which will be source for the converter
     * @return A Collection of generated models.
     */
    protected abstract Collection<? extends IModel> generateModels(Collection<? extends IModel> wrappedModels);

    /**
     * Prepares the {@link ProcessModel} for the transformation.
     * In general this means they will be wrapped in an adapter such that they implement the {@link IModel} interface.
     * @param models The extracted models.
     * @return The models wrapped in adapters.
     */
    protected abstract Collection<? extends IModel> wrapModels(Collection<? extends ProcessModel> models);

    /**
     * This method will create a ProcessModel that can be opened inside the ProcesssEditor for every Model
     * returned by the {@link #generateModels(Collection)} method.
     * @param generatedModels The models generated by the {@link #generateModels(Collection)} method.
     * @return The models converted such that they can be opened and displayed by the Workbench.
     */
    protected abstract Collection<? extends ProcessModel> convertModels(Collection<? extends IModel> generatedModels);

    /**
     * Opens every model inside the workbench.
     * @param convertedModel The models which should be opened. It is the output of the method {@link #convertModels(Collection)}.
     */
    protected void openModels(Collection<? extends ProcessModel> convertedModel) {
        for (ProcessModel processModel : convertedModel) {
            wb.openNewModel(processModel);
        }
    }
}
