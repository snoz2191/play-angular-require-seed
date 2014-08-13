/*
 * Author: Domingo
 */

var app = angular.module("app", ["ngResource","ngRoute"])
	.constant("apiUrl", "http://localhost:9000\\:9000/api") // to tell AngularJS that 9000 is not a dynamic parameter
	.config(["$routeProvider", function($routeProvider) {
	"use strict";
		return $routeProvider
                    .when("/", {
                        templateUrl: "/assets/templates/main.html",
                        controller: "ListCtrl"
                    })
                    .when("/rank", {
                        templateUrl: "/assets/templates/rank.html",
                        controller: "CreateCtrl"
                    })
                    .otherwise({
                        redirectTo: "/"});

	}]);

// the global controller
app.controller("AppCtrl", ["$scope", "$location", function($scope, $location) {
	// the very sweet go function is inherited to all other controllers
	"use strict";
	$scope.go = function (path) {
		$location.path(path);
	};

	$scope.isActive = function (viewLocation) {
        return viewLocation === $location.path();
    };
}]);

// the list controller
app.controller("ListCtrl", ["$scope", "$resource", "apiUrl", function($scope, $resource, apiUrl) {
    "use strict";
    $scope.TweetText = "Tottenham v Wigan: Match Preview::  Tottenham and Wigan are set to meet at White Hart Lane this Saturday as the ... http://t.co/hCcGRbLu"
    $scope.generate = function(){
        if ($scope.User) {
            $scope.getTweet = true;
        } else {
            $scope.getTweet = false;
        }
    }
	$scope.setPolarity = function (pol){
	    $scope.polarity = pol;

	}
}]);


// the create controller
app.controller("CreateCtrl", ["$scope", "$resource", "$timeout", "apiUrl", function($scope, $resource, $timeout, apiUrl) {
    "use strict";
	// to save a celebrity
	$scope.save = function() {
		var CreateCelebrity = $resource(apiUrl + "/celebrities/new"); // a RESTful-capable resource object
		CreateCelebrity.save($scope.celebrity); // $scope.celebrity comes from the detailForm in public/html/detail.html
		$timeout(function() { $scope.go('/'); }); // go back to public/html/main.html
	};

    $scope.orderProp = 'score';
}]);

// the edit controller
app.controller("EditCtrl", ["$scope", "$resource", "$routeParams", "$timeout", "apiUrl", function($scope, $resource, $routeParams, $timeout, apiUrl) {
	"use strict";
	var ShowCelebrity = $resource(apiUrl + "/celebrities/:id", {id:"@id"}); // a RESTful-capable resource object
	if ($routeParams.id) {
		// retrieve the corresponding celebrity from the database
		// $scope.celebrity.id.$oid is now populated so the Delete button will appear in the detailForm in public/html/detail.html
		$scope.celebrity = ShowCelebrity.get({id: $routeParams.id});
		$scope.dbContent = ShowCelebrity.get({id: $routeParams.id}); // this is used in the noChange function
	}
	
	// decide whether to enable or not the button Save in the detailForm in public/html/detail.html 
	$scope.noChange = function() {
		return angular.equals($scope.celebrity, $scope.dbContent);
	};

	// to update a celebrity
	$scope.save = function() {
		var UpdateCelebrity = $resource(apiUrl + "/celebrities/" + $routeParams.id); // a RESTful-capable resource object
		UpdateCelebrity.save($scope.celebrity); // $scope.celebrity comes from the detailForm in public/html/detail.html
		$timeout(function() { $scope.go('/'); }); // go back to public/html/main.html
	};
	
	// to delete a celebrity
	$scope.delete = function() {
		var DeleteCelebrity = $resource(apiUrl + "/celebrities/" + $routeParams.id); // a RESTful-capable resource object
		DeleteCelebrity.delete(); // $scope.celebrity comes from the detailForm in public/html/detail.html
		$timeout(function() { $scope.go('/'); }); // go back to public/html/main.html
	};
}]);