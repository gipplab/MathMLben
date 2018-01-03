var gouldi = angular.module('gouldiApp');

gouldi.config([
    '$routeProvider', '$locationProvider', function($routeProvider, $locationProvider){
    $routeProvider
    .when('/:qid', {
            templateUrl: 'views/index.html',
            controller: 'GouldiMainController',
            resolve: {
                "check": function ( $route, $location ){
                    var qid = $route.current.params.qid;
                    if (
                        !/^([1-9]\d*)$/.test(qid)
                    ) {
                        console.log("Redirect to main page.");
                        $location.path("/");
                    }
                }
            }
        });

    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
}]);