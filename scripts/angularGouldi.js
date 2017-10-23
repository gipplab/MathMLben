angular
    .module('gouldiApp', ['schemaForm'])
    .controller('FormController', function ($scope, $http) {
        var loadFromJson = function (name) {
            $http.get("scripts/" + name + ".json").then(function (res) {
                $scope[name] = res.data;
            });
        };

        loadFromJson('schemarepo');
        loadFromJson('formrepo');
        loadFromJson('modelrepo');
        loadFromJson('model');
        loadFromJson('schema');
        loadFromJson('form');

        $scope.onRequest = function (form){
            $scope.$broadcast('schemaFormValidate');

            // Then we check if the form is valid
            if ( form.$valid ) {
                $scope.readModel();
            }
        };

        $scope.updated = function () {
            var scriptTag = document.createElement('script');
            scriptTag.setAttribute('src', 'widgets/formula-ast-widget.js');

            scriptTag.setAttribute('mathml', $scope.model.correct_mml);
            var container = document.getElementById("ast");
            container.innerHTML = "";
            container.appendChild(scriptTag);
        };

        $scope.setID = function(){
            $scope.readModel($scope.form);
        };

        $scope.previousID = function( model ){
            if ( model.qID <= 1 ) return;

            model.qID = model.qID-1;
            $scope.readModel();
        };

        $scope.nextID = function(model){
            if ( model.qID >= 300 ) return;

            model.qID = model.qID+1;
            $scope.readModel();
        };

        $scope.readModel = function () {
            var id = $scope.model.qID;
            var githubReq = $scope.modelrepo;
            githubReq.path =
                $scope.modelrepo.foldername + "/" + id + ".json";

            $http
                .post('/get-model', githubReq)
                .then(function (res) {
                    $scope.model = res.data;
                    $scope.model.qID = id;
                    $scope.updated();
                }).then( function(){
                    var help = document.getElementsByClassName("alert alert-info").item(0);
                    help.innerHTML = "Successfully loaded ID: " + id;
            } );
        };

        $scope.disableTokenError = function(){
            $scope.$broadcast(
                'schemaForm.error.token',
                'necessaryToken',
                true,
                'repo'
            );
        };

        $scope.onSave = function(form) {
            if ( $scope.modelrepo.token === "" ){
                $scope.$broadcast(
                    'schemaForm.error.token',
                    'necessaryToken',
                    'An access token is necessary to push changes to GitHub!',
                    'repo'
                );
                // First we broadcast an event so all fields validate themselves
                $scope.$broadcast('schemaFormValidate');
                return;
            } else {
                $scope.$broadcast(
                    'schemaForm.error.token',
                    'necessaryToken',
                    true,
                    'repo'
                );
                // First we broadcast an event so all fields validate themselves
                $scope.$broadcast('schemaFormValidate');
            }

            var gold = $scope.model;

            // Then we check if the form is valid
            if (form.$valid) {
                $http.post('/write-model', {
                    user: $scope.modelrepo.owner,
                    repo: $scope.modelrepo.repo,
                    filename: $scope.modelrepo.foldername + "/" + $scope.model.qID + ".json",
                    token: $scope.modelrepo.token,
                    data: gold
                }).then( function (res) {
                    var help = document.getElementsByClassName("alert alert-info").item(0);
                    help.innerHTML = "Successfully pushed changes for ID: " + id;
                }).catch( function (jsonError) {
                    jsonError.config.data = " ... ";
                    $scope.readModel();
                    var help = document.getElementsByClassName("alert alert-info").item(0);
                    help.setAttribute( 'class', 'alert alert-danger' );
                    help.innerHTML = "ERROR! " + JSON.stringify(jsonError,null,2);
                });
            }
        };
    });