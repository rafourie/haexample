package uk.me.eastmans.service.ejb;

import org.jboss.msc.service.ServiceRegistry;

/**
 * Created by markeastman on 17/08/2016.
 */
public class ServiceRegistryWrapper {
    private static volatile ServiceRegistry serviceRegistry;

    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public static synchronized void setServiceRegistry(final ServiceRegistry serviceRegistry) {
        if (ServiceRegistryWrapper.serviceRegistry == null) {
            ServiceRegistryWrapper.serviceRegistry = serviceRegistry;
        }
    }
}
