require('app-module-path').addPath(__dirname);
var express = require('express');
var app = express();
var path = require("path");

require('app-module-path').addPath(path.join(__dirname + '/node_modules/vmext'));
var bodyParser = require('body-parser');
app.use(bodyParser.json());

var GithubContent = require('github-content');
require('mathoid/server.js');

// Allow CORS
app.use(function (req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
});

app.use('/node_modules', express.static(path.join(__dirname + '/node_modules')));
app.use('/scripts', express.static(path.join(__dirname + '/scripts')));
app.use('/css', express.static(path.join(__dirname + '/css')));
app.use('/widgets', express.static(path.join(__dirname + '/node_modules/vmext/public/widgets')));
app.use('/vendor', express.static(path.join(__dirname + '/node_modules/vmext/public/vendor')));
app.use('/api', require("./node_modules/vmext/api/versions.js"));

app.post('/get-model', function (req, res) {
    var body = req.body;
    var gc = new GithubContent(body);
    gc.file(body.filename, function (err, file) {
        try {
            var json = JSON.parse(file.contents.toString());
            res.send(json);
        } catch (e) {
            res.status(400).send('Invalid JSON string');
        }

    });
});

app.get('/', function (req, res) {
    res.sendFile(path.join(__dirname + '/main.html'));
});

app.listen(8080, function () {
    console.log('Started GoUldI on 8080!');
});