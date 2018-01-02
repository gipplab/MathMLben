angular
    .module('gouldiApp', ['schemaForm','ui.bootstrap','ngCookies'])
    .controller('FormController', ['$scope', '$cookies', '$cookieStore', '$http', function ($scope, $cookies, $cookieStore, $http) {
        //$cookies.name = 'gouldi_githubaccesstoken';
        //$scope.platformCookie = $cookies.name;

        var loadFromJson = function (arr) {
            if ( arr.length <= 0 ) {
                console.log("Finished loading process.");
                return;
            }

            var name = arr.pop();
            $http
                .get("scripts/" + name + ".json")
                .then(function (res) {
                    $scope[name] = res.data;
                    console.log("Loaded: " + name);
                    return name;
                })
                .then( function(name){
                    if ( name === 'model' ) {
                        console.log("Reload actual model from GitHub!");
                        $scope.readModel();
                    }
                })
                .then( function() {
                    loadFromJson(arr);
                });
        };

        var loader = [
            'schemarepo',
            'formrepo',
            'modelrepo',
            'model',
            'schema',
            'form'
        ].reverse();

        loadFromJson(loader);

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

                    if ( !('constraints' in $scope.model) )
                        $scope.model.constraints = [];

                    $scope.updated();
                }).then( function(){
                    $scope.logger("Loaded ID: " + id, 'alert-info');
                }).catch( function(err) {
                    $scope.logger(err, 'alert-danger');
            });
        };

        $scope.disableTokenError = function(){
            $scope.$broadcast(
                'schemaForm.error.token',
                'necessaryToken',
                true,
                'repo'
            );
        };

        $scope.generateMathML = function(semantic_tex){
            if ( semantic_tex === "" ) {
                console.log("Empty semantic tex");
                return;
            }

            $http.post('/latexml', {
                latex: semantic_tex
            }).then( function(res) {
                console.log("Created MML!");
                $scope.model.correct_mml = res.data;
                $scope.updated();
                $scope.logger("Successfully created MML!", "alert-success");
            }).catch( function(e) {
                $scope.logger(e.message, 'alert-danger');
            });
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
                $scope.activeForm = 1;
                $scope.logger("Missing Access Token", 'alert-warning');
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
                }).then( function ( res ) {
                    $scope.logger( res, 'alert-success');
                }).catch( function (jsonError) {
                    jsonError.config.data = " ... ";
                    $scope.readModel();
                    $scope.logger( jsonError, 'alert-danger' );
                });
            }
        };

        $scope.logger = function( msg, alert ){
            var help = document.getElementById("logger-info-helper");
            //console.log("Hmm... " + help);
            if ( help !== null ){
                help.setAttribute( 'class', "alert " + alert );
                help.innerHTML = JSON.stringify(msg, null, 2);
            }
        };

        $scope.$watch('model.math_inputtex', function(){
            if ( !('model' in $scope) || !$scope.model.math_inputtex ){
                //console.log("Undefined model!");
                return;
            }

            $http.post('/render-math', {
                input: $scope.model.math_inputtex
            }).then( function(res){
                var container = document.getElementById('svg-renderer-container');
                container.innerHTML = "";
                container.innerHTML = res.data;
            }).catch( function(e){
                console.log("ERROR: " + e.data);
            });
        }, true);

        $scope.$watch('model', function(){
            var model_help = document.getElementById("model-info-helper");
            if ( model_help !== null )
                model_help.innerHTML = JSON.stringify($scope.model, null, 2);
        }, true);

        console.log("Finish instantiation of controller. Load first model!");
    }]);