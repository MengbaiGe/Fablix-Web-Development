let add_star = $("#add_star");
/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleResult(resultDataString) {
    //let resultDataJson = JSON.parse(resultDataString);

    //console.log(resultDataJson);
    //console.log(resultDataJson["status"]);
    console.log("handle addstar");
    if (resultDataString["status"] === "fail")
    {
        window.alert("adding failed: no star name");
    }
    else
    {
        window.alert("added star id: " + resultDataString["star_id"]);
    }

    // If login succeeds, it will redirect the user to index.html
    /*
    if (resultDataJson["status"] === "success") {
        //window.location.replace("index.html");

        //In the div block, show the movie successsfully added
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        //console.log(resultDataJson["message"]);
        //$("#login_error_message").text(resultDataJson["message"]);
    }

     */
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addstar", {
            method: "POST",
            datatype:"json",
            // Serialize the login form to the data sent by POST request
            data: add_star.serialize(),
            success: result => handleResult(result)
        }
    );
}

// Bind the submit action of the form to a handler function
add_star.submit(submitForm);

