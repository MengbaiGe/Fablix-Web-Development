let add_movie = $("#add_movie");
/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleResult(resultDataString) {
    console.log("handle addmovie");

        window.alert(resultDataString["msg"]);

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
    let ok = 1;
    $('input[type="text"]').each(function() {
        if ($(this).val() == "" && $(this).attr("name")!="birthyear" ) {
            ok = 0;
        }
    });
    if(ok===0)
    {
        window.alert("please fill all required fields");
        return;
    }

    $.ajax(
        "api/addmovie", {
            method: "POST",
            datatype:"json",
            // Serialize the login form to the data sent by POST request
            data: add_movie.serialize(),
            success: result => handleResult(result)
        }
    );

}

// Bind the submit action of the form to a handler function
add_movie.submit(submitForm);
