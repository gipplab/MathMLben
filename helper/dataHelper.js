var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));

var goldDir = 'data';

getNumberOfEntries = function() {
    return fs.readdirSync(goldDir, {withFileTypes: true})
        .filter(function(file) {
            if ( file.indexOf(".json") > -1 ) return file;
        })
        .length
}

module.exports = {
    numberOfEntries : getNumberOfEntries
}