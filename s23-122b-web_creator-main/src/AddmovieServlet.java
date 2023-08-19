import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "AddmovieServlet", urlPatterns = "/api/addmovie")
public class AddmovieServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles GET requests to store session information
     */
    /*
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<String>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

     */

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {

            // Declare our statement
            //Statement statement = conn.createStatement();

            JsonObject jsonObject = new JsonObject();


            String callquery = "call add_movies(?,?,?,?,?,?)";
            CallableStatement cstatement = conn.prepareCall(callquery);

            String tit = request.getParameter("movietitle");
            String movieyear = request.getParameter("movieyear");
            String dir = request.getParameter("moviedirector");
            String gen = request.getParameter("moviegenre");
            String starname = request.getParameter("starname");
            String birthyear = request.getParameter("birthyear");
            Integer myear = Integer.parseInt(movieyear);


            cstatement.setString(1,tit);
            cstatement.setInt(2,myear);
            cstatement.setString(3,dir);
            cstatement.setString(4,starname);
            if (birthyear==null || birthyear.isEmpty())
            {
                cstatement.setNull(5,java.sql.Types.INTEGER);
            }
            else {
                Integer byear = Integer.parseInt(birthyear);
                cstatement.setInt(5,byear);
            }

            cstatement.setString(6,gen);


            // Perform the query
            ResultSet rs = cstatement.executeQuery();
            rs.next();
            jsonObject.addProperty("msg", rs.getString("msg"));


            rs.close();
            cstatement.close();


            System.out.println("added new movie");





            // Log to localhost log
            //request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
