package uk.me.eastmans.haexample;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by markeastman on 11/08/2016.
 */
@WebServlet(value="/haexample", name="haexample-servlet")
public class HAExampleServlet  extends GenericServlet {

    private static final String COUNT_VALUE = "countValue";

    // We now want to use a session value to see if they persist across HA redirects
    public void service(ServletRequest req, ServletResponse res)
            throws IOException, ServletException
    {
        String message = "Unknown ServletRequest type";
        if (req instanceof HttpServletRequest)
        {
            HttpServletRequest httpReq = (HttpServletRequest) req;
            HttpSession session = httpReq.getSession();
            Integer count = incrementCount( session );
            message = "From session " + session.getId() + ", for the " + count + " time ";
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

}
