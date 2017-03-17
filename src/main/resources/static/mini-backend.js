// simple asynchronoues http client
var HttpClient = function() {
    // url [String]: full URL to destination
    // body [String]:
    // pCallback [Function]: what to do on positive reply
    this.post = function(url, body, pCallback) {
        var httpRequest = new XMLHttpRequest();
        // prepare callback
        httpRequest.onreadystatechange = function() {
            if (httpRequest.readyState == 4 && httpRequest.status == 200) {
                pCallback(httpRequest.responseText);
            }
        }
        // http execute
        httpRequest.open("POST", url, true);
        httpRequest.send(body);
    }
};

function convertLatex() {
    var client = new HttpClient();
    // put the first latex as request body and expect a positive reply with mathml
    var latex = document.getElementById("latex1").value;
    client.post("/math", latex, function(mathml) {
        document.getElementById("mathml1").value = mathml;
    });

    // put the second latex as request body and expect a positive reply with mathml
    latex = document.getElementById("latex2").value;
    client.post("/math", latex, function(mathml) {
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