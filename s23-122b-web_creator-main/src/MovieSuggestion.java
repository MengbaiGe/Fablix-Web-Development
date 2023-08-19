import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mysql.cj.protocol.Resultset;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
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
import java.util.HashMap;
import java.util.Map;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /*
    public static HashMap<Integer, String> superHeroMap = new HashMap<>();

    static {
        superHeroMap.put(1, "Blade");
        superHeroMap.put(2, "Ghost Rider");
        superHeroMap.put(3, "Luke Cage");
        superHeroMap.put(4, "Silver Surfer");
        superHeroMap.put(5, "Beast");
        superHeroMap.put(6, "Thing");
        superHeroMap.put(7, "Black Panther");
        superHeroMap.put(8, "Invisible Woman");
        superHeroMap.put(9, "Nick Fury");
        superHeroMap.put(10, "Storm");
        superHeroMap.put(11, "Iron Man");
        superHeroMap.put(12, "Professor X");
        superHeroMap.put(13, "Hulk");
        superHeroMap.put(14, "Cyclops");
        superHeroMap.put(15, "Thor");
        superHeroMap.put(16, "Jean Grey");
        superHeroMap.put(17, "Wolverine");
        superHeroMap.put(18, "Daredevil");
        superHeroMap.put(19, "Captain America");
        superHeroMap.put(20, "Spider-Man");
        superHeroMap.put(101, "Superman");
        superHeroMap.put(102, "Batman");
        superHeroMap.put(103, "Wonder Woman");
        superHeroMap.put(104, "Flash");
        superHeroMap.put(105, "Green Lantern");
        superHeroMap.put(106, "Catwoman");
        superHeroMap.put(107, "Nightwing");
        superHeroMap.put(108, "Captain Marvel");
        superHeroMap.put(109, "Aquaman");
        superHeroMap.put(110, "Green Arrow");
        superHeroMap.put(111, "Martian Manhunter");
        superHeroMap.put(112, "Batgirl");
        superHeroMap.put(113, "Supergirl");
        superHeroMap.put(114, "Black Canary");
        superHeroMap.put(115, "Hawkgirl");
        superHeroMap.put(116, "Cyborg");
        superHeroMap.put(117, "Robin");
    }


 */


    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try (out; Connection conn = dataSource.getConnection() ){
            //Connection conn = dataSource.getConnection();
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");
            System.out.println("query is " + query);

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }
            Statement statement = conn.createStatement();

            String selectfrom = "SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(distinct s.name order by s.cnt desc, s.name asc SEPARATOR ',') as starname, GROUP_CONCAT(distinct s.id order by s.cnt desc, s.name asc SEPARATOR ',') as sid, GROUP_CONCAT(distinct g.name order by g.name asc SEPARATOR ',') as genrename, max(m.rating) as rating ";
            String fromcnts = "from stars_in_movies as sim, (select m.id, m.title, m.year, m.director, ifnull(r.rating, 0) as rating from movies as m left outer join ratings as r on m.id = r.movieId) as m, genres as g, genres_in_movies as gim, (select s.id as id, s.name, count(mo.id) as cnt from stars as s, stars_in_movies as sim, movies as mo where mo.id = sim.movieId and sim.starId = s.id group by s.id) as s ";
            String where = "WHERE m.id = sim.movieId and sim.starId = s.id and m.id = gim.movieId and gim.genreId = g.id ";


            String searchstr = "";
            String tit = query;
            String year = "";
            String director = "";
            String starname = "";

            //String titleinit = "s";
            String titleinit = "";
            String genre = "";

            //page info(pagenum...)



            System.out.println("check49");

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


            String order = " order by ";


            System.out.println("check191");


            System.out.println("check194");

            // split the title based on white spaces into the array
            String[] titarray = tit.split("\\s+");
            String againststring = "";
            for (String token: titarray) {
                againststring += "+" + token + "*";
            }
            System.out.println("The against string is ");
            System.out.println(againststring);
            String titlequery = "and " + "MATCH m.title AGAINST (? IN BOOLEAN MODE) ";
            String Preparequery = selectfrom + fromcnts + where + titlequery + "group by m.id limit 10";
            System.out.println(Preparequery);

            //p3
            //String Preparecheat = selectfrom + fromcnts + where + "?";
            PreparedStatement Pstatement = conn.prepareStatement(Preparequery);
            //String Pstringcheat = searchstr + group + order + pagination;
            Pstatement.setString(1, againststring);



            ResultSet rs = Pstatement.executeQuery();

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
            HashMap<String, String> movieMap = new HashMap<>();
            while (rs.next()){
                String movieid = rs.getString("id");
                String movietitle = rs.getString("title");
                movieMap.put(movieid, movietitle);
            }
            for (String id : movieMap.keySet()) {
                String movieName = movieMap.get(id);
                System.out.println(movieName);
                jsonArray.add(generateJsonObject(id, movieName));

            }


            out.write(jsonArray.toString());
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieID, String movieName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}