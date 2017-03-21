// simple asynchronoues http client
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
                    pCallback(httpRequest.responseText);
                } else {
                    // log error
                    console.error(httpRequest.responseText)
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
                        pCallback(httpRequest.responseText);
                    } else {
                        // log error
                        console.error(httpRequest.responseText)
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
    var formData1 = new FormData();
    formData1.append("latex", document.getElementById("latex1").value);
    formData1.append("config", document.getElementById("latexcfg1").value);
    client.post("/math", formData1, function(mathml) {
        document.getElementById("mathml1").value = mathml;
    });

    // put the second latex as request body and expect a positive reply with mathml
    var formData2 = new FormData();
    formData2.append("latex", document.getElementById("latex2").value);
    formData2.append("config", document.getElementById("latexcfg2").value);
    client.post("/math", formData2, function(mathml) {
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

    var client = new HttpClient();
    client.post("/math/similarity", formData, function(similarity) {
        document.getElementById("sim").value = similarity;
    });
};

function renderAST() {
    var mathml = document.getElementById("mathml1").value;
    var url = 'http://math.citeplag.org/widgets/formula-ast-widget.js';

    // prepare widget data
    var scriptTag = document.createElement('script');
    scriptTag.setAttribute('src', url);
    scriptTag.setAttribute('mathml', mathml);

    // add script
    var container = document.getElementById("ast")
    container.innerHTML = "";
    container.appendChild(scriptTag);
};

function renderCompare() {
    var mathml1 = document.getElementById("mathml1").value;
    var mathml2 = document.getElementById("mathml2").value;
    var sim = document.getElementById("sim").value;
    var url = 'http://math.citeplag.org/widgets/formula-similarity-widget.js';

    // prepare widget data
    var scriptTag = document.createElement('script');
    scriptTag.setAttribute('src', url);
    scriptTag.setAttribute('reference_mathml', mathml1);
    scriptTag.setAttribute('comparison_mathml', mathml2);
    scriptTag.setAttribute('similarities', sim);

    // add script
    var container = document.getElementById("ast")
    container.innerHTML = "";
    container.appendChild(scriptTag);
};

function getConfig() {
    var client = new HttpClient();
    client.get("/math/config", function(config) {
        document.getElementById('latexcfg1').value = config;
        document.getElementById('latexcfg2').value = config;
    });
}

function showCfg(btn) {
    if(document.getElementById(btn).style.display == "block") {
        document.getElementById(btn).style.display = "none";
    } else {
        document.getElementById(btn).style.display = "block";
    }
}