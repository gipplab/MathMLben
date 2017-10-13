var express 	= require('express');
var app 		= express();
var path 		= require("path");

app.use( '/node_modules', express.static( path.join(__dirname + '/node_modules') ) );
app.use( '/scripts', express.static( path.join(__dirname + '/scripts') ) );

app.get('/', function (req, res) {
	res.sendFile( path.join(__dirname + '/main.html') );
});

app.listen(8080, function () {
	console.log('Started GoUldI on 8080!');
});
