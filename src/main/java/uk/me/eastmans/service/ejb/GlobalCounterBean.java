package uk.me.eastmans.service.ejb;

import javax.ejb.Singleton;

/**
 * Created by markeastman on 11/08/2016.
 */
@Singleton
public class GlobalCounterBean implements GlobalCounter {
    private int counter = 0;

    @Override
    public void reset()
    {
        counter = 0;
    }

    @Override
    public int increment ()
    {
        return counter++;
    }
}
