/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
let num = 10;
let sortmode = 1;
let page = 1;
let movieTableBodyElement = jQuery("#movie_table_body");
//let rowHTML = "";
//let array = [];
let condition_form = $("#condition_form");
/*
window.addEventListener("click", function (event){
    if (event.target == Popup){
        Popup.classList.remove("show");
    }
});
*/


function numfun(){
    var mylist = document.getElementById("movie_num");
    num = parseInt(mylist.options[mylist.selectedIndex].text);
}

function sortfun(){
    var mylist = document.getElementById("sort");
    sortmode = mylist.options[mylist.selectedIndex].text;
}



function Previous(){
    /*
    movieTableBodyElement.empty();
    movieTableBodyElement.append(rowHTML);

     */
    if(page > 1){
        page = page-1;
    }
    let myurl = "api/movies";
    let title = getParameterByName("title");
    if(title == null){
        title = "";
    }
    let year = getParameterByName("year");
    if(year == null){
        year = "";
    }
    let director = getParameterByName("director");
    if(director == null){
        director = "";
    }
    let starname = getParameterByName("starname");
    if(starname == null){
        starname = "";
    }
    let titleinit = getParameterByName("titleinit");
    if(titleinit == null){
        titleinit = "";
    }

    let genre = getParameterByName("genre");
    if(genre == null){
        genre = "";
    }
    myurl += "?title=" + title + "&year=" + year
        + "&director=" + director + "&starname=" + starname
        + "&page=" + page + "&number=" + num
        + "&sortmode=" + sortmode + "titleinit=" + titleinit
        +  "&genre=" + genre;

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        // url: window.location.href,
        url: myurl, // Setting request url, which is mapped by StarsServlet in Stars.java

        //success: (resultData) => handleMovieResult(num,sortmode, page, resultData)
        // Setting callback function to handle data returned successfully by the StarsServlet
        success: resultData => {
            //array = resultData;
            // console.log("This is the fking movie price: ", resultData[0]["price_dict"]);
            movieTableBodyElement.empty();
            handleMovieResult(resultData)
        }
    });


}

function Next(){
    //movieTableBodyElement.empty();
    page = page + 1;
    //movieTableBodyElement.empty();
    let myurl = "api/movies";
    let title = getParameterByName("title");
    if(title == null){
        title = "";
    }
    let year = getParameterByName("year");
    if(year == null){
        year = "";
    }
    let director = getParameterByName("director");
    if(director == null){
        director = "";
    }
    let starname = getParameterByName("starname");
    if(starname == null){
        starname = "";
    }
    let titleinit = getParameterByName("titleinit");
    if(titleinit == null){
        titleinit = "";
    }

    let genre = getParameterByName("genre");
    if(genre == null){
        genre = "";
    }
    myurl += "?title=" + title + "&year=" + year
        + "&director=" + director + "&starname=" + starname
        + "&page=" + page + "&number=" + num
        + "&sortmode=" + sortmode + "titleinit=" + titleinit
        +  "&genre=" + genre;
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        // url: window.location.href,
        url: myurl, // Setting request url, which is mapped by StarsServlet in Stars.java

        //success: (resultData) => handleMovieResult(num,sortmode, page, resultData)
        // Setting callback function to handle data returned successfully by the StarsServlet
        success: resultData => {
            //array = resultData;
            // console.log("This is the fking movie price: ", resultData[0]["price_dict"]);
            movieTableBodyElement.empty();
            handleMovieResult(resultData)
        }
    });

}

function handleUpdate(searchEvent){
    searchEvent.preventDefault();

    let myurl = "api/movies";
    let title = getParameterByName("title");
    if(title == null){
        title = "";
    }
    let year = getParameterByName("year");
    if(year == null){
        year = "";
    }
    let director = getParameterByName("director");
    if(director == null){
        director = "";
    }
    let starname = getParameterByName("starname");
    if(starname == null){
        starname = "";
    }
    let titleinit = getParameterByName("titleinit");
    if(titleinit == null){
        titleinit = "";
    }

    let genre = getParameterByName("genre");
    if(genre == null){
        genre = "";
    }
    myurl += "?title=" + title + "&year=" + year
        + "&director=" + director + "&starname=" + starname
        + "&page=" + 1 + "&number=" + num
        + "&sortmode=" + sortmode + "titleinit=" + titleinit
        +  "&genre=" + genre;
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        // url: window.location.href,
        url: myurl, // Setting request url, which is mapped by StarsServlet in Stars.java

        //success: (resultData) => handleMovieResult(num,sortmode, page, resultData)
        // Setting callback function to handle data returned successfully by the StarsServlet
        success: resultData => {
            //array = resultData;
            // console.log("This is the fking movie price: ", resultData[0]["price_dict"]);
            movieTableBodyElement.empty();
            handleMovieResult(resultData)
        }
    });
}

function addmovie(movie_title){
    $.ajax("api/cart", {
        method: "POST",
        data: "title=" + movie_title + "&mode=increase",
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            console.log("The current dict length is: ", resultDataJson["dictlength"]);
            //Popup.classList.add("show");
        }
    });
    window.alert("successfully added to cart");
}

function Checkout(){

    window.location.href = "cart.html";
}
/*
function closepop(){
    Popup.classList.remove("show");
}*/
//number,sorting, page_number,
//start_index + number
function handleMovieResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");
    //console.log(number, sorting,page_number);
    /*
    if(sorting == "title+, rating+"){
        resultData.sort(function (a,b) {
            return a["movie_title"].localeCompare(b["movie_title"])  || parseFloat(a["movie_rating"]) - parseFloat(b["movie_rating"]);
        });
    }
    else if(sorting == "title+, rating-"){
        resultData.sort(function (a,b) {
            return a["movie_title"].localeCompare(b["movie_title"]) || parseFloat(b["movie_rating"]) - parseFloat(a["movie_rating"]);
        });
    }
    else if(sorting == "title-, rating+"){
        resultData.sort(function (a,b) {
            return b["movie_title"].localeCompare(a["movie_title"]) || parseFloat(a["movie_rating"]) - parseFloat(b["movie_rating"]);
        });
    }
    else if(sorting == "title-, rating-"){
        resultData.sort(function (a,b) {
            return b["movie_title"].localeCompare(a["movie_title"]) || parseFloat(b["movie_rating"]) - parseFloat(a["movie_rating"]);
        });
    }
    else if(sorting == "rating+, title+"){
        resultData.sort(function (a,b) {
            return parseFloat(a["movie_rating"]) - parseFloat(b["movie_rating"]) || a["movie_title"].localeCompare(b["movie_title"]);
        });
    }
    else if(sorting == "rating+, title-"){
        resultData.sort(function (a,b) {
            return parseFloat(a["movie_rating"]) - parseFloat(b["movie_rating"]) || b["movie_title"].localeCompare(a["movie_title"]);
        });
    }
    else if(sorting == "rating-, title+"){
        resultData.sort(function (a,b) {
            return parseFloat(b["movie_rating"]) - parseFloat(a["movie_rating"]) || a["movie_title"].localeCompare(b["movie_title"]);
        });
    }
    else{
        resultData.sort(function (a,b) {
            return parseFloat(b["movie_rating"]) - parseFloat(a["movie_rating"]) || b["movie_title"].localeCompare(a["movie_title"]);
        });
    }*/


    // Populate the star table
    // Find the empty table body by id "star_table_body"
    //let movieTableBodyElement = jQuery("#movie_table_body");
    //let start_index =  number * (page_number-1);

    rowHTML = "";
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i <  resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";

        rowHTML += "<tr>";


        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'+ resultData[i]["movie_title"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        //rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
        //rowHTML += "<th>" + resultData[i]["movie_stars"] + "</th>";

        //rowHTML += "<th>" + resultData[i]["movie_stars"] + "</th>";
        const Moviestars = resultData[i]["movie_stars"].split(",");
        const Moviegenres = resultData[i]["movie_genres"].split(",");
        rowHTML += "<th>";
        let index = 0;
        for(index = 0; index < Math.min(3, Moviegenres.length); index++){
            rowHTML += Moviegenres[index];
            rowHTML += ",";
        }
        rowHTML = rowHTML.substring(0,rowHTML.length-1);
        rowHTML += "</th>";
        const starids = resultData[i]["star_ids"].split(",");

        let stars_string = "<th>";

        for(index = 0; index < Math.min(3, Moviestars.length); index++){
            stars_string += '<a href="single-star.html?id=' + starids[index] + '">'
                + Moviestars[index] +     // display star_name for the link text
                '</a>';
            stars_string += ",";
        }
        stars_string = stars_string.substring(0, stars_string.length-1);

        stars_string += "</th>";
        rowHTML += stars_string;
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "<th>" + '<button type = "button" onclick=\"addmovie(\'' + resultData[i]["movie_title"] + '\')\">Add</button>' + "</th>";
        rowHTML += "</tr>";
        /*
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
            + resultData[i]["star_name"] +     // display star_name for the link text
            '</a>' +
            "</th>";

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";
        */
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    //movieTableBodyElement.append(rowHTML);
}

function addmovie(movie_title){
    $.ajax("api/cart", {
        method: "POST",
        data: "title=" + movie_title + "&mode=increase",
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            console.log("The current dict length is: ", resultDataJson["dictlength"]);
            //Popup.classList.add("show");
        }
    });
    window.alert("successfully added to cart");
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    // url: window.location.href,
    url: "api/movies" + window.location.search, // Setting request url, which is mapped by StarsServlet in Stars.java

    //success: (resultData) => handleMovieResult(num,sortmode, page, resultData)
    // Setting callback function to handle data returned successfully by the StarsServlet
    success: resultData => {
        //array = resultData;
        // console.log("This is the fking movie price: ", resultData[0]["price_dict"]);
        handleMovieResult(resultData);
    }
});

condition_form.submit(handleUpdate);