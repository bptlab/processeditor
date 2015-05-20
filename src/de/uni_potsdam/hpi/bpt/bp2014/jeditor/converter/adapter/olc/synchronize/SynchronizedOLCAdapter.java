package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.synchronize;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.synchronize.SynchronizedObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.OLCAdapter;

import java.util.*;

/**
 * This Class represents an adapter for the synchronized object life cycles.
 * A Synchronized Object Life cycles is based on a collection of Object Life Cycles.
 * And on synchronization edges.
 */
public class SynchronizedOLCAdapter extends SynchronizedObjectLifeCycle {
    /**
     * Holds the Collection of Object Life Cycles wrapped by this SynchronizedOLCAdapter.
     * The aggregation of all this life cycles is the synchronized olc.
     */
    Set<ObjectLifeCycle> olcs;

    /**
     * Creates a new synchronized object life cycle adapter based on a collection
     * of Object Life Cycles.
     * First every Object life cycle will be wrapped with an adapter.
     * Afterwards the synchronization edges will be initialized.
     * @param olcs The Object Life Cycles which represent the synchronized OLC.
     */
    public SynchronizedOLCAdapter(Collection<de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle> olcs) {
        super();
        this.olcs = new HashSet<>();
        for (de.uni_potsdam.hpi.bpt.bp2014.jeditor.visualization.olc.ObjectLifeCycle olc : olcs) {
            ObjectLifeCycle adapted = (ObjectLifeCycle) new OLCAdapter(olc);
            this.olcs.add(adapted);
        }
        super.getOLCs().addAll(this.olcs);
        initSynchronizationEdges();
    }

    /**
     * This method initializes the synchronization edges.
     * Therefore it extracts all state transitions from all the object life cycles.
     * Afterwards those transitions will be grouped.
     * Which means for every transition all the transitions referred with the synchronized
     * edges property will determined and clustered.
     * The result will be saved inside the synchronisationEdges map.
     */
    private void initSynchronizationEdges() {
        List<StateTransition> transitions = new LinkedList<>();
        for (ObjectLifeCycle olc : olcs) {
            for (INode node : olc.getNodes()) {
                for (IEdge edge : node.getOutgoingEdges()) {
                    transitions.add((StateTransition)edge);
                }
            }
        }
        for (StateTransition transition : transitions) {
            if (transition instanceof de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.StateTransitionAdapter) {
                Set<StateTransition> synchronizedTransitions = new HashSet<>();
                for (StateTransition possibleSynchronizedTransition : transitions) {
                    if (possibleSynchronizedTransition instanceof de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.StateTransitionAdapter &&
                            ((de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.StateTransitionAdapter) transition)
                                    .getSynchronizedEdgeURIs()
                                    .contains(((de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.StateTransitionAdapter) possibleSynchronizedTransition).getURI()) ||
                            ((de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.StateTransitionAdapter)possibleSynchronizedTransition)
                                    .getSynchronizedEdgeURIs()
                                    .contains(((de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.StateTransitionAdapter) transition).getURI())) {
                        synchronizedTransitions.add(possibleSynchronizedTransition);
                    }
                }
                if (!super.getSynchronisationEdges().containsKey(transition)) {
                    super.getSynchronisationEdges().put(transition, new LinkedList<StateTransition>());
                }
                super.getSynchronisationEdges().get(transition).addAll(synchronizedTransitions);
            }
        }
    }

}
