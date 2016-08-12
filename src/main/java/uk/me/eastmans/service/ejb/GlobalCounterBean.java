package uk.me.eastmans.service.ejb;

import javax.ejb.Singleton;
import java.util.logging.Logger;

/**
 * Created by markeastman on 11/08/2016.
 */
@Singleton
public class GlobalCounterBean implements GlobalCounter {

    private final Logger log = Logger.getLogger(this.getClass().toString());

    private int counter = 0;

    public GlobalCounterBean()
    {
        log.info( "+++++++++++++ GlobalCounterBean created" );
    }

    @Override
    public void reset()
    {
        counter = 0;
    }

    @Override
    public int increment ()
    {
        return ++counter;
    }
}
