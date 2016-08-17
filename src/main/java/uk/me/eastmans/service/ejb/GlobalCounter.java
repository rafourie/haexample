package uk.me.eastmans.service.ejb;

import javax.ejb.Remote;

/**
 * Created by markeastman on 11/08/2016.
 */
@Remote
public interface GlobalCounter {
    void reset();
    int increment();
}
