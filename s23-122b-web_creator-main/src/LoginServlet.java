import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;


import com.mysql.cj.protocol.Resultset;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    //new code
    private DataSource dataSource;
    private DataSource dataSource2;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("39");
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        /*String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        JsonObject responseJsonObject = new JsonObject();

        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            responseJsonObject.addProperty("message", "Recaptcha verification fails.");
            out.write(responseJsonObject.toString());

            out.close();
            return;
        }

         */



        String email = request.getParameter("email");
        String password = request.getParameter("password");

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        //JsonObject responseJsonObject = new JsonObject();

        //new code

        //String query = String.format("SELECT count(*) as c from customers where email='%s' and password='%s'", email, password);
        System.out.println("70");

        try (out; Connection dbCon = dataSource.getConnection()) {

            // Create a new connection to database
            System.out.println("75");
            //Connection dbCon = dataSource.getConnection();
            System.out.println("77");

            // Declare a new statement
            //Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            //String email = request.getParameter("email");
            //String password = request.getParameter("password");

            // Generate a SQL query
            /*
            String query = String.format("SELECT count(*) as c from customers where email='%s' and password='%s'", email, password);

            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            //Integer userid = rs.getInt("id");
            String idquery = String.format("SELECT id from customers where email='%s' and password='%s' limit 1", email, password);
            */

            String pwquery = "SELECT password from customers where email=?";
            PreparedStatement statement = dbCon.prepareStatement(pwquery);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            System.out.println("105");
            /*
            boolean result = rs.next();
            System.out.println("107: " + result);
            
             */
            boolean success = false;
            if (rs.next()) {
                // get the encrypted password from the database
                String encryptedPassword = rs.getString("password");
                System.out.println("The encrypted password is " + encryptedPassword);
                System.out.println("The plain password is " + password);

                // use the same encryptor to compare the user input password with encrypted password stored in DB
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

            }
            System.out.println("117");
            PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

            String idquery = "SELECT id from customers where email=? limit 1";
            PreparedStatement statement2 = dbCon.prepareStatement(idquery);
            statement2.setString(1, email);
            //password = passwordEncryptor.encryptPassword(password);
            //statement2.setString(2, password);
            //ResultSet rs2 = statement2.executeQuery();


            //rs.last();
            //Integer count = rs.getInt("c");
            if (success) // replace with success
            {
                System.out.println("Success");
                //rs=statement.executeQuery(idquery);
                ResultSet rs2 = statement2.executeQuery();
                rs2.next();

                Integer userid = rs2.getInt("id");
                System.out.println("User id is ");
                System.out.println(userid);
                request.getSession().setAttribute("user", new User(userid));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }
            else
            {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "incorrect email/password");

            }


            // Close all structures
            rs.close();
            statement.close();
            out.write(responseJsonObject.toString());
            dbCon.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            request.getServletContext().log("Error: ", e);
            System.out.println("The error is " + e.toString());
            // Output Error Massage to html
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", e.getMessage()));
            return;
        }

        //old code
        /*
        if (username.equals("anteater") && password.equals("123456")) {
            // Login success:

            // set this user into the session
            request.getSession().setAttribute("user", new User(username));

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            if (!username.equals("anteater")) {
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            } else {
                responseJsonObject.addProperty("message", "incorrect password");
            }

        }
        */
        //old code end
        System.out.println(responseJsonObject.toString());

        //out.write(responseJsonObject.toString());
    }
}
