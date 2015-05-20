package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.synchronize;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.IEdge;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.INode;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.StateTransition;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.synchronize.SynchronizedObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.olc.OLCAdapter;

import java.util.*;

/**
 * This
 */
public class SynchronizedOLCAdapter extends SynchronizedObjectLifeCycle {
    Set<ObjectLifeCycle> olcs;

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
