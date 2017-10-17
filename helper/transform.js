var gold = require('./gold.json');
var BB = require('bluebird');
var fs = BB.promisifyAll(require("fs"));

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
    Object.assign(newVal,x.value.formula);
    delete newVal.formula;
    delete newVal.qID;
    newVal.uri = "https://en.formulasearchengine.com/w/index.php?oldid=" + newVal.oldId + "#math"+ newVal.oldId + "." +newVal.fid;
    delete newVal.oldId;
    delete newVal.fid;
    newVal.correct_tex = newVal.math_inputtex;
    y[x.key] = newVal;
    return y;
}, {})
    .then(function (y) {
        return fs.writeFileAsync('../data/gold.json', JSON.stringify(y, null, 2));
    })
    .then(function () {
        console.log("done")
    });
