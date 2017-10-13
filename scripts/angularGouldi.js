var request = new XMLHttpRequest();
request.open('GET', 'scripts/schema.json', true);

request.onload = function() {
  if (request.status >= 200 && request.status < 400) {
    // Success!
    var data = JSON.parse(request.responseText);
	
	angular.module('gouldiApp', ['schemaForm'])
    .controller('FormController', function($scope, $http) {
		$scope.schema = data; 
			
			$scope.form = [
				"*",
				{
					type: "submit",
					title: "Save"
				}
			];
			
			$scope.model = {};
		});
  } else {
    // We reached our target server, but it returned an error

  }
};

request.onerror = function() {
  // There was a connection error of some sort
};

request.send();
