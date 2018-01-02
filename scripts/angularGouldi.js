var gouldi = angular.module('gouldiApp', ['schemaForm','ui.bootstrap','ngCookies']);

gouldi.service(
    'gouldiHttpServices',
    ['$http', '$q', function($http, $q){
        this.scriptLoader = function( name, $scope ){
            return $http
                .get("scripts/" + name + ".json")
                .then( function( res ){
                    $scope[name] = res.data;
                    console.log("Loaded: " + name);
                });
        };

        this.initScripts = function( $scope ){
            var load = [
                'schemarepo',
                'formrepo',
                'modelrepo',
                'model',
                'schema',
                'form'
            ];

            var promises = [];

            while ( load.length > 0 ){
                promises.push(this.scriptLoader( load.pop(), $scope ));
            }

            return $q.all(promises);
        };

        this.modelRequest = function( id, jsonInfo ){
            var githubReq = jsonInfo;
            githubReq.path = jsonInfo.foldername + "/" + id + ".json";
            return $http.post('/get-model', githubReq);
        };

        this.writeModelRequest = function( repo, data ){
            return $http.post('/write-model', {
                user:       repo.owner,
                repo:       repo.repo,
                filename:   repo.foldername + "/" + data.qID + ".json",
                token:      repo.token,
                data:       data
            });
        };

        this.latexmlRequest = function( semantic_latex ){
            return $http.post('/latexml', {
                latex: semantic_latex
            });
        };

        this.renderMathRequest = function( latex ){
            return $http.post('/render-math', {
                input: latex
            });
        }
    }]
);

gouldi.service('gouldiCookieService',
    ['$cookies', function( $cookies ){
        var id = 'gouldi_githubaccesstoken';

        this.loadCookie = function (){
            return $cookies.get(id);
        };

        this.writeCookie = function ( token ){
            var expireDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
            console.log("Safe: " + id + " - " + token);
            $cookies.put( id, token, {
                expires: expireDate,
                secure: false
            });
        };

        // should be called once after all models and repos are loaded
        this.initCookies = function( modelrepo ){
            var cookie = this.loadCookie();
            if ( cookie ){
                console.log("Found cookie of access token: " + cookie);
                modelrepo.token = cookie;
            }
        };

        this.update = function( modelrepo ){
            var cookie = this.loadCookie();
            if ( modelrepo.token !== "" && modelrepo.token !== cookie ) {
                console.log("Store new access token in cookies.");
                this.writeCookie( modelrepo.token );
            }
        }
    }]
);

gouldi.controller('FormController', ['$scope', 'gouldiCookieService', 'gouldiHttpServices', function ($scope, gouldiCookieService, gouldiHttpServices) {
        var init = function( ){
            gouldiHttpServices
                .initScripts( $scope )
                .then( function(){
                    console.log("Finished loading process. Init cookies and load actual model.");
                    $scope.readModel();
                    gouldiCookieService.initCookies( $scope.modelrepo );
            });
        };

        init();

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
            if ( model.qID <= $scope.schema.qID.minimum ) return;

            model.qID = model.qID-1;
            $scope.readModel();
        };

        $scope.nextID = function(model){
            if ( model.qID >= $scope.schema.qID.maximum ) return;

            model.qID = model.qID+1;
            $scope.readModel();
        };

        $scope.readModel = function () {
            var id = $scope.model.qID;
            var githubReq = $scope.modelrepo;

            gouldiHttpServices.modelRequest( id, githubReq )
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

            gouldiHttpServices.latexmlRequest(semantic_tex)
                .then( function(res) {
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

            // Then we check if the form is valid
            if (form.$valid) {
                gouldiHttpServices.writeModelRequest($scope.modelrepo, $scope.model)
                    .then(function (res) {
                        $scope.logger(res, 'alert-success');
                    }).catch(function (jsonError) {
                        jsonError.config.data = " ... ";
                        $scope.readModel();
                        $scope.logger(jsonError, 'alert-danger');
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

        $scope.$watch('modelrepo.token', function(){
            if ( 'modelrepo' in $scope ){
                gouldiCookieService.update( $scope.modelrepo );
            }
        });

        $scope.$watch('model.math_inputtex', function(){
            if ( !('model' in $scope) || !$scope.model.math_inputtex ){
                //console.log("Undefined model!");
                return;
            }

            gouldiHttpServices.renderMathRequest($scope.model.math_inputtex)
                .then( function(res){
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

        console.log("Finish instantiation of controller!");
    }]);