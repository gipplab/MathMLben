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
  var num = name.split('.')[0];
  if (fs.statSync(path).isFile()) {
    return fs.readFileAsync(path, 'utf8')
      .then(function (content) {
        return parseAsync(content);
      })
      .then(function (json) {
        console.log();
        console.log('Parse ' + num + ':');
        try {
          var tex = json.math_inputtex;
          console.log('Actual: ' + tex);
          tex = tex.replace(/%(?:\r\n|\r|\n)/g, '');
          tex = tex.replace(/^(\\\[)?(\\[.,;!]+)*|[.,;!]*(\\[.,;!]+)*(\\])?$/g, '');
          console.log('Replace:' + tex);
        } catch (err) {
          console.log('Skip ' + num + ': ' + err.message);
        }
        return tex;
      }).then(tex => {
        fs.writeFileAsync( zillaDir + "/" + num + ".tex", tex );
        return spawn('wolframscript', ['-file', 'tex2mml.wl', tex], { capture: [ 'stdout', 'stderr' ]}).then(result => {
            const stdOut = result.stdout.toString();
            if (stdOut){
              console.log("Successfully parsed " + num);
              var prettyMML = pd.xml(stdOut);
              console.log("Successfully prettify MML.");
              return fs.writeFileAsync( zillaDir + "/" + num + ".mml", prettyMML );
            } else {
              throw result.stderr.toString();
            }

          });
        }
      );
  } else {
    console.log('Skip directory ' + name);
  }
},{concurrency: 1}); //Otherwise, runtime errors occur: Mathematia complains about a missing license