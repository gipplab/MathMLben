require('app-module-path').addPath(__dirname);

const express = require('express');
const app = express();
const path = require("path");
const yaml = require('js-yaml');
const BBPromise = require('bluebird');
const preq = require('preq');
const fs = BBPromise.promisifyAll(require('fs'));
const mathoidcfg = yaml.safeLoad(fs.readFileSync('config.yaml'));

//var readme = fs.readFileSync('views/README.md').toString();

require('app-module-path').addPath(path.join(__dirname + '/node_modules/vmext'));
const bodyParser = require('body-parser');

app.use(bodyParser.json({limit: "50mb"}));
app.use(bodyParser.urlencoded({limit: "50mb", extended: true, parameterLimit:50000}));
const GithubContent = require('github-content');
require('mathoid/server.js');

const githubChangeRemoteFile = require('github-change-remote-file');

const mergeHelper = require('helper/merge.js');

// add middleware to:
// 1) Allow CORS
app.use(function (req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
});

// need to specify a view because of
// --> app.use('/', require('./node_modules/vmext/routes/routes'));
// therefore we need ejs and set the view to html --> switch main.html to view/index.html
app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');

// needed subpath gold because it is the new subpath of the GUI
app.use('/node_modules', express.static(path.join(__dirname + '/node_modules')));
app.use('/scripts', express.static(path.join(__dirname + '/scripts')));
app.use('/styles', express.static(path.join(__dirname + '/styles')));
app.use('/widgets', express.static(path.join(__dirname + '/node_modules/vmext/public/widgets')));
app.use('/vendor', express.static(path.join(__dirname + '/node_modules/vmext/public/vendor')));
app.use('/assets', express.static(path.join(__dirname + '/assets')));
app.use('/readme', express.static(path.join(__dirname + '/views/README.md')));
app.use('/datasetSources', express.static(path.join(__dirname + '/views/dataSources.csv')));
app.use('/api', require("./node_modules/vmext/api/versions.js"));
app.use('/', require('./node_modules/vmext/routes/routes'));

app.post('/get-model', function (req, res) {
    const body = req.body;
    const gc = new GithubContent(body);
    gc.file(body.filename, function (err, file) {
        try {
            const json = JSON.parse(file.contents.toString());
            res.send(json);
        } catch (e) {
            res.status(400).send('Invalid JSON string');
        }
    });
});

app.post('/write-model', function (req, res) {
    const body = req.body;
    body.transform = function (x) {
        body.message = body.data.commitMsg || `Update item ${body.data.qID} \n\n[ci skip]`;
        delete body.data.commitMsg;
        delete body.data.qID;
        return JSON.stringify(body.data, null, 2);
    };
    githubChangeRemoteFile(body)
        .then(function (inner_res) {
            res.send( inner_res );
        })
        .catch(function (err) {
            throw err;
        });
});

app.post('/render-math', function (req, res){
    preq.post(
        {
            uri: 'http://localhost:10044/svg/',
            encoding: null,
            body: {q: req.body.input, nospeech: true}
        }
    ).then( function(inner_res) {
        res.send( inner_res.body.toString() );
    }).catch( function(e){
        res.status(400).send('Error while render SVG: ' + e.message);
    } );
});

app.post('/latexml', function(req, res){
    preq.post(
        {
            uri: 'http://vmext-demo.wmflabs.org/math/',
            query: {
                latex: req.body.latex
            }
        }
    ).then( function(inner_res){
        res.send( inner_res.body.result );
    }).catch( function(e){
        res.status(400).send('Error while request LaTeXML: ' + e.message);
    });
});

app.get('/dataset', function(req, res){
    res.sendFile( "views/dataSourcesTemplate.html", { root: __dirname } );
});

app.get('/about', function(req, res, next) {
    res.sendFile( "views/aboutPage.html", { root: __dirname } );
});

app.get('/rawdata/:qid', function(req, res){
    const qid = req.params.qid;
    if ( /^([0-9]\d*)$/g.test(qid) ){
        const num = parseInt(qid);
        let maxID = 320;
        if ( num < 1 || maxID < num ){
            res.status(400).send( 'Invalid QID request: ' + qid + ' is out of range. Must be between 1 and 307.' );
        } else {
            console.log("Requesting single raw file of QID: " + num);
            res.sendFile( "data/"+qid+".json", { root: __dirname } );
        }
    } else if ( qid.match(/^all$/g) ){
        console.log("Requesting complete dataset!");
        console.dir(req.headers); // provide request information
        mergeHelper.mergeAllGoldFiles()
           .then(() => {
               console.log("Done, send entire gold standard.");
               res.send(mergeHelper.merged);
           });
    } else {
        res.status(400).send('Invalid QID request: ' + qid + ' is not a number! For the entire gold standard call "rawdata/all-gen"');
    }
});

app.all('/*', function(req, res, next) {
    // Just send the index.html for other files to support HTML5Mode
    res.sendFile('views/index.html', { root: __dirname });
});

app.use('/', function(req, res){
    res.redirect('about');
});

const port = 34512; //process.env.GOULDI_PORT  | mathoidcfg.gouldi.port;
app.listen( port, function () {
    console.log('Started GoUldI on ' + port);
});
