var gouldi = angular.module('gouldiApp');

gouldi.factory( 'gouldiSharingFactory', function($rootScope){
    var factoryObject = {
        model: "",
        commitMessage: ""
    };

    return factoryObject;
});