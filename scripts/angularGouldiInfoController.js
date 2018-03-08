var gouldi = angular.module('gouldiApp');

gouldi.controller(
    'GouldiInfoController',
    ['$scope', 'gouldiSharingFactory', function ($scope, gouldiSharingFactory) {
        // setup code highlighting block
        $scope.code_language = 'html';
        $scope.code_line_numbering = 'true';

        $scope.$watch(function(){ return gouldiSharingFactory.model }, function(newVal, oldVal){
            $scope.model = newVal;
            if ( newVal ) $scope.code_mml = newVal.correct_mml;
        }, true);

        $scope.updateCommit = function( value ){
            gouldiSharingFactory.commitMessage = value;
        }
    }]);