var BB = require('bluebird');
var fs = BB.promisifyAll(require("fs"));
var rp = require('request-promise');
var directory = '../data';
var parseAsync = BB.method(JSON.parse);
var xml2js = BB.promisifyAll(require('xml2js'));
fs.readdirAsync(directory).map(function (filename) {
    return fs.readFileAsync(directory + "/" + filename, "utf8")
        .then(function (content) {
        return parseAsync(content)
        })
        .then(function (json) {
            if (json.math_inputtex_semantic &&  ! json.math_inputtex) {
                return rp
                    .get(json.uri.replace('#','.')+'.tex')
                    .then(function(res){
                        json.math_inputtex = res;
                        return fs.writeFileAsync(directory + '/' + filename, JSON.stringify(json, null, 2));
                    })
                    .catch(function(err){
                        json.math_inputtex = '';
                        console.log("Problem in file" + filename);
                        return fs.writeFileAsync(directory + '/' + filename, JSON.stringify(json, null, 2));
                        //console.dir(err);
                    });
            }
            });
});