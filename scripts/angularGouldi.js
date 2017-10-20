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
                });
        };

        $scope.onSave = function(form) {
            // First we broadcast an event so all fields validate themselves
            $scope.$broadcast('schemaFormValidate');

            var gold = $scope.model;

            // Then we check if the form is valid
            if (form.$valid) {
                $http.post('/write-model', {
                    user: $scope.modelrepo.owner,
                    repo: $scope.modelrepo.repo,
                    filename: $scope.modelrepo.foldername + "/" + $scope.model.qID + ".json",
                    token: $scope.modelrepo.token,
                    data: gold
                }).then(function (res) {
                    alert("Pushed successfully qID: " + $scope.model.qID + "!");
                }).catch(function (e) {
                    readModel();
                    alert("It was not possible to push changes! Revered everything!" + e.data);
                });
            }
        };
    });