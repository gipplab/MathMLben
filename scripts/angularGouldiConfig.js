var gouldi = angular.module('gouldiApp');

gouldi.config([
    '$routeProvider', '$locationProvider', function($routeProvider, $locationProvider){
    $routeProvider
    .when('/:qid', {
            templateUrl: 'views/index.html',
            controller: 'GouldiMainController'
        });

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
}]);