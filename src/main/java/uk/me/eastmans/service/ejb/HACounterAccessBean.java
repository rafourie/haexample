package uk.me.eastmans.service.ejb;

import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceController;
import org.jboss.as.server.CurrentServiceContainer;

import javax.ejb.Stateless;
import java.util.logging.Logger;

@Stateless
public class HACounterAccessBean implements HACounterAccess {

    private final Logger log = Logger.getLogger(this.getClass().toString());

    public String getCounterValue() {
        log.info("Method getCounterValue() is invoked");
        final ServiceContainer serviceContainer = CurrentServiceContainer.getServiceContainer();
        if (serviceContainer != null) {
            ServiceController<?> service = serviceContainer.getService(HACounterService.SINGLETON_SERVICE_NAME);

            // Example how to leverage JBoss Logging to do expensive String concatenation only when needed:
            log.info("Service: " + service);

            if (service != null) {
                return service.getValue().toString();
            } else {
                throw new IllegalStateException("Service '" + HACounterService.SINGLETON_SERVICE_NAME + "' not found!");
            }
        } else {
            throw new IllegalStateException("ServiceContainer is null");
        }

    }
}