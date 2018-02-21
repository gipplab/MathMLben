var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var parseAsync = BB.method(JSON.parse);

var goldDir = '../data';
var dataArr = [['QID', 'URI', 'Location']];

fs.readdirAsync(goldDir)
    .filter( function (name) {
        var path = goldDir + '/' + name;
        var isFile = fs.statSync(path).isFile();
        if (!isFile) console.log("Skip directories.");
        return fs.statSync(path).isFile()
    })
    .map( function (name) {
        var path = goldDir + '/' + name;
        var num = name.split('.')[0];

        return fs.readFileAsync(path, 'utf8')
            .then(function (content) {
                return parseAsync(content);
            })
            .then(function (json) {
                console.log('Parse ' + num + ': ' + json.title);

                if (num <= 200) {
                    dataArr[num] = [num, json.uri, ''];
                } else {
                    dataArr[num] = [num, json.uri, "Page: " + json.page + "; Formula: " + json.formula];
                }
            });
    })
    .then( function(){
        var str = "";
        //console.log(dataArr);
        dataArr.forEach(function(row){
            // console.log(row);
            str += row.join(",") + "\n";
        });
        fs.writeFileAsync('../views/dataSources.csv', str)
            .then( console.log("Done!") );
    });