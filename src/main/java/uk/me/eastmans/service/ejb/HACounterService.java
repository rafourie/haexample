package uk.me.eastmans.service.ejb;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Created by markeastman on 11/08/2016.
 */
public class HACounterService implements Service<String> {
    private final Logger log = Logger.getLogger(this.getClass().toString());
    public static final ServiceName SINGLETON_SERVICE_NAME = ServiceName.JBOSS.append("openshift", "ha", "singleton", "counter");

    /**
     * A flag whether the service is started.
     */
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * @return the name of the server node
     */
    public String getValue() throws IllegalStateException, IllegalArgumentException {
        if (!started.get()) {
            throw new IllegalStateException("The service '" + this.getClass().getName() + "' is not ready!");
        }
        log.info( "HACounterService.getValue() on pod " + System.getenv("HOSTNAME") );
        return System.getenv("HOSTNAME");
    }

    public void start(StartContext arg0) throws StartException {
        log.info( "HACounterService.start() on pod " + System.getenv("HOSTNAME") );
        if (!started.compareAndSet(false, true)) {
            throw new StartException("The service is still starting!");
        }
        // Nothing to really start
        try {
            InitialContext ic = new InitialContext();
            ((GlobalCounter) ic.lookup("global/ROOT/GlobalCounterBean"))
                    .reset();
        } catch (NamingException e) {
            throw new StartException("Could not reset global counter", e);
        }
    }

    public void stop(StopContext arg0) {
        log.info( "HACounterService.stop() on pod " + System.getenv("HOSTNAME") );
        if (started.compareAndSet(true, false)) {
            // Nothing to really stop
            try {
                InitialContext ic = new InitialContext();
                ((GlobalCounter) ic.lookup("global/ROOT/GlobalCounterBean"))
                        .reset();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }

}
