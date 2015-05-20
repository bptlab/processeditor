package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class adapts a {@link de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition} to a
 * {@link StateTransition}.
 * Therefore, it extends the StateTransition class. In addition it overrides all methods specified by the
 * IEdge interface.
 * In addition it provides the possibility to access the uri and the synchronized edges properties.
 * Which can be used to initialize the combined transitions.
 */
public class StateTransitionAdapter extends StateTransition {
    /**
     * The stateTransition which will be wrapped by this adapter.
     */
    private final de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition stateTransition;

    /**
     * Creates a new Adapter for a state transition.
     * The label will be initialized to the value of the given object.
     * The object will be saved for further operations.
     *
     * @param stateTransition The state transition which will be wrapped.
     */
    public StateTransitionAdapter(de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.StateTransition stateTransition) {
        super();
        this.setLabel(stateTransition.getLabel());
        this.stateTransition = stateTransition;
    }

    /**
     * The URI which identifies the element of the ProcessEditor object.
     * @return Returns the uri property. The request will be delegated to the wrapped object.
     */
    public String getURI() {
        return stateTransition.getProperty("#uri");
    }

    /**
     * The URIs which identify the synchronized edges.
     * This method delegates the request to the wrapped object and creates a new Collection.
     * @return The Collection of URIs which represent the synchronized edges.
     */
    public Collection<String> getSynchronizedEdgeURIs() {
        Set<String> uris = new HashSet<>();
        uris.addAll(
                Arrays.asList(stateTransition.getProperty(stateTransition.PROP_SYNCHRONIZED).split(","))
        );
        return uris;
    }
}
