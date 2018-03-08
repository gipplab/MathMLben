var gouldi = angular.module('gouldiApp');

gouldi.controller(
    'GouldiMainController',
    ['$scope', '$routeParams', 'marked', 'gouldiCookieService', 'gouldiHttpServices', 'gouldiSharingFactory',
        function ($scope, $routeParams, marked, gouldiCookieService, gouldiHttpServices, gouldiSharingFactory) {
        var gouldiController = this;
        var reroutingID = 1; // default loaded QID is 1

        var init = function(){
            gouldiHttpServices
                .initScripts( $scope )
                .then( function(){
                    console.log("Finished loading process. Init cookies and load actual model.");

                    //console.log("Let us see what's going on here, on init: " + $routeParams.qid);
                    gouldiCookieService.initCookies( $scope.modelrepo );
                    $scope.max = $scope.schema.properties.qID.maximum;
                    $scope.min = $scope.schema.properties.qID.minimum;
                    if (reroutingID >= $scope.min &&
                        reroutingID <= $scope.max) {
                        console.log("Load different qID: " + reroutingID);
                        $scope.model.qID = reroutingID;
                    } else {
                        console.log("Load default qID 1.");
                        $scope.model.qID = 1;
                    }
                    $scope.readModel();
                });

            gouldiHttpServices
                .initAboutPage( $scope )
                .then( function() {
                    var aboutContainer = document.getElementById('about-container-div');
                    aboutContainer.innerHTML = $scope.aboutPage;
            });

            // setup code highlighting block
            $scope.code_language = 'html';
            $scope.code_line_numbering = 'true';
        };

        gouldiController.$onInit = function(){
            console.log("Calling initialization. Init model-schema-form.");
            init();
        };

        $scope.$on('$routeChangeStart', function(angularEvent, next, current) {
            if ( next === undefined ) return;
            console.log("Register rerouting to qID: " + next.params.qid);
            reroutingID = parseInt(next.params.qid);
        });

        $scope.onRequest = function (form){
            $scope.$broadcast('schemaFormValidate');

            // Then we check if the form is valid
            if ( form.$valid ) {
                $scope.readModel();
            }
        };

        $scope.updated = function () {
            if ( !("correct_mml" in $scope.model) ) return;

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

        $scope.changeID = function( newID ){
            if ( newID < $scope.min || $scope.max < newID ) return;
            $scope.model.qID = newID;
            $scope.readModel();
        }

        $scope.previousID = function( model ){
            $scope.changeID( model.qID-1 )
        };

        $scope.nextID = function(model){
            $scope.changeID( model.qID+1 )
        };

        $scope.readModel = function () {
            var id = $scope.model.qID;
            var githubReq = $scope.modelrepo;

            gouldiHttpServices.modelRequest( id, githubReq )
                .then( function (res) {
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

        $scope.broadcastingTest = function(){
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
                return -1;
            } else {
                $scope.$broadcast(
                    'schemaForm.error.token',
                    'necessaryToken',
                    true,
                    'repo'
                );
                // First we broadcast an event so all fields validate themselves
                $scope.$broadcast('schemaFormValidate');
                return 0;
            }
        };

        $scope.onSave = function(form) {
            var returnValue = $scope.broadcastingTest();
            if ( returnValue < 0 ) return;

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

        const cssColorSettings = [
            '#155724', // success text color
            '#c3e6cb', // success border color
            '#d4edda', // success background color
            '#0c5460', // info text color IDX=3
            '#bee5eb', // info border color
            '#d1ecf1', // info background color
            '#856404', // warning text color IDX=6
            '#ffeeba', // warning border color
            '#fff3cd', // warning background color
            '#721c24', // danger text color IDX=9
            '#f5c6cb', // danger border color
            '#f8d7da', // danger background color
        ];

        $scope.logger = function( msg, alert ){
            var help = document.getElementById("logger-info-helper");
            if ( help !== null ){
                help.setAttribute( 'class', "alert " + alert );
                help.innerHTML = JSON.stringify(msg, null, 2);
            }
            var panelDiv = document.getElementById("logger-info-panel").firstChild;
            var defaultIdx = 0; // alert-info level
            if ( alert === 'alert-info' )
                defaultIdx = 3;
            else if ( alert === 'alert-warning')
                defaultIdx = 6;
            else if ( alert === 'alert-danger' )
                defaultIdx = 9;
            panelDiv.style.color = cssColorSettings[defaultIdx];
            panelDiv.style.borderColor = cssColorSettings[defaultIdx+1];
            panelDiv.style.backgroundColor = cssColorSettings[defaultIdx+2];

        };

        $scope.$watch('modelrepo.token', function(){
            if ( 'modelrepo' in $scope ){
                gouldiCookieService.update( $scope.modelrepo );
            }
        });

        $scope.$watch('model.correct_tex', function(){
            if ( !('model' in $scope) || !$scope.model.correct_tex ){
                return;
            }

            var container = document.getElementById('svg-renderer-container');
            var parentContainer = container.parentNode;
            container.innerHTML = "";

            gouldiHttpServices.renderMathRequest($scope.model.correct_tex)
                .then( function(res){
                    container.innerHTML = res.data;
                    parentContainer.setAttribute('class', "alert alert-success math-renderer");
                }).catch( function(e){
                    console.log("ERROR: " + e.data);
                    parentContainer.setAttribute('class', "alert alert-danger math-renderer");
                });
        }, true);

        $scope.$watch('model', function(){
            var model_help = document.getElementById("model-info-helper");
            if ( model_help !== null )
                model_help.innerHTML = JSON.stringify($scope.model, null, 2);
            gouldiSharingFactory.model = $scope.model;
        }, true);

        $scope.$watch(function(){ return gouldiSharingFactory.commitMessage }, function(newVal, oldVal){
            if ( 'model' in $scope ){
                $scope.model.commitMsg = newVal;
            }
        }, true);

        console.log("Finish instantiation of controller!");
    }]);
