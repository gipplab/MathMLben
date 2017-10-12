/**
 * JS backend for our demo. Handle all calls and logging to the API.
 * @author Vincent
 */

/** Math AST URL */
var mastUrl = "https://vmext.wmflabs.org";

/** simple asynchronoues http client */
var HttpClient = function() {
    // url [String]: full URL to destination
    // body [String]:
    // pCallback [Function]: what to do on positive reply
    this.post = function(url, body, pCallback) {
        var httpRequest = new XMLHttpRequest();
        // prepare callback
        httpRequest.onreadystatechange = function() {
            if (httpRequest.readyState == 4) {
                if (httpRequest.status == 200) {
                    log(httpRequest.responseText);
                    pCallback(httpRequest.responseText);
                } else {
                    // log error
                    log(httpRequest.responseText, true);
                }
            }
        }
        // http execute
        httpRequest.open("POST", url, true);
        httpRequest.send(body);
    }

    this.get = function(url, pCallback) {
        var httpRequest = new XMLHttpRequest();
        // prepare callback
        httpRequest.onreadystatechange = function() {
            if (httpRequest.readyState == 4) {
                if (httpRequest.status == 200) {
                    log(httpRequest.responseText);
                    pCallback(httpRequest.responseText);
                } else {
                    // log error
                    log(httpRequest.responseText);
                }
            }
        }
        // http execute
        httpRequest.open("GET", url, true);
        httpRequest.send(null);
    }
};

function convertLatex() {
    var client = new HttpClient();

    // put the first latex as request body and expect a positive reply with mathml
    log("convert latex field 1 via LaTeXML");
    var formData1 = new FormData();
    formData1.append("latex", document.getElementById("latex1").value);
    formData1.append("config", document.getElementById("latexcfg1").value);
    client.post("/math", formData1, function(serviceRep) {
        var json = JSON.parse(serviceRep);
        document.getElementById("mathml1").value = json.result;
        renderAST(json.result);
        log("conversion of latex field 1 finished");
    });


};

function convertLatexMathoid() {
    var client = new HttpClient();

    // put the first latex as request body and expect a positive reply with mathml
    log("convert latex field 1 via Mathoid");
    var formData1 = new FormData();
    formData1.append("latex", document.getElementById("latex1").value);
    client.post("/math/mathoid", formData1, function(mathml) {
        document.getElementById("mathml1").value = mathml;
        renderAST(mathml);
    });


};



function renderAST(mathml) {
    //var mathml = document.getElementById("mathml1").value;
    var url = mastUrl + '/widgets/formula-ast-widget.js';

    // prepare widget data
    var scriptTag = document.createElement('script');
    scriptTag.setAttribute('src', url);
    scriptTag.setAttribute('mathml', mathml);

    // add script
    log("start to render first AST")
    var container = document.getElementById("ast")
    container.innerHTML = "";
    container.appendChild(scriptTag);
};


/**
 * Request of the configuration from the backend.
 */
function getConfig() {
log("Load default configuration")
    var client = new HttpClient();
    // get latex config
    client.get("/config/latexml", function(config) {
        document.getElementById('latexcfg1').value = config;
        log("latexml configuration loaded")
    });
    // get mast url
    client.get("/config/mast", function(config) {
        mastUrl = config;
        log("mast configuration loaded")
    });
}

function loadExample() {
    log("Load default example");
    var client = new HttpClient();
    // get latex config
    client.get("/math/example", function(example) {
        var json = JSON.parse(example);
        document.getElementById("latex1").value = json.latex1;
        document.getElementById("mathml1").value = json.mathml1;
        log("Example loaded");
        renderAST(json.mathml1);
    });
}

/**
 * This method will change the visibility of a certain field.
 * @param field field to change the visibility
 */
function showCfg(field) {
    if(document.getElementById(field).style.display == "block") {
        document.getElementById(field).style.display = "none";
    } else {
        document.getElementById(field).style.display = "block";
    }
}

/**
 *
 */
function log(text) {
    log(text, false);
}

/**
 *
 */
function log(text, error) {
    if (error)
        console.error(text);
    else
        console.info(text);
    // log into the textfield
    var logarea = document.getElementById('log');
    document.getElementById('log').value = logarea.value + "\n" + text;
    logarea.scrollTop = logarea.scrollHeight;
}
