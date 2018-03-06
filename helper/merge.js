var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var parseAsync = BB.method(JSON.parse);

var goldDir = 'data';
var merged = [];

module.exports = {
    merged,
    mergeAllGoldFiles: function() {
        return fs.readdirAsync(goldDir)
            .filter(function (name) {
                var path = goldDir + '/' + name;
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
                        json.qid = parseInt(num);
                        merged[num-1] = json;
                        return null;
                    });
            });
    }
};