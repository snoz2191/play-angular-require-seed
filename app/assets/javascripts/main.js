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
                        controller: "MainCtrl"
                    })
                    .when("/rank", {
                        templateUrl: "/assets/templates/rank.html",
                        controller: "RankCtrl"
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

app.filter('capitalize', function() {
    return function(input, scope) {
        if (input!=null)
            input = input.toLowerCase();
        return input.substring(0,1).toUpperCase()+input.substring(1);
    }
});

// the list controller
app.controller("MainCtrl", ["$scope", "$resource", "apiUrl", "$http", function($scope, $resource, apiUrl, $http) {
    "use strict";
    //$scope.tweet.text = "Tottenham v Wigan: Match Preview::  Tottenham and Wigan are set to meet at White Hart Lane this Saturday as the ... http://t.co/hCcGRbLu"
    $scope.generate = function(){
        $http.get('/tweets/'+ $scope.User).
            success(function(data){
                $scope.tweet = data;
                console.log($scope.tweet)
                $scope.emptied = false;
                $scope.getTweet = true;
            }).
            error(function(status){
                $scope.emptied = true;
                $scope.getTweet = false;
            });
    }

	$scope.vote = function( vote ){
        $scope.polarity = vote;
        if ( vote == "negative") {
            $scope.tweet.negvote += 1;
        } else if ( vote == "neutral") {
            $scope.tweet.neuvote += 1;
        } else if ( vote == "positive") {
            $scope.tweet.posivote += 1;
        }
        console.log($scope.tweet);
        $http.post('/tweets' , $scope.tweet).
                    success(function(){
                    }).
                    error(function(status){
                    });
	}
}]);


// the create controller
app.controller("RankCtrl", ["$scope", "$resource", "$timeout", "apiUrl", "$http", function($scope, $resource, $timeout, apiUrl, $http) {
    "use strict";
    $http.get('/rank').
        success(function(data){
            $scope.users = data
            console.log($scope.users)
        }).
        error(function(status){
        });
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