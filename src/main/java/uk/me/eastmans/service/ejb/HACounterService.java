package uk.me.eastmans.service.ejb;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by markeastman on 11/08/2016.
 */
public class HACounterService implements Service<String> {
    public static final ServiceName SINGLETON_SERVICE_NAME = ServiceName.JBOSS.append("openshift", "ha", "singleton", "counter");
    /**
     * A flag whether the service is started.
     */
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * @return the name of the server node
     */
    public String getValue() throws IllegalStateException, IllegalArgumentException {
        return System.getenv("HOSTNAME");
    }

    public void start(StartContext arg0) throws StartException {
        if (!started.compareAndSet(false, true)) {
            throw new StartException("The service is still starting!");
        }
        // Nothing to really start
        try {
            InitialContext ic = new InitialContext();
            ((GlobalCounter) ic.lookup("global/ROOT/GlobalCounterBean!uk.me.eastmans.service.ejb.GlobalCounter"))
                    .reset();
        } catch (NamingException e) {
            throw new StartException("Could not reset global counter", e);
        }
    }

    public void stop(StopContext arg0) {
        if (started.compareAndSet(true, false)) {
            // Nothing to really stop
            try {
                InitialContext ic = new InitialContext();
                ((GlobalCounter) ic.lookup("global/ROOT/GlobalCounterBean!uk.me.eastmans.service.ejb.GlobalCounter"))
                        .reset();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }

}
