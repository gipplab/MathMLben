/**
 * JS backend for our demo. Handle all calls and logging to the API.
 * @author Vincent
 */

/** Math AST URL */
var mastUrl = "http://math.citeplag.org";

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
        log("conversion of latex field 1 finished");
    });

    log("convert latex field 2 via LaTeXML");
    // put the second latex as request body and expect a positive reply with mathml
    var formData2 = new FormData();
    formData2.append("latex", document.getElementById("latex2").value);
    formData2.append("config", document.getElementById("latexcfg2").value);
    client.post("/math", formData2, function(serviceRep) {
        var json = JSON.parse(serviceRep);
        document.getElementById("mathml2").value = json.result;
        log("conversion of latex field 2 finished");
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
    });

    // put the second latex as request body and expect a positive reply with mathml
    log("convert latex field 2 via Mathoid");
    var formData2 = new FormData();
    formData2.append("latex", document.getElementById("latex2").value);
    client.post("/math/mathoid", formData2, function(mathml) {
        document.getElementById("mathml2").value = mathml;
    });
};

function getSimilarities(type) {
    var mathml1 = document.getElementById("mathml1").value;
    var mathml2 = document.getElementById("mathml2").value;

    var formData = new FormData();
    formData.append("mathml1", mathml1);
    formData.append("mathml2", mathml2);
    formData.append("type", type);

    log("compare similarities for type: " + type);
    var client = new HttpClient();
    client.post("/math/similarity", formData, function(similarityRep) {
        var json = JSON.parse(similarityRep);
        document.getElementById("sim").value = JSON.stringify(json.result, null, 2);
        log("similarity comparison finished");
    });
};

function renderAST() {
    var mathml = document.getElementById("mathml1").value;
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

function renderCompare() {
    var mathml1 = document.getElementById("mathml1").value;
    var mathml2 = document.getElementById("mathml2").value;
    var sim = document.getElementById("sim").value;
    var url = mastUrl + '/widgets/formula-similarity-widget.js';

    // prepare widget data
    var scriptTag = document.createElement('script');
    scriptTag.setAttribute('src', url);
    scriptTag.setAttribute('reference_mathml', mathml1);
    scriptTag.setAttribute('comparison_mathml', mathml2);
    scriptTag.setAttribute('similarities', sim);

    // add script
    log("start to render comparison")
    var container = document.getElementById("ast")
    container.innerHTML = "";
    container.appendChild(scriptTag);
};

/**
 * Request of the configuration from the backend.
 */
function getConfig() {
    var client = new HttpClient();
    // get latex config
    client.get("/config/latexml", function(config) {
        document.getElementById('latexcfg1').value = config;
        document.getElementById('latexcfg2').value = config;
        log("latexml configuration loaded")
    });
    // get mast url
    client.get("/config/mast", function(config) {
        mastUrl = config;
        log("mast configuration loaded")
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
