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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
            message.append( " and global counter is " + getSingletonServiceValue() );
            message.append( " and db counter is " + getFromDatabaseCounter() );
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

    private int getSingletonServiceValue()
    {
        final ServiceController<?> requiredService = ServiceRegistryWrapper.getServiceRegistry()
                .getRequiredService(HACounterService.SINGLETON_SERVICE_NAME);
        final Service<?> service = requiredService.getService();
        return (Integer) service.getValue();
    }

    private int getFromDatabaseCounter()
    {
        int result = -1;
        try {
            String databaseURL = "jdbc:postgresql://";
            databaseURL += System.getenv("POSTGRESQL_94_RHEL7_SERVICE_HOST");
            databaseURL += "/" + System.getenv("POSTGRESQL_DATABASE");

            String username = System.getenv("POSTGRESQL_USER");
            String password = System.getenv("PGPASSWORD");
            String hostname = System.getenv("HOSTNAME");

			Connection connection = DriverManager.getConnection(databaseURL, username, password);

			if (connection != null) {
				String updateSQL = "update counters set current_value = current_value+1 where name = 'globalSingleton'";
				Statement updateStmt = connection.createStatement();
                updateStmt.executeUpdate(updateSQL);
                String getSQL = "select current_value from counters where name ='globalSingleton'";
                Statement getStmt = connection.createStatement();
                ResultSet rs = getStmt.executeQuery(getSQL);
				while (rs.next()) {
				    result = rs.getInt(1);
				}
				rs.close();
				connection.close();
			}
        } catch (Exception e) {
            log.severe("Problem with database");
            e.printStackTrace();
        }
        return result;
    }
}
