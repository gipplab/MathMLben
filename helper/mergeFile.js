var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var parseAsync = BB.method(JSON.parse);

var goldDir = '../data';

fs.readdirAsync(goldDir)
    .filter(function (name) {
        var path = goldDir + '/' + name;
        var isFile = fs.statSync(path).isFile();
        if (!isFile) console.log("Skip directories.");
        return fs.statSync(path).isFile()
    })
    .map(function (name) {
        var path = goldDir + '/' + name;
        var num = name.split('.')[0];

        return fs.readFileAsync(path, 'utf8')
            .then(function (content) {
                return parseAsync(content);
            })
            .then(function (json) {
                console.log();
                console.log('Parse ' + num + ': ' + json.title);

                json.formula = {
                    qID: '' + name.replace(/\.json/, '') || '0',
                    math_inputtex: json.math_inputtex,
                    fid: (json.fid | -1) + "",
                    title: json.title || 'unknown title',
                    oldId: (json.oldId | -1) + ""
                };

                if (num < 100) {
                    var match = /.*?oldid=(\d+)#.*?\.(\d+)/.exec(json.uri);
                    json.formula.oldId = match[1];
                    json.formula.fid = match[2];
                }

                return json;
            });
    })
    .then(jsons => {
            return fs.writeFileAsync('gold.json', JSON.stringify(jsons, null, 2));
        }
    );