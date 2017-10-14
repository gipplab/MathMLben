angular
    .module('gouldiApp', ['schemaForm'])
    .controller('FormController', function ($scope, $http) {
        var loadFromJson = function (name) {
            $http.get("scripts/" + name + ".json").then(function (res) {
                $scope[name] = res.data;
            });
        };
        loadFromJson('schema');
        loadFromJson('form');
        loadFromJson('model');

        var scriptTag = document.createElement('script');
        scriptTag.setAttribute('src', 'widgets/formula-ast-widget.js');
        $http.get("scripts/sample-eulergamma.mml.xml").then(function (res) {
            scriptTag.setAttribute('mathml', res.data);
            var container = document.getElementById("ast");
            container.innerHTML = "";
            container.appendChild(scriptTag);
        });

    });