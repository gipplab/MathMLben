const BB = require('bluebird');
const fs = BB.promisifyAll(require('fs'));
const mathml = require('mathml');

const goldDir = '../data';

const helpers = require('./helperMethods');

fs.readdirAsync(goldDir)
    .filter(function (name) {
        return helpers.fileCheck(goldDir, name);
    })
    .map(function (name) {
        const path = goldDir + '/' + name;
        const num = name.split('.')[0];
        return helpers.parseJSON(path)
            .then(function (json) {
                console.log();
                console.log('Parse ' + num + ': ' + json.title);
                if (json.correct_mml){
                    return {mml:mathml(json.correct_mml).simplifyIds().prefixName(`q${num}.`), id: parseInt(num)};
                } else {
                    return {mml:"",id:parseInt(num)};
                }
            });
    })
    .call("sort", (a, b) => a.id - b.id)
    .reduce((s, j) => s += `\n\n<a href="https://mathmlben.wmflabs.org/${j.id}">${j.id}:</a>\n<br/>${j.mml}<br/>`, "<html> <head>\n" +
        "  <meta charset=\"UTF-8\">\n" +
        "</head> <body>\n\n")
    .then(s => {
            return fs.writeFileAsync('ps.html', s+"\n</body></html>");
        }
    );