package uk.me.eastmans.haexample;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceController;
import uk.me.eastmans.service.ejb.GlobalCounter;
import uk.me.eastmans.service.ejb.HACounterService;
import uk.me.eastmans.service.ejb.ServiceRegistryWrapper;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Created by markeastman on 11/08/2016.
 */
@WebServlet(value="/haexample", name="haexample-servlet")
public class HAExampleServlet  extends GenericServlet {

    private final Logger log = Logger.getLogger(this.getClass().toString());

    private static final String COUNT_VALUE = "countValue";

    // We now want to use a session value to see if they persist across HA redirects
    public void service(ServletRequest req, ServletResponse res)
            throws IOException, ServletException
    {
        StringBuilder message = new StringBuilder(System.getenv("HOSTNAME")); // This will be the pod name
        if (req instanceof HttpServletRequest)
        {
            HttpServletRequest httpReq = (HttpServletRequest) req;
            HttpSession session = httpReq.getSession();
            Integer count = incrementCount( session );
            message.append( " from session " + session.getId() + ", for the " + count + " time " );
            message.append( " and global counter is " + findServiceValue() );
        }
        res.getWriter().println(message);
    }

    private Integer incrementCount(HttpSession session)
    {
        Integer count = (Integer)session.getAttribute(COUNT_VALUE);
        if (count == null)
            count = 1;
        else
            count = count + 1;
        session.setAttribute(COUNT_VALUE,count);
        return count;
    }

    private int findServiceValue()
    {
        final ServiceController<?> requiredService = ServiceRegistryWrapper.getServiceRegistry()
                .getRequiredService(HACounterService.SINGLETON_SERVICE_NAME);
        final Service<?> service = requiredService.getService();
        log.info( "++++++++++++ the service we received is of type " + service.getClass() );
        return (Integer) service.getValue();
    }
}
