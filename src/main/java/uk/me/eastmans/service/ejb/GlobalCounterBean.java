package uk.me.eastmans.service.ejb;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import java.util.logging.Logger;

/**
 * Created by markeastman on 11/08/2016.
 */
@Singleton
@Remote (GlobalCounter.class)
public class GlobalCounterBean implements GlobalCounter {

    private final Logger log = Logger.getLogger(this.getClass().toString());

    private int counter = 0;

    public GlobalCounterBean()
    {
        log.info( "+++++++++++++ GlobalCounterBean created" );
    }

    public void reset()
    {
        counter = 0;
    }

    public int increment ()
    {
        return ++counter;
    }
}
