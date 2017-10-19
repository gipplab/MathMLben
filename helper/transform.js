var gold = require('./gold.json');
var BB = require('bluebird');
var fs = BB.promisifyAll(require("fs"));
var rp = require('request-promise');

function process(stats) {
    var statsArray = Object.keys(stats).map(function(key) {
        return {
            key: key, value: stats[key]
        };
    });
    return BB.map(statsArray, function(item) {
        return item;
    });
};



BB.reduce(process(gold), function (y, x) {

        var newVal = x.value;
    var options = {
        method: 'POST',
        uri: 'https://vmext-demo.wmflabs.org/math',
        form: {
        // Like <input type="text" name="name">
        latex: newVal.correct_tex
    }
    };

    return  rp(options)
        .then(function (body) {
            newVal.correct_mml = JSON.parse(body).result;
            y[x.key] = newVal;
            return y;
        })

}, {})
    .then(function (y) {
        return fs.writeFileAsync('../data/gold.json', JSON.stringify(y, null, 2));
    })
    .then(function () {
        console.log("done")
    });
