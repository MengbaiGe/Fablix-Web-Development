
//package com.mkyong.io.csv.opencsv;

import com.opencsv.CSVWriter;

import com.opencsv.CSVWriterBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;


public class DomParserExample {

    //List<Employee> employees = new ArrayList<>();
    List<star> stars = new ArrayList<>();
    HashMap<String, String> starinfo = new HashMap<>();    //key is name,value is id
    List<String[]> starcsvlist = new ArrayList<>();

    //CSVWriter writer = new CSVWriterBuilder(new FileWriter("trystar.csv"));
    Document dom;
    Document actordom;
    Document maindom;
    Document castdom;

    String loginUser = "mytestuser";
    String loginPasswd = "My6$Password";
    String loginUrl = "jdbc:mysql://localhost:3306/moviedb?autoReconnect=true";

    int starid = 0;

    int movieid = 0;
    int genreid = 0;


    List<String[]> moviecsvlist = new ArrayList<>();
    HashMap<String, String> movieinfo = new HashMap<>();
    //hashmap of movie info, key is "title,director", value is id

    List<String[]> genrecsvlist = new ArrayList<>();
    HashMap<String, String> genreinfo = new HashMap<>();        //key is genre name, value is id

    List<String[]> gimcsvlist = new ArrayList<>();

    List<String[]> simcsvlist = new ArrayList<>();


    int starcnt = 0;
    int moviecnt = 0;
    int badmoviecnt =0;

    List <String> incmovielist =new ArrayList<>();

    int dupmoviecnt = 0;

    List <String> dupmovielist =new ArrayList<>();

    int genrecnt = 0;
    int gimcnt =0;
    int simcnt = 0;
    int movienotfound = 0;
    List <String> badmovielist =new ArrayList<>();
    int starnotfound =0;
    List <String> badstarlist =new ArrayList<>();


    //Connection conn = null;

    /*
    public DomParserExample(){
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        this.conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

    }

     */


    public void runExample() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();
        String query = "SELECT max(id) as i from stars";

        ResultSet rs = statement.executeQuery(query);
        rs.next();
        String idstr = rs.getString("i");
        int idnum = Integer.parseInt(idstr.substring(2));
        starid = idnum;
        rs.close();


        String query2 = "select max(id) as mi from movies";
        ResultSet rs2 = statement.executeQuery(query2);
        rs2.next();
        idstr = rs2.getString("mi");
        int midnum = Integer.parseInt(idstr.substring(2));
        movieid = midnum;
        rs2.close();

        String query3 = "select max(id) as gi from genres";
        ResultSet rs3 = statement.executeQuery(query3);
        rs3.next();

        genreid = rs3.getInt("gi");
        rs3.close();

        String query4 = "select id, name  from genres";
        ResultSet rs4 = statement.executeQuery(query4);
        while (rs4.next())
        {
            String gname = rs4.getString("name");
            String gid = rs4.getString("id");
            genreinfo.put(gname, gid);
        }
        rs4.close();


        String query5 = "select title, director, id from movies";
        ResultSet rs5 = statement.executeQuery(query5);
        while (rs5.next())
        {
            String mtitle = rs5.getString("title");
            String mdir = rs5.getString("director");
            String mid = rs5.getString("id");
            movieinfo.put(mtitle+","+mdir, mid);
        }
        rs5.close();







        //testing actor.xml, cast.xml

        // parse the xml file and get the dom object
        parseActor();
        parseStarDocument();
        try (CSVWriter writer = new CSVWriter(new FileWriter("/var/lib/mysql/moviedb/teststar.csv"))) {
                    writer.writeAll(starcsvlist);
                }catch(IOException i){
                    System.out.println("IOException");
                }
        parseMovie();
        parseMovieDocument();
        try (CSVWriter writer = new CSVWriter(new FileWriter("/var/lib/mysql/moviedb/testmovie.csv"))) {
            writer.writeAll(moviecsvlist);
        }catch(IOException i){
            System.out.println("IOException");
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter("/var/lib/mysql/moviedb/testgenre.csv"))) {
            writer.writeAll(genrecsvlist);
        }catch(IOException i){
            System.out.println("IOException");
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter("/var/lib/mysql/moviedb/testgim.csv"))) {
            writer.writeAll(gimcsvlist);
        }catch(IOException i){
            System.out.println("IOException");
        }
        parseCast();
        parseCastDocument();
        try (CSVWriter writer = new CSVWriter(new FileWriter("/var/lib/mysql/moviedb/testsim.csv"))) {
            writer.writeAll(simcsvlist);
        }catch(IOException i){
            System.out.println("IOException");
        }
        printData();

        File file1 = new File("movienotfound.txt");
        FileWriter fw1 = new FileWriter(file1);
        BufferedWriter bw1 = new BufferedWriter(fw1);
        for (String line : badmovielist) {
            bw1.write(line);
            bw1.newLine(); // add line separator
        }

        bw1.close();
        File file2 = new File("starnotfound.txt");
        FileWriter fw2 = new FileWriter(file2);
        BufferedWriter bw2 = new BufferedWriter(fw2);

        for (String line : badstarlist) {
            bw2.write(line);
            bw2.newLine(); // add line separator
        }

        bw2.close();
        File file3 = new File("inconsistentmovies.txt");
        FileWriter fw3 = new FileWriter(file3);
        BufferedWriter bw3 = new BufferedWriter(fw3);

        for (String line : incmovielist) {
            bw3.write(line);
            bw3.newLine(); // add line separator
        }

        bw3.close();

        File file4 = new File("duplicatemovie.txt");
        FileWriter fw4 = new FileWriter(file4);
        BufferedWriter bw4 = new BufferedWriter(fw4);

        for (String line : dupmovielist) {
            bw4.write(line);
            bw4.newLine(); // add line separator
        }

        bw4.close();
        //Testing main.xml
        /*
        parseMovie();
        parseMovieDocument();
        try (CSVWriter writer = new CSVWriter(new FileWriter("testmovie.csv"))) {
            writer.writeAll(moviecsvlist);
        }catch(IOException i){
            System.out.println("IOException");
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter("testaddgenre.csv"))) {
            writer.writeAll(genrecsvlist);
        }catch(IOException i){
            System.out.println("IOException");
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter("testaddgim.csv"))) {
            writer.writeAll(gimcsvlist);
        }catch(IOException i){
            System.out.println("IOException");
        }


         */


        String loadquery1 = "LOAD DATA INFILE 'teststar.csv' INTO TABLE stars " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(name, @by,id) " +
                "SET birthYear = NULLIF(@by,0);";

        int rows1 = statement.executeUpdate(loadquery1);
        //System.out.println(rows1);
        System.out.println("Total added " + rows1 + " stars to database");


        String loadquery2 = "LOAD DATA INFILE 'testmovie.csv' INTO TABLE movies " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(id, title, year, director) " ;

        int rows2 = statement.executeUpdate(loadquery2);
        //System.out.println(rows2);
        System.out.println("Total added " + rows2 + " movies to database");

        String loadquery3 = "LOAD DATA INFILE 'testgenre.csv' INTO TABLE genres " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(id, name) " ;

        int rows3 = statement.executeUpdate(loadquery3);
        //System.out.println(rows3);
        System.out.println("Total added " + rows3 + " genres to database");

        String loadquery4 = "LOAD DATA INFILE 'testgim.csv' INTO TABLE genres_in_movies " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(genreId, movieId) " ;

        int rows4 = statement.executeUpdate(loadquery4);
        //System.out.println(rows4);
        System.out.println("Total added " + rows4 + " genres_in_movies records to database");

        String loadquery5 = "LOAD DATA INFILE 'testsim.csv' INTO TABLE stars_in_movies " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(starId, movieId) " ;

        int rows5 = statement.executeUpdate(loadquery5);
        //System.out.println(rows5);
        System.out.println("Total added " + rows5 + " stars_in_movies records to database");



        statement.close();
        connection.close();

    }

    private void parseActor() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            actordom = documentBuilder.parse("actors63.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }
    private void parseMovie() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory2 = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder2 = documentBuilderFactory2.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            maindom = documentBuilder2.parse("mains243.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseCast() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory3 = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder3 = documentBuilderFactory3.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            castdom = documentBuilder3.parse("casts124.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }
    private void parseMovieDocument() {
        // get the document root Element
        Element documentElement = maindom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        String[] header = {"id","title", "year", "director"};
        moviecsvlist.add(header);
        String[] header2 = {"genreId","movieId"};
        gimcsvlist.add(header2);
        String[] header3 = {"id","name"};
        genrecsvlist.add(header3);
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the employee element
            Element dfelement = (Element) nodeList.item(i);
            NodeList dnode = dfelement.getElementsByTagName("director");
            NodeList fnode = dfelement.getElementsByTagName("films");
            Element delement = (Element) dnode.item(0);
            String dirname = getTextValue(delement, "dirname");
            if (dirname == null || dirname.equals(""))
            {
                badmoviecnt++;

                incmovielist.add("bad movie entry: no director name");
                continue;
            }
            Element fselement = (Element) fnode.item(0);
            NodeList filmlist = fselement.getElementsByTagName("film");
            for(int j = 0; j<filmlist.getLength();j++)
            {
                Element film = (Element) filmlist.item(j);
                String title = getTextValue(film, "t");
                int year = getIntValue(film,"year");
                if (title == null || title.equals(""))
                {
                    badmoviecnt++;
                    incmovielist.add("movie has no title: director: "+dirname);
                    continue;
                }
                if ( year == 0)
                {
                    badmoviecnt++;
                    incmovielist.add("movie has no year: director: "+dirname + ", title: "+title);
                    continue;
                }
                //check duplicate
                String key = title+","+dirname;
                String value = movieinfo.get(key);
                if (value != null) {
                    dupmoviecnt++;
                    dupmovielist.add("duplicate movie: title: "+title+", director: "+dirname);
                    // duplicated movie, do nothing

                } else {
                    movieid++;
                    String[] csventry = {"tt0"+movieid, title, ""+year,dirname};
                    moviecsvlist.add(csventry);
                    movieinfo.put(title+","+dirname, "tt0"+movieid);
                    moviecnt++;

                    // add movie, deal with genres
                    NodeList genrelist = film.getElementsByTagName("cats");
                    for (int g = 0; g<genrelist.getLength();g++)
                    {
                        Element gen = (Element) genrelist.item(g);
                        String gname = getTextValue(gen,"cat");
                        if (gname == null || gname.equals(""))
                        {
                            badmoviecnt++;

                            incmovielist.add("no genre for this movie: title: "+ title + ", year: "+year);
                            continue;
                        }
                        gname = gname.trim();
                        String gid = genreinfo.get(gname);
                        if (gid == null)
                        {
                            //add new genre
                            genrecnt++;
                            genreid++;
                            genreinfo.put(gname, ""+genreid);
                            String[] gcsventry = {""+genreid,gname};
                            genrecsvlist.add(gcsventry);
                            gid = "" + genreid;
                        }
                        //add genres_in_movies
                        String[] gimcsv = {""+gid,"tt0"+movieid};
                        gimcsvlist.add(gimcsv);
                        gimcnt++;
                    }
                }
            }



        }
    }

    private void parseCastDocument() {
        // get the document root Element
        Element documentElement = castdom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object

        //NodeList clist = documentElement.getElementsByTagName("casts");

        //Element casts = (Element) clist.item(0);
        //NodeList nodeList = casts.getElementsByTagName("dirfilms");
        NodeList nodeList = documentElement.getElementsByTagName("dirfilms");
        String[] header = {"starId","movieId"};
        simcsvlist.add(header);
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the employee element
            Element dfelement = (Element) nodeList.item(i);
            //NodeList dirnode = dfelement.getElementsByTagName("is");
            NodeList fnode = dfelement.getElementsByTagName("filmc");
            //Element delement = (Element) dirnode.item(0);
            String dirname = getTextValue(dfelement, "is");
            if (dirname == null || dirname.equals(""))
            {
                continue;
            }
            for(int j = 0; j<fnode.getLength();j++)
            {
                Element filmc = (Element) fnode.item(j);
                NodeList filmlist = filmc.getElementsByTagName("m");
                for(int f=0;f< filmlist.getLength();f++)
                {
                    Element film = (Element) filmlist.item(f);
                    String title = getTextValue(film, "t");
                    String starname = getTextValue(film, "a");
                    if (title == null || title.equals("")||starname == null || starname.equals(""))
                    {
                        continue;
                    }
                    //check match
                    String key = title+","+dirname;
                    String mid = movieinfo.get(key);
                    if (mid == null) {
                        movienotfound++;

                        badmovielist.add("director name: "+ dirname + ", title: " + title+ ", star: "+ starname);
                        // movie doesn't exist, do nothing, maybe log to some file

                    } else {//movie exist, check star exist or not
                        String sid = starinfo.get(starname);
                        if (sid == null)
                        {
                            starnotfound++;
                            badstarlist.add("director name: "+ dirname + ", title: " + title+ ", star: "+ starname);
                            //star doen't exist, do nothing, maybe log
                        }
                        else {
                            //star exist, add to stars_in_movies
                            String[] simcsv = {sid, mid};
                            simcsvlist.add(simcsv);
                            simcnt++;
                        }
                    }


                    //
                }

                //

            }



        }
    }

    private void parseStarDocument() {
        // get the document root Element
        Element documentElement = actordom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        String[] header = {"name", "birthYear", "id"};
        starcsvlist.add(header);
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the employee element
            Element element = (Element) nodeList.item(i);

            // get the Employee object
            star actor = parseStar(element);

            // add it to list
            //String key = actor.getName()+","+actor.getYear();
            starinfo.put(actor.getName(),actor.getId());
            stars.add(actor);
            starcsvlist.add(actor.tocsv());
            starcnt++;
        }
    }



    private star parseStar(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String name = getTextValue(element, "stagename");
        starid++;
        String id = "nm"+starid;
        int year = getIntValue(element, "dob");

        // create a new Employee with the value read from the xml nodes
        return new star(name, year, id);
    }
    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            if (nodeList.item(0).getFirstChild()== null){
                return "";
            }


            textVal = nodeList.item(0).getFirstChild().getNodeValue();

        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        int i = 0;
        // in production application you would catch the exception
        try{
            i = Integer.parseInt(getTextValue(ele, tagName));
        }catch(NumberFormatException e)
        {
            return 0;
        }


        return i;
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("Total " + starcnt + " stars to be added");
        System.out.println("Total " + moviecnt + " movies to be added");
        System.out.println("Total " + genrecnt + " genres to be added");
        System.out.println("Total " + simcnt + " stars_in_movies records to be added");
        System.out.println("Total " + gimcnt + " genres_in_movies records to be added");
        System.out.println("Inconsistent movies: " + badmoviecnt);
        System.out.println("Duplicated movies: " + dupmoviecnt);
        System.out.println("Total " + movienotfound + " bad cast entries due to movie not found");
        System.out.println("Total " + starnotfound + " bad cast entries due to star not found");




    }

    public static void main(String[] args) throws Exception{
        // create an instance
        DomParserExample domParserExample = new DomParserExample();

        // call run example
        domParserExample.runExample();

    }

}
