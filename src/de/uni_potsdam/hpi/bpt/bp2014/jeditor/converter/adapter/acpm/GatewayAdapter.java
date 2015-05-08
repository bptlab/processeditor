package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Gateway;
import net.frapu.code.visualization.ProcessNode;

/**
 * Created by Stpehan on 07.05.2015.
 */
public class GatewayAdapter extends Gateway {
    private net.frapu.code.visualization.bpmn.Gateway gateway;

    public GatewayAdapter(net.frapu.code.visualization.bpmn.Gateway processNode) {
        gateway = processNode;
    }
}
