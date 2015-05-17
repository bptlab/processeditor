package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.ModelDescription;
import com.inubit.research.client.ModelDirectory;
import com.inubit.research.client.ModelDirectoryEntry;
import com.inubit.research.gui.Workbench;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.converter.olc.FragmentsFromOLCVersions;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.OLCAdapter;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle;
import net.frapu.code.visualization.ProcessModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class GenerateFragmentsFormOLCDiffs extends GeneratePCMFragmentFromMultipleOLC {
    Map<ObjectLifeCycle, ObjectLifeCycle> predecessors;
    Map<OLCAdapter, OLCAdapter> wrappedPredecessors;

    public GenerateFragmentsFormOLCDiffs(Workbench wb) {
        super(wb);
    }

    @Override
    protected String getName() {
        return "Generate Fragments From OLC Versions";
    }

    @Override
    protected Collection<? extends ProcessModel> extractModelsFromSubDirectory(ModelDirectory directory) {
        for (ModelDirectoryEntry directoryEntry : directory.getEntries()) {
            if (directoryEntry instanceof ModelDirectory) {
                extractModelsFromSubDirectory((ModelDirectory) directoryEntry);
            } else if (directoryEntry instanceof ModelDescription) {
                try {
                    ProcessModel model = ((ModelDescription) directoryEntry).getHead().getProcessModel();
                    ProcessModel predecessor = ((ModelDescription) directoryEntry).getPredecessors(
                            ((ModelDescription)directoryEntry).getHead()).iterator().next().getProcessModel();
                    if (model instanceof ObjectLifeCycle) {
                        predecessors.put((ObjectLifeCycle)model, (ObjectLifeCycle)predecessor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return predecessors.keySet();
    }

    @Override
    protected Collection<? extends IModel> generateModels(Collection<? extends IModel> wrappedModels) {
        FragmentsFromOLCVersions ffOLCv = new FragmentsFromOLCVersions();
        return ffOLCv.convert(wrappedPredecessors.keySet(), wrappedPredecessors.values());
    }

    @Override
    protected Collection<? extends IModel> wrapModels(Collection<? extends ProcessModel> models) {
        for (ProcessModel olc : models) {
            wrappedPredecessors.put(new OLCAdapter((ObjectLifeCycle) olc),
                    new OLCAdapter(predecessors.get(olc)));
        }
        return wrappedPredecessors.keySet();
    }


    @Override
    protected void initialize() {
        super.initialize();
        predecessors = new HashMap<>();
        wrappedPredecessors = new HashMap<>();
    }
}