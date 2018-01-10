var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var directory = '../data';
var goldDir = '../data';
var parseAsync = BB.method(JSON.parse);
fs.readdirAsync(goldDir).map(function (name) {
  var path = goldDir + '/' + name;
  if (fs.statSync(path).isFile()) {
    return fs.readFileAsync(path, 'utf8')
      .then(function (content) {
        return parseAsync(content);
      })
      .then(function (json) {
        var num = name.split('.')[0];
        if (num > 100 && num < 201) {
          tex = json.math_inputtex.replace(/%(?:\r\n|\r|\n)/g, '');
          tex = tex.replace(/^(\\\[)?(\\[.,;!]+)*|[.,;!]*(\\[.,;!]+)*(\\])?$/g, '');
          json.correct_tex = tex;
          return fs.writeFileAsync(directory + '/' + name, JSON.stringify(json, null, 2));
        }
      })
      .catch(function (err) {
        console.dir(err);
        console.log('Problem in file' + name);
      });
  } else {
    console.log('Skip directory ' + name);
  }
});
