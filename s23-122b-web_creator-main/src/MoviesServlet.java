import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.sql.PreparedStatement;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;




// Declaring a WebServlet called Movies_Servlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
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
        System.out.println("The doGet starts!");
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {
            //HttpSession session = request.getSession();
            //Map<String, String> pricedict = (Map<String, String>) session.getAttribute("pricedict");
            //System.out.println("HelloWorld");

            // Declare our statement
            Statement statement = conn.createStatement();
            /*
            String query2 = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                    "where m.id = sim.movieId and sim.starId = s.id and m.id = ? LIMIT 3;";

            String query3 = "SELECT * from genres as g, genres_in_movies as gim, movies as m " +
                    "where m.id = gim.movieId and gim.genreId = g.id and m.id = ? LIMIT 3;";
            */

            //String query1 = "SELECT m.id, m.title, m.year, m.director, r.rating, s.name as starname, s.id as sid,  g.name as genrename from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r where m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.id and m.id = r.movieId ORDER BY r.rating DESC LIMIT 1000";

            //p2 stuff
            //String selectfrom = "SELECT m.id, m.title, m.year, m.director, r.rating, s.name as starname, s.id as sid,  g.name as genrename from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r ";
            //String fromcnts = ", (select s.id as sid, s.name, count(m.id) as cnt from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id group by s.id) as cnts ";
            //String where = " WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.id and m.id = r.movieId and cnts.sid = s.id ";
            String selectfrom = "SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(distinct s.name order by s.cnt desc, s.name asc SEPARATOR ',') as starname, GROUP_CONCAT(distinct s.id order by s.cnt desc, s.name asc SEPARATOR ',') as sid, GROUP_CONCAT(distinct g.name order by g.name asc SEPARATOR ',') as genrename, max(m.rating) as rating ";
            String fromcnts = "from stars_in_movies as sim, (select m.id, m.title, m.year, m.director, ifnull(r.rating, 0) as rating from movies as m left outer join ratings as r on m.id = r.movieId) as m, genres as g, genres_in_movies as gim, (select s.id as id, s.name, count(mo.id) as cnt from stars as s, stars_in_movies as sim, movies as mo where mo.id = sim.movieId and sim.starId = s.id group by s.id) as s ";
            String where = "WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.id ";


            String searchstr = "";
            String tit = request.getParameter("title");
            if(tit == null){
                tit = "";
            }
            String year = request.getParameter("year");
            if(year == null){
                year = "";
            }
            String director = request.getParameter("director");
            if(director == null){
                director = "";
            }
            String starname = request.getParameter("starname");
            if(starname == null){
                starname = "";
            }

            //String titleinit = "s";
            String titleinit = request.getParameter("titleinit");
            if(titleinit == null){
                titleinit = "";
            }
            String genre = request.getParameter("genre");

            if(genre == null){
                genre = "";
            }

            //page info(pagenum...)
            String page = (request.getParameter("page"));
            if (page == null)
            {
                page = "1";
            }
            String number = (request.getParameter("number"));
            if (number == null)
            {
                number = "10";
            }
            String sortmode = (request.getParameter("sortmode"));
            if (sortmode == null)
            {
                sortmode = "1";
            }
            HttpSession session = request.getSession();
            //Map<String, String> mypageinfo = (Map<String, String>) session.getAttribute("pageinfo");
            //if (mypageinfo == null) {
            Map<String, String> mypageinfo = (Map<String, String>) session.getAttribute("pageinfo");
            if(mypageinfo == null){
                mypageinfo = new HashMap<>();
                //session.setAttribute("pageinfo",mypageinfo);
            }
            else {
                synchronized (mypageinfo){
                    mypageinfo = new HashMap<>();
                }
            }
            //Map<String, String> mypageinfo = new HashMap<>();
            System.out.println("check100");
            //put pageinfo
            mypageinfo.put("page", page);
            mypageinfo.put("number", number);
            mypageinfo.put("sortmode", sortmode);
            if(tit==null)
            {
                mypageinfo.put("title", "");
            }else{
                mypageinfo.put("title", tit);
            }
            if(year==null)
            {
                mypageinfo.put("year", "");
            }else{
                mypageinfo.put("year", year);
            }
            if(starname==null)
            {
                mypageinfo.put("starname", "");
            }else{
                mypageinfo.put("starname", starname);
            }
            if(director==null)
            {
                mypageinfo.put("director", "");
            }else{
                mypageinfo.put("director", director);
            }
            if(titleinit==null)
            {
                mypageinfo.put("titleinit", "");
            }else{
                mypageinfo.put("titleinit", titleinit);
            }
            if(tit==null)
            {
                mypageinfo.put("genre", "");
            }else{
                mypageinfo.put("genre", genre);
            }

            session.setAttribute("pageinfo", mypageinfo);

            System.out.println("check143");

            String titP = "%";
            if (tit != null && !tit.isEmpty())
            {
                searchstr = searchstr + "and m.title like '%"+tit+"%' ";
                titP = "%"+tit+"%";
            }
            String yearP = "%";
            if (year != null && !year.isEmpty())
            {
                searchstr = searchstr + "and m.year = '"+year+"' ";
                yearP = year;
            }
            String directorP = "%";
            if (director != null && !director.isEmpty())
            {
                searchstr = searchstr + "and m.director like '%"+director+"%' ";
                directorP = "%"+director+"%";
            }
            String titleinitP = "^[^\n]";
            if (titleinit != null && !titleinit.isEmpty())
            {
                if (titleinit.equals("*"))
                {
                    searchstr = searchstr + "and m.title regexp '" + "^[^a-zA-Z0-9]' ";
                    titleinitP = "^[^a-zA-Z0-9]";
                }
                else {
                    searchstr = searchstr + "and m.title like '"+titleinit+"%' ";
                    titleinitP = "^"+titleinit;
                }
            }

            //System.out.println("check193");
            //String order = " order by cnts.cnt desc, s.name asc";
            String group = " group by m.id ";
            String starnameP = "%";
            if (starname != null && !starname.isEmpty())
            {
                group = group + "having lower(starname) like '%"+starname+"%' ";
                starnameP = "%"+starname+"%";
            }
            String genreP = "%";
            if (genre != null && !genre.isEmpty())
            {
                group = group + "having genrename like '%"+genre+"%' ";
                genreP = "%"+genre+"%";
            }

            String order = " order by ";
            String sortmodeP = "m.title asc, rating asc";
            if(sortmode.equals("4") )
            {
                order = order + "m.title desc, rating desc ";
                sortmodeP = "m.title desc, rating desc";
            }
            else if(sortmode.equals("2") )
            {
                order = order + "m.title asc, rating desc ";
                sortmodeP = "m.title asc, rating desc";
            }
            else if(sortmode.equals("3") )
            {
                order = order + "m.title desc, rating asc ";
                sortmodeP = "m.title desc, rating asc";
            }
            else
            {
                order = order + "m.title asc, rating asc ";
            }

            System.out.println("check100");
            String pagination = "limit " + number + " offset " + (Integer.parseInt(number))*(Integer.parseInt(page)-1);
            Integer numberP = Integer.parseInt(number);
            Integer offsetP = (Integer.parseInt(number))*(Integer.parseInt(page)-1);

            System.out.println("check241");
            String Searchquery = selectfrom + fromcnts + where + searchstr + group + order + pagination;
            System.out.println("The movie title is " + tit);
            System.out.println("check244");
            // split the title based on white spaces into the array
            String[] titarray = tit.split("\\s+");
            String againststring = "";
            for (String token: titarray) {
                againststring += "+" + token + "*";
            }

            System.out.println("The against string is ");
            System.out.println(againststring);
            String titlequery = "and " + "MATCH m.title AGAINST (? IN BOOLEAN MODE) ";
            if (tit.equals("")){
                titlequery = "";
            }
            String Preparequery = selectfrom + fromcnts + where + titlequery + "and m.year like ? and m.director like ? and m.title regexp ? group by m.id having lower(starname) like ? and genrename like ? order by ? limit ? offset ?";
            System.out.println(Preparequery);

            //p3
            //String Preparecheat = selectfrom + fromcnts + where + "?";
            PreparedStatement Pstatement = conn.prepareStatement(Preparequery);
            if (tit.equals("")){
                Pstatement.setString(1, yearP);
                Pstatement.setString(2, directorP);
                Pstatement.setString(3, titleinitP);
                Pstatement.setString(4, starnameP);
                Pstatement.setString(5, genreP);
                Pstatement.setString(6, sortmodeP);
                Pstatement.setInt(7, numberP);
                Pstatement.setInt(8, offsetP);
            }
            else {

                //String Pstringcheat = searchstr + group + order + pagination;
                Pstatement.setString(1, againststring);
                Pstatement.setString(2, yearP);
                Pstatement.setString(3, directorP);
                Pstatement.setString(4, titleinitP);
                Pstatement.setString(5, starnameP);
                Pstatement.setString(6, genreP);
                Pstatement.setString(7, sortmodeP);
                Pstatement.setInt(8, numberP);
                Pstatement.setInt(9, offsetP);
            }

            ResultSet rs = Pstatement.executeQuery();
            //p2+ improved
            //ResultSet rs = statement.executeQuery(Searchquery);

            System.out.println("check311");
            JsonArray jsonArray = new JsonArray();
            while (rs.next()){
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String movie_star = rs.getString("starname");
                String movie_genre = rs.getString("genrename");
                String star_id = rs.getString("sid");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_stars", movie_star);
                jsonObject.addProperty("movie_genres", movie_genre);
                jsonObject.addProperty("star_ids", star_id);
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
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

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("receive request post");

        HttpSession session = request.getSession();
        Map<String, String> mypageinfo = (Map<String, String>) session.getAttribute("pageinfo");
        // get the previous items in a ArrayList


        JsonObject responseJsonObject = new JsonObject();
        if(mypageinfo == null){
            System.out.println("null case");
            responseJsonObject.addProperty("page", "1");
            responseJsonObject.addProperty("number", "10");
            responseJsonObject.addProperty("sortmode", "1");
            responseJsonObject.addProperty("title", "");
            responseJsonObject.addProperty("genre", "");
            responseJsonObject.addProperty("titleinit", "");
            responseJsonObject.addProperty("director", "");
            responseJsonObject.addProperty("starname", "");
            responseJsonObject.addProperty("year", "");
        }
        else {

            System.out.println("not null case");

            System.out.println(mypageinfo.values());
            responseJsonObject.addProperty("page", mypageinfo.get("page"));
            responseJsonObject.addProperty("number", mypageinfo.get("number"));
            responseJsonObject.addProperty("sortmode", mypageinfo.get("sortmode"));

            responseJsonObject.addProperty("title", mypageinfo.get("title"));
            responseJsonObject.addProperty("titleinit", mypageinfo.get("titleinit"));
            responseJsonObject.addProperty("director", mypageinfo.get("director"));
            responseJsonObject.addProperty("starname", mypageinfo.get("starname"));
            responseJsonObject.addProperty("year", mypageinfo.get("year"));
            responseJsonObject.addProperty("genre", mypageinfo.get("genre"));

        }
        System.out.println("470check");

        response.getWriter().write(responseJsonObject.toString());
        /*
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<String>();
            previousItems.add(item);
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                previousItems.add(item);
            }
        }

        JsonObject responseJsonObject = new JsonObject();

        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());

        */
    }
}