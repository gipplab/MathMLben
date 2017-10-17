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
        $scope.onSubmit = function(form) {
            // First we broadcast an event so all fields validate themselves
            $scope.$broadcast('schemaFormValidate');

            // Then we check if the form is valid
            if (form.$valid) {
                $http.post('/get-model', $scope.modelrepo).then(function (res) {
                    $scope.gold= res.data;
                    $scope.model = res.data[$scope.modelrepo.itemid];
                });
            }
        };

        $scope.onSave = function(form) {
            // First we broadcast an event so all fields validate themselves
            $scope.$broadcast('schemaFormValidate');

            // Then we check if the form is valid
            if (form.$valid) {
                $scope.gold[$scope.modelrepo.itemid]=$scope.model;
                $http.post('/write-model', {
                    user: $scope.modelrepo.owner,
                    repo: $scope.modelrepo.repo,
                    filename: $scope.modelrepo.filename,
                    token: $scope.modelrepo.token,
                    data:$scope.gold
                }).then(function (res) {
                    alert(JSON.stringify(res));
                });
            }
        }

    });