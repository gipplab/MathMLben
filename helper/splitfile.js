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



BB.map(process(gold), function (x) {
  return fs.writeFileAsync('../data/'+x.key+'.json', JSON.stringify(x.value, null, 2));
})
  .then(function () {
    console.log("done")
  });