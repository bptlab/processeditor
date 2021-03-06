/**
 *
 * Process Editor - inubit Workbench Plugin Package
 *
 * (C) 2009, 2010 inubit AG
 * (C) 2014 the authors
 *
 */
package com.inubit.research.gui.plugins;

import com.inubit.research.gui.plugins.choreography.interfaceGenerator.BehavioralInterfaceGeneratingPlugin;
import com.inubit.research.gui.plugins.validationPlugin.ValidationPlugin;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation.UpdateScenarioFromOLCDiff;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation.GenerateOLCFromFragment;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation.GenerateOLCsFromScenario;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation.GeneratePCMFragmentFromMultipleOLC;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author frank
 */
public class PluginHelper {
    private static List<Class<? extends WorkbenchPlugin>> result = new LinkedList<Class<? extends WorkbenchPlugin>>();
    
    static {
        result.add(WorkbenchBirdview.class);
        result.add(NodeViewPlugin.class);
        result.add(PropertiesPlugin.class);
        result.add(ModelOperationsPlugin.class);
        //result.add(CreateVariantOfModel.class);
        //result.add(NaturalLanguageInputPlugin.class);
        //result.add(ProcessModelMergerPlugin.class);
        result.add(BehavioralInterfaceGeneratingPlugin.class);
        result.add(RPSTPlugin.class);
        result.add(ValidationPlugin.class);
        //result.add(ServerLoadTestPlugin.class);
        result.add(AnimationDemoPlugin.class);

        result.add(GenerateOLCFromFragment.class);
        result.add(GenerateOLCsFromScenario.class);
        result.add(GeneratePCMFragmentFromMultipleOLC.class);
        result.add(UpdateScenarioFromOLCDiff.class);
        //result.add(yFilesLayoutPlugin.class);
    }
    

    /**
     * Return all plugins.
     * @return 
     */
    public static List<Class<? extends WorkbenchPlugin>> getPlugins() {
        return result;
    }
    
    /**
     * Removes a plugin.
     */
    public static boolean removePlugin(Class<? extends WorkbenchPlugin> plugin) {
        return result.remove(plugin);
    }
    
    /**
     * Adds a plugin.
     */
    public static boolean addPlugin(Class<? extends WorkbenchPlugin> plugin) {
        return result.add(plugin);
    }
    
    /**
     * Removes all plugins.
     */
    public static void removeAllPlugins() {
        result.clear();
    }
    
}
