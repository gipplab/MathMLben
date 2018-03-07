var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var parseAsync = BB.method(JSON.parse);

var goldDir  = '../data';
var zillaDir = '../data/texzilla';

var pd = require('pretty-data').pd;
var zilla = require('texzilla');

console.log("Done init...");

if (!fs.existsSync(zillaDir)){
    console.log("Create zilla dir.")
    fs.mkdirSync(zillaDir);
}

console.time("texzillaCreator");
var failCounter = 0;
fs.readdirAsync(goldDir).map( function(name){
    var path = goldDir + "/" + name;
    if ( fs.statSync(path).isFile() ){
        return fs.readFileAsync(path, "utf8")
            .then(function (content){
                return parseAsync(content)
            })
            .then(function (json) {
                var num = name.split(".")[0];
                console.log();
                console.log("Parse " + num + ":");
                try {
                    var tex = json.correct_tex;
                    console.log( "Actual: " + tex );
                    tex = tex.replace( /%(?:\r\n|\r|\n)/g, "" );
                    tex = tex.replace( /^(\\\[)?(\\[.,;!]+)*|[.,;!]*(\\[.,;!]+)*(\\])?$/g, "" );
                    console.log( "Replace:" + tex );
                    var filtered = zilla.filterString( tex, true );
                    var mml = zilla.toMathMLString(filtered);
                    console.log("Successfully parsed " + num);
                    var prettyMML = pd.xml(mml);
                    console.log("Successfully prettify MML.");
                    return fs.writeFileAsync( zillaDir + "/" + num + ".mml", prettyMML );
                } catch ( err ){
                    failCounter++;
                    console.log("Skip " + num + ": " + err.message);
                }
            });
    } else {
        console.log("Skip directory " + name);
    }
}).then(function(){
    console.timeEnd("texzillaCreator");
    console.log(failCounter);
});