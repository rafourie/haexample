package uk.me.eastmans.service.ejb;

import javax.ejb.Remote;

/**
 * Created by markeastman on 16/08/2016.
 */
@Remote
public interface HACounterAccess {
    /**
     * Provide the node name where the scheduler is started.
     *
     * @return name of the cluster node where the schedule timer is running
     */
    String getCounterValue();
}
