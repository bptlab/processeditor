package de.uni_potsdam.hpi.bpt.bp2014.jeditor.converter.adapter.acpm;

import de.uni_potsdam.hpi.bpt.bp2014.conversion.activity_centric.Gateway;
import net.frapu.code.visualization.ProcessNode;

/**
 * This is an Adapter for a Gateway.
 * A Gateway can either be a {@link net.frapu.code.visualization.bpmn.ExclusiveGateway} or
 * a {@link net.frapu.code.visualization.bpmn.ParallelGateway}.
 * This type will be set from the outside.
 */
public class GatewayAdapter extends Gateway {
    /**
     * The gateway wrapped by this adapter.
     */
    private net.frapu.code.visualization.bpmn.Gateway gateway;

    /**
     * Creates a new instance.
     * @param processNode The Gateway node to be wrapped.
     */
    public GatewayAdapter(net.frapu.code.visualization.bpmn.Gateway processNode) {
        gateway = processNode;
    }
}
