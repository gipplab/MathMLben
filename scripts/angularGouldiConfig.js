var gouldi = angular.module('gouldiApp');

gouldi.config([
    '$routeProvider', '$locationProvider', function($routeProvider, $locationProvider){
    $routeProvider
    .when('/gold/:qid', {
            templateUrl: 'views/index.html',
            controller: 'GouldiMainController',
            resolve: {
                factory: checkRouting
            }
        });

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
}]);

var checkRouting = function($route, $location){
    var qid = $route.current.params.qid;
    if (
        !/^([1-9]\d*)$/.test(qid)
    ) {
        console.log("Invalid qID, redirect to main page.");
        $location.path("/");
    }
};