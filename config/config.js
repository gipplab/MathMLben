'use strict';

var yaml = require('js-yaml');
var BBPromise = require('bluebird');

var fs = BBPromise.promisifyAll(require('fs'));


var mathoidcfg = yaml.safeLoad(fs.readFileSync('config.yaml'));
//console.log(JSON.stringify(mathoidcfg))
module.exports = {
    host: 'localhost:8080',
    slack: {
        webhook: 'https://hooks.slack.com/services/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',
        channels: {
            exceptions: '#nodejs_exceptions'
        }
    },
    logs: {
        dir: '/logs',
        level: (process.env.NODE_ENV === 'production') ? 'info' : 'debug'
    },
    mathoidUrl: "http://localhost:" +mathoidcfg.services[0].conf.port
};
