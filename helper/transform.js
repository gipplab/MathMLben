var gold = require('./gold.json');
var BB = require('bluebird');
var fs = BB.promisifyAll(require("fs"));

BB.reduce(gold, function (y, x) {
    y[x.formula.qID] = x;
    return y;
}, {})
    .then(function (y) {
        return fs.writeFileAsync('../data/gold.json', JSON.stringify(y, null, 2));
    })
    .then(function () {
        console.log("done")
    });