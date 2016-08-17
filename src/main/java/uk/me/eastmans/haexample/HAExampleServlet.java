package uk.me.eastmans.haexample;

import uk.me.eastmans.service.ejb.GlobalCounter;

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
            // We now want to add the singleton counter bean to get a global counter
            message.append( " and global counter is " + incrementSingletonCounter() );
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

    private int incrementSingletonCounter()
    {
        Context context = null;
        try {
            final Hashtable jndiProperties = new Hashtable();
            jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            jndiProperties.put("jboss.naming.client.ejb.context",true);
            context = new InitialContext(jndiProperties);
            Object ejb = context.lookup("global/ROOT/GlobalCounterBean!uk.me.eastmans.service.ejb.GlobalCounter");
            log.info( "+++++++++ ejb bean is " + ejb );
            if (ejb != null && ejb instanceof GlobalCounter)
            {
                // Try to cast
                GlobalCounter counter = (GlobalCounter) ejb;
                return counter.increment();
            }
            else
            {
                log.info( "++++++++ ejb is not of type GlobalCounter");
            }
        } catch (NamingException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (context != null)
                try {
                    context.close();
                } catch (Exception ce) { }
        }
        return -1;
    }
}
