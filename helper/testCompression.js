const BB = require('bluebird');
const fs = BB.promisifyAll(require('fs'));
const mathml = require('mathml');
const table = require('markdown-table');
const zlib = BB.promisifyAll(require('zlib'));

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
                let mml = json.correct_mml;
                if (mml) {
                    const mmlShort = mathml(mml).toMinimalPmml().toString();
                    return BB.join(
                        zlib.deflateAsync(mml),
                        zlib.deflateAsync(mmlShort),
                        (comp, shortComp) => {
                            return {
                                MathMLString: mml.length,
                                OptimizedMathMLString: mmlShort.length,
                                MathMLStringURIEncoded: encodeURIComponent(mml).length,
                                MathMLStringBase64Encoded: new Buffer(mml).toString('base64').length,
                                zlibCompressedMathMLStringBase64urlEncoded: comp.toString('base64').length,
                                zlibCompressedMathMLStringBase64Encoded: encodeURIComponent(comp.toString('base64')).length,
                                zlibCompressedOptimizedMathMLStringBase64urlEncoded: shortComp.toString('base64').length
                            }
                        });
                } else {
                    return false;
                }
            });
    })
    .filter(x => typeof x === 'object')
    .reduce((s, j) => {
        Object.entries(j).forEach(
            ([k, v]) => {
                if (s[k]) {
                    const e = s[k];
                    e.min = Math.min(e.min, v);
                    e.max = Math.max(e.max, v);
                    e.cnt += 1;
                    e.sum += v;
                } else {
                    s[k] = {
                        min: v,
                        max: v,
                        cnt: 1,
                        sum: v,
                    }
                }
            }
        );
        return s;
    }, {})
    .then(s => {
        const t = [['name', 'min', 'avg', 'max', 'cnt']];
        Object.entries(s).forEach(
            ([k, v]) => {
                t.push([k, v.min, Math.round(v.sum / v.cnt), v.max, v.cnt]);
            });
        return fs.writeFileAsync('CompressionResults.md', table(t, {
            align: ['l', 'r', 'r', 'r', 'r']
        }));
    });

