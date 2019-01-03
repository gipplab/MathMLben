const BB = require('bluebird');
const fs = BB.promisifyAll(require('fs'));

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
                const tex = (json.correct_mml||"")
                    .replace(/\n/g,' ');
                return {tex, id: parseInt(num)};
            });
    })
    .call("sort", (a, b) => a.id - b.id)
    .reduce((s, j) => s += `${j.tex} <!-- https://mathmlben.wmflabs.org/${j.id}-->\n`, "")
    .then(s => {
            return fs.writeFileAsync('ps.mml.xml', s);
        }
    );