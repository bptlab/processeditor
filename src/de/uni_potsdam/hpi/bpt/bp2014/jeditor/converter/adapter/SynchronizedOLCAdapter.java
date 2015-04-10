package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.ObjectLifeCycle;
import de.uni_potsdam.hpi.bpt.bp2014.conversion.olc.SynchronizedObjectLifeCycle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Stpehan on 09.04.2015.
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
    }
}
