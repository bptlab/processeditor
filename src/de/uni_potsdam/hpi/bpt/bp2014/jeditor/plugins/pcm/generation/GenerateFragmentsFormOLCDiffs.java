package de.uni_potsdam.hpi.bpt.bp2014.jeditor.plugins.pcm.generation;

import com.inubit.research.client.ModelDirectory;
import com.inubit.research.gui.Workbench;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.IModel;
import net.frapu.code.visualization.ProcessModel;

import java.util.Collection;

/**
 * Created by Stpehan on 17.05.2015.
 */
public class GenerateFragmentsFormOLCDiffs extends GeneratorPlugin {
    public GenerateFragmentsFormOLCDiffs(Workbench wb) {
        super(wb);
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected Collection<? extends ProcessModel> extractModelsFromDirectory(ModelDirectory directory) {
        return null;
    }

    @Override
    protected Collection<? extends IModel> generateModels(Collection<? extends IModel> wrappedModels) {
        return null;
    }

    @Override
    protected Collection<? extends IModel> wrapModels(Collection<? extends ProcessModel> models) {
        return null;
    }

    @Override
    protected Collection<? extends ProcessModel> convertModels(Collection<? extends IModel> generatedModels) {
        return null;
    }
}
