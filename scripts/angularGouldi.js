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
        $scope.updated = function (modelValue, form) {
            var scriptTag = document.createElement('script');
            scriptTag.setAttribute('src', 'widgets/formula-ast-widget.js');
            var payload = new FormData();
            payload.append("latex", modelValue);
            // $http.post('https://vmext-demo.wmflabs.org/math/mathoid', payload,
            //     {
            //         headers: {
            //             'Content-Type': undefined,
            //             'Accept': 'application/xml' //           transformResponse: []
            //
            //         }
            //     }).then(function (res) {
            //     scriptTag.setAttribute('mathml', res.data);
            //     var container = document.getElementById("ast");
            //     container.innerHTML = "";
            //     container.appendChild(scriptTag);
            // });
            $http.post('https://vmext-demo.wmflabs.org/math', payload,
                {
                    headers: {
                        'Content-Type': undefined
                    }
                }).then(function (res) {
                scriptTag.setAttribute('mathml', res.data.result);
                var container = document.getElementById("ast");
                container.innerHTML = "";
                container.appendChild(scriptTag);
            });
        };

        //      $http.get("scripts/sample-eulergamma.mml.xml")
        $scope.onRequest = function(form) {
            // First we broadcast an event so all fields validate themselves
            $scope.$broadcast('schemaFormValidate');

            // Then we check if the form is valid
            if (form.$valid) {
                $http.post('/get-model', $scope.modelrepo).then(function (res) {
                    $scope.gold = res.data;
                    $scope.model = res.data[$scope.modelrepo.itemid];
                    $scope.model.qID = $scope.modelrepo.itemid;
                });
            }
        };

        $scope.previousID = function(model){
            if ( model.qID <= 1 ) return;

            model.qID = model.qID-1;
            updateModules(model);
        };

        $scope.nextID = function(model){
            if ( model.qID >= 200 ) return;

            model.qID = model.qID+1;
            updateModules(model);
        };

        var updateModules = function (model) {
            $http.post('/get-model', $scope.modelrepo).then(function (res) {
                $scope.gold = res.data;
                $scope.model = res.data[model.qID];
                $scope.model.qID = model.qID;
                $scope.modelrepo.itemid = model.qID;
            });
        };

        $scope.onSave = function(form) {
            // First we broadcast an event so all fields validate themselves
            $scope.$broadcast('schemaFormValidate');

            var secureCopy = $scope.gold[$scope.model.qID];
            alert("Until here its fine: " + form.$valid );

            // Then we check if the form is valid
            if ( form.$valid ) {
                try {
                    $scope.gold[$scope.model.qID]=$scope.model;

                    $http.post('/write-model', {
                        user: $scope.modelrepo.owner,
                        repo: $scope.modelrepo.repo,
                        filename: $scope.modelrepo.filename,
                        token: $scope.modelrepo.token,
                        data: $scope.gold
                    }).then( function (res) {
                        alert( "Pushed successfully qID: " + $scope.modelrepo.itemid + "!");
                    });
                } catch (e) {
                    $scope.gold[$scope.model.qID] = secureCopy;
                    $scope.model = secureCopy;
                    $scope.model.qID = $scope.modelrepo.itemid;
                    alert( "It was not possible to push changes! Revered everything!" )
                }
            }
        }
    });