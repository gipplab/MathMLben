var BB = require('bluebird');
var fs = BB.promisifyAll(require("fs"));
var rp = require('request-promise');
var directory = '../data';
var parseAsync = BB.method(JSON.parse);
var xml2js = BB.promisifyAll(require('xml2js'));
fs.readdirAsync(directory).map(function (filename) {
    return fs.readFileAsync(directory + "/" + filename, "utf8").then(function (content) {
        return parseAsync(content).then(function (json) {
            if (json.math_inputtex_semantic) {
                xml2js.parseStringAsync(json.correct_mml).then(function (result) {
                    json.math_inputtex = result.math.$.alttext;
                    return fs.writeFileAsync(directory + '/' + filename, JSON.stringify(json, null, 2));
                }).catch(function (err) {
                    console.log("Problem in file" + filename);
                    console.dir(err);
                });
            }
        });
    });
});