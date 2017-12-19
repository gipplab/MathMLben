var BB = require('bluebird');
var fs = BB.promisifyAll(require("fs"));
var directory = '../data';
var list = Array.apply(null, Array(13)).map(function (_, i) {return i;});
BB.map(list, function(x){
    return fs.writeFileAsync(directory + '/' + (x+288) +'.json', JSON.stringify({}, null, 2));
});
