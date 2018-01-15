var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var parseAsync = BB.method(JSON.parse);

var goldDir = '../data';

fs.readdirAsync(goldDir).map(function (name) {
  var path = goldDir + '/' + name;
  var num = name.split('.')[0];

  if (fs.statSync(path).isFile() && num <= 200 ) {
    return fs.readFileAsync(path, 'utf8')
      .then(function (content) {
        return parseAsync(content);
      })
      .then(function (json) {
        console.log();
        console.log('Parse ' + num + ': ' + json.title);

        json.formula={
          qID : ''+name.replace(/\.json/,'') || '0',
          math_inputtex : json.math_inputtex,
          fid : (json.fid | -1) + "",
          title : json.title || 'unknown title',
          oldId : (json.oldId | -1) + ""
        };

        if ( num < 100 ){
          var match = /.*?oldid=(\d+)#.*?\.(\d+)/.exec( json.uri );
          json.formula.oldId = match[1];
          json.formula.fid = match[2];
        }

        return json;
      });
  } else if ( num > 200 ) {
    console.log('Skip high numbers... ' + num);
  } else {
    console.log('Skip directory ' + name);
  }
}).then(jsons => {return fs.writeFileAsync('gold.json', JSON.stringify(jsons,null,2));});