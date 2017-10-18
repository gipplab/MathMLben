// load and concatenate arrays of gold standard
var gold_wiki = require('./gold.json');
var gold_dlmf = require('./gold_dlmf.json');
var gold = gold_wiki.concat( gold_dlmf );

// use bluebird to process map and reduce
var BB = require('bluebird');
var fs = BB.promisifyAll(require("fs"));

BB.reduce(gold, function (new_gold, gold_element) {
    var new_element = gold_element;    // manipulate gold_element

    // get rid off "formula"
    Object.assign( new_element, gold_element.formula );
    delete new_element.formula; // delete "formula"

    // copy tex for "correct_tex"
    if ( new_element.hasOwnProperty( "math_inputtex" ) ){
        new_element.correct_tex = new_element.math_inputtex;
    } else if ( new_element.hasOwnProperty( "math_inputtex_semantic" ) ){
        new_element.correct_tex = new_element.math_inputtex_semantic;
    } // add here other cases for other gold standards

    // build uri and delete IDs
    if ( new_element.hasOwnProperty( "fid" ) ){
        new_element.uri =
            "https://en.formulasearchengine.com/w/index.php?oldid="
            + new_element.oldId
            + "#math"
            + new_element.oldId
            + "."
            + new_element.fid;
        delete new_element.oldId;
        delete new_element.fid;
    } else if ( new_element.hasOwnProperty( "dlmfId" ) ){
        new_element.uri =
            "http://dlmf.nist.gov/"
            + new_element.dlmfId;
        delete new_element.dlmfId;
    }

    // safe element with qID as key and get rid off qID-element
    new_gold[new_element.qID] = gold_element;
    delete new_element.qID;

    // done
    return new_gold;
}, {})
    .then( function( new_gold_standard ) {
        return fs.writeFileAsync(
            '../data/gold.json',
            JSON.stringify(new_gold_standard, null, 2)
        );
    })
    .then( function() {
        console.log("done");
    });