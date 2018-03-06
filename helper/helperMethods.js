var BB = require('bluebird');
var fs = BB.promisifyAll(require('fs'));
var parseAsync = BB.method(JSON.parse);

module.exports = {
    fileCheck: function(directory, name){
        var path = directory + '/' + name;
        return fs.statSync(path).isFile();
    },

    parseJSON: function( fileName ){
        return fs.readFileAsync( fileName, 'utf8' )
            .then(function(content) {
                return parseAsync(content);
            })
    },

    cleanTeX: function( input ){
        var tex = input.replace(/%(?:\r\n|\r|\n)/g, '');
        return tex.replace(/^(\\\[)?(\\[.,;!]+)*|[.,;!]*(\\[.,;!]+)*(\\])?$/g, '');
    }
};