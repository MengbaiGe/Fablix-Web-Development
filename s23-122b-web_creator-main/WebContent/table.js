let metainfo = jQuery("#metainfo");

function handleResult(resultdata){

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i <  resultdata.length; i++) {
        let rowHTML = "<center>";
        let tablename = resultdata[i]["tablename"];
        let names = resultdata[i]["names"];
        let types = resultdata[i]["types"];
        console.log(names);
        console.log(types);
        const namelist = names.split(",");
        const typelist = types.split(",");
        rowHTML += tablename + "<br>";
        rowHTML += "<table> " +
            "<thead>" +
            "<tr>" +
            "<th>Attribute</th>" +
            "<th>Type</th>" +
            "</tr>" +
            "</thead>";
        for (let j = 0; j < namelist.length; j++){
            let name = namelist[j];
            let type = typelist[j];
            rowHTML += "<tr><th>" + name+ "</th><th>" +  type + "</th></tr>";
        }
        rowHTML += "</table>" + "<br>";
        rowHTML += "</center>";
        //rowHTML += "</center>"
        metainfo.append(rowHTML);


    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    // url: window.location.href,
    url: "api/tables", // Setting request url, which is mapped by StarsServlet in Stars.java

    //success: (resultData) => handleMovieResult(num,sortmode, page, resultData)
    // Setting callback function to handle data returned successfully by the StarsServlet
    success: resultData => {
        //array = resultData;
        // console.log("This is the fking movie price: ", resultData[0]["price_dict"]);
        handleResult(resultData);
    }
});