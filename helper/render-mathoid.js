var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var rp = require('request-promise');
var directory = '../data';
var parseAsync = BB.method(JSON.parse);
var xml2js = BB.promisifyAll(require('xml2js'));
var pd = require('pretty-data').pd;
var mathoidDir = '../data/mathoid';

var helpers = require('./helperMethods');

var failCounter = 0;
console.time("mathoidCreator");

var mathoidOptions = {
    method: 'POST',
    uri: 'http://localhost:10044/mml/',
    body: null,
    json: true // Automatically stringifies the body to JSON
};

var requesting = function( options, filename ){
    console.log("Requesting: " + filename);
    return rp(options)
        .then(function (res) {
            var prettyMML = pd.xml(res);
            var num = filename.split('.')[0];
            return fs.writeFileAsync(mathoidDir + '/' + num + '.mml', prettyMML);
        })
        .catch(function (err) {
            console.log('Problem in file' + filename);
            failCounter++;
            return;
        });
};

var mathoidCreator = function(){
    fs.readdirAsync(directory)
        .filter(function (name) {
            return helpers.fileCheck(directory, name);
        })
        .map(function (filename) {
            return helpers.parseJSON( directory + '/' + filename )
                .then(function (json) {
                    if (json.correct_tex) {
                        var tex = helpers.cleanTeX( json.correct_tex );
                        var options = mathoidOptions;
                        options.body = {q: tex};

                        return requesting( options, filename );
                    }
                });
        }).catch(function(e){
        console.log("Hm");
    }).then(function(){
        console.timeEnd("mathoidCreator");
        console.log(failCounter);
    });
};

mathoidCreator();