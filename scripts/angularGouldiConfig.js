var gouldi = angular.module('gouldiApp');

gouldi.config([
    '$routeProvider', '$locationProvider', 'markedProvider',
    function($routeProvider, $locationProvider, markedProvider){

    // setup rerouting for QIDs
    $routeProvider
        .when('/:qid', {
            templateUrl: 'views/index.html',
            controller: 'GouldiMainController',
            resolve: {
                factory: checkRouting
            }
        });

    // config to allow HTML
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });

    // config markedProvider
    markedProvider.setOptions({
        renderer: new marked.Renderer(),
        gfm: true,          // flavoured markdown (used in GitHub)
        tables: true,       // requires gfm
        breaks: true,       // requires gfm
        pedantic: false,    // do not try to fix bugs in original markdown
        sanitize: false,    // ignores html in markdown file
        smartLists: true,   // use smarter lists behavior (maybe no effect)
        smartypants: true,  // smart typographic punctuation
        xhtml: true         // use xthml
    });
}]);

var checkRouting = function($route, $location){
    var qid = $route.current.params.qid;
    console.log("Na geht doch!");
    if (
        !/^([0-9]\d*)$/g.test(qid)
    ) {
        console.log("Invalid qID, redirect to main page.");
        $location.path("/");
    }
};