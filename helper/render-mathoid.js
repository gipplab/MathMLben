var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var rp = require('request-promise');
var directory = '../data';
var parseAsync = BB.method(JSON.parse);
var xml2js = BB.promisifyAll(require('xml2js'));
var pd = require('pretty-data').pd;
var zillaDir = '../data/mathoid';

fs.readdirAsync(directory).map(function (filename) {
  return fs.readFileAsync(directory + '/' + filename, 'utf8')
    .then(function (content) {
      return parseAsync(content);
    })
    .then(function (json) {
      if (json.math_inputtex) {
        var tex = json.math_inputtex.replace(/%(?:\r\n|\r|\n)/g, '');
        tex = tex.replace(/^(\\\[)?(\\[.,;!]+)*|[.,;!]*(\\[.,;!]+)*(\\])?$/g, '');

        var options = {
          method: 'POST',
          uri: 'http://localhost:10044/mml/',
          body:
            {q: tex}
          ,
          json: true // Automatically stringifies the body to JSON
        };
        return rp(options)
          .then(function (res) {
            var prettyMML = pd.xml(res);
            var num = filename.split('.')[0];
            return fs.writeFileAsync(zillaDir + '/' + num + '.mml', prettyMML);
          })
          .catch(function (err) {
            console.log('Problem in file' + filename);
            //console.dir(err);
          });
      }
    });
});