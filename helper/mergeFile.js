var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var parseAsync = BB.method(JSON.parse);

var goldDir = '../data';
var zillaDir = '../data/mathematica';

var pd = require('pretty-data').pd;
var spawn = require('child-process-promise').spawn;
console.log('Done init...');

if (!fs.existsSync(zillaDir)) {
  console.log('Create zilla dir.');
  fs.mkdirSync(zillaDir);
}

fs.readdirAsync(goldDir).map(function (name) {
  var path = goldDir + '/' + name;
  if (fs.statSync(path).isFile()) {
    return fs.readFileAsync(path, 'utf8')
      .then(function (content) {
        return parseAsync(content);
      })
      .then(function (json) {
        var num = name.split('.')[0];
        console.log();
        console.log('Parse ' + num + ':');
        return json;
      });
  } else {
    console.log('Skip directory ' + name);
  }
}, {concurrency: 1})
  .then(jsons => {return fs.writeFileAsync('gold.json', JSON.stringify(jsons));});