var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));

var goldDir = '../data';
var dataArr = [['QID', 'URI', 'Location']];

var helpers = require('./helperMethods');

fs.readdirAsync(goldDir)
    .filter( function (name) {
        return helpers.fileCheck(goldDir, name);
    })
    .map( function (name) {
        var path = goldDir + '/' + name;
        var num = name.split('.')[0];

        return helpers.parseJSON(path)
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