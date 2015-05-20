package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is an adapter.
 * It adapts {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.DataObjectState}
 * to {@link de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState}.
 * It allows us to use the DataObjectState nodes from the Processeditor
 * as a node of the converter.
 * In addition it is possible to create a new {@link de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState}
 * with the data from the processeditor object.
 */
public class DataObjectStateAdapter extends de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.DataObjectState {

    /**
     * The Object which will be wrapped by this adapter.
     */
    private DataObjectState dataObjectState;

    /**
     * Creates a new DataObjectState adapter.
     * The name will be set based on the given DataObjectState.
     * @param dataObjectState The DataObjectState which will be wrapped.
     */
    public DataObjectStateAdapter(DataObjectState dataObjectState) {
        super(dataObjectState.getName());
        this.dataObjectState = dataObjectState;
    }
}