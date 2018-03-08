var gouldi = angular.module('gouldiApp');

gouldi.service(
    'gouldiHttpServices',
    ['$http', '$q', 'marked', function($http, $q, marked){
        this.initAboutPage = function( $scope ){
            return $http
                .get("/readme")
                .then( function(res){
                    $scope.aboutPage = marked(res.data);
                    console.log("Loaded: about page.");
                    })
                .catch( function(e){
                    console.log("Error during loading the about page. " + e);
                    $scope.aboutPage = "ERROR";
                });
        };

        this.scriptLoader = function( name, $scope ){
            return $http
                .get("scripts/" + name + ".json")
                .then( function( res ){
                    $scope[name] = res.data;
                    console.log("Loaded: " + name);
                });
        };

        this.initScripts = function( $scope ){
            var load = [
                'schemarepo',
                'formrepo',
                'modelrepo',
                'model',
                'schema',
                'form',
            ];

            var promises = [];

            while ( load.length > 0 ){
                promises.push(this.scriptLoader( load.pop(), $scope ));
            }

            return $q.all(promises);
        };

        this.modelRequest = function( id, jsonInfo ){
            var githubReq = jsonInfo;
            githubReq.path = jsonInfo.foldername + "/" + id + ".json";
            return $http.post('/get-model', githubReq);
        };

        this.writeModelRequest = function( repo, data ){
            return $http.post('/write-model', {
                user:       repo.owner,
                repo:       repo.repo,
                filename:   repo.foldername + "/" + data.qID + ".json",
                token:      repo.token,
                data:       data
            });
        };

        this.latexmlRequest = function( semantic_latex ){
            return $http.post('/latexml', {
                latex: semantic_latex
            });
        };

        this.renderMathRequest = function( latex ){
            return $http.post('/render-math', {
                input: latex
            });
        }
    }]
);

gouldi.service('gouldiCookieService',
    ['$cookies', function( $cookies ){
        var id = 'gouldi_githubaccesstoken';

        this.loadCookie = function (){
            return $cookies.get(id);
        };

        this.writeCookie = function ( token ){
            var expireDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
            console.log("Safe: " + id + " - " + token);
            $cookies.put( id, token, {
                expires: expireDate,
                secure: false
            });
        };

        // should be called once after all models and repos are loaded
        this.initCookies = function( modelrepo ){
            var cookie = this.loadCookie();
            if ( cookie ){
                console.log("Found cookie of access token: " + cookie);
                modelrepo.token = cookie;
            }
        };

        this.update = function( modelrepo ){
            var cookie = this.loadCookie();
            if ( modelrepo.token !== "" && modelrepo.token !== cookie ) {
                console.log("Store new access token in cookies.");
                this.writeCookie( modelrepo.token );
            }
        }
    }]
);