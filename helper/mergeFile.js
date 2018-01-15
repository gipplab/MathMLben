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
    var num = name.split('.')[0];
    if ( num > 200 ){
        console.log('Skip high numbers... ' + num);
        return;
    }

    return fs.readFileAsync(path, 'utf8')
      .then(function (content) {
        return parseAsync(content);
      })
      .then(function (json) {
        //var num = name.split('.')[0];
        console.log();
        console.log('Parse ' + num + ': ' + json.title);
        json.formula={
          qID : ''+name.replace(/\.json/,'') || '0',
          math_inputtex : json.math_inputtex,
          fid : ''+json.fid || '0',
          title : json.title || 'unknown title',
          oldId : ''+json.oldId || '0'
        };
        return json;
      });
  } else {
    console.log('Skip directory ' + name);
    return {};
  }
})
  .then(jsons => {return fs.writeFileAsync('gold.json', JSON.stringify(jsons,null,2));});