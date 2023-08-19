import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.sql.*;
import java.util.*;
import java.util.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;


// Declaring a WebServlet called Movies_Servlet, which maps to url "/api/movies"
@WebServlet(name = "TableServlet", urlPatterns = "/api/tables")
public class TableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {
            //HttpSession session = request.getSession();
            //Map<String, String> pricedict = (Map<String, String>) session.getAttribute("pricedict");
            //System.out.println("HelloWorld");

            // Declare our statement
            DatabaseMetaData metadata = conn.getMetaData();
            JsonArray jsonarray = new JsonArray();
            Statement statement = conn.createStatement();
            System.out.println("59");
            ResultSet rs1 = statement.executeQuery("Show tables");
            System.out.println("61");
            /*
            ResultSet rs1 = statement.executeQuery("Show tables");
            System.out.println("Tables in the current database: ");
            while(rs1.next()) {
                System.out.print(rs1.getString(1));
                System.out.println();
            }


             */
            int mycount = 0;
            List<String> namelist = new ArrayList<>();
            System.out.println("74");
            while(rs1.next()){
                String tablename = rs1.getString(1);
                System.out.println(tablename);
                namelist.add(tablename);
                System.out.println("79");

            }
            System.out.println("81");
            System.out.println(namelist);
            //rs1.close();
            for(int j = 0; j < namelist.size(); j ++) {
                //mycount ++;
                List<String> table = Arrays.asList("creditcards", "customers", "genres", "movies", "ratings", "sales", "stars");

                String tablename = namelist.get(j);
                System.out.println(tablename);
                if(true) {
                    mycount ++;
                    System.out.println(tablename);
                    String query = String.format("select * from %s", tablename);
                    ResultSet rs3 = statement.executeQuery(query);
                    System.out.println("63");
                    ResultSetMetaData rsmeta = rs3.getMetaData();
                    int count = rsmeta.getColumnCount();
                    //ResultSet rs2 = metadata.getColumns(null, null, tablename, null);
                    String names = "";
                    String types = "";
                    for (int i = 1; i <= count; i++) {

                    /*ResultSetMetaData rsMetaData = rs2.getMetaData();
                    System.out.println("List of column names in the current table: ");
                    //Retrieving the list of column names
                    int count = rsMetaData.getColumnCount();
                    for(int i = 1; i<=count; i++) {
                        System.out.println(rsMetaData.getColumnName(i));
                    }

                     */


                        String name = rsmeta.getColumnName(i);

                        String type = rsmeta.getColumnTypeName(i);
                        names += name + ",";
                        types += type + ",";
                    }
                    //rs3.close();
                    names = names.substring(0, names.length() - 1);
                    types = types.substring(0, types.length() - 1);
                    System.out.println(names);
                    System.out.println(types);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("tablename", tablename);
                    jsonObject.addProperty("names", names);
                    jsonObject.addProperty("types", types);
                    jsonarray.add(jsonObject);
                }


            }
            //rs1.close();
            System.out.println("The fking count is " );
            System.out.println(mycount);
            request.getServletContext().log("getting " + jsonarray.size() + " results");

            // Write JSON string to output
            out.write(jsonarray.toString());
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

        // Always remember to close db connection after usage. Here it's done by try-with-resources


