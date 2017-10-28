(function () {
    var cmpe281AwsApp = angular.module('cmpe281AwsWebSer', ['ngRoute', 'angularUtils.directives.dirPagination']);

    cmpe281AwsApp.directive('active', function ($location) {
        return {
            link: function (scope, element) {
                function makeActiveIfMatchesCurrentPath() {
                    if ($location.path().indexOf(element.find('a').attr('href').substr(1)) > -1) {
                        element.addClass('active');
                    } else {
                        element.removeClass('active');
                    }
                }

                scope.$on('$routeChangeSuccess', function () {
                    makeActiveIfMatchesCurrentPath();
                });
            }
        };
    });
    
    cmpe281AwsApp.directive('fileModel', [ '$parse', function($parse) {
    	return {
    		restrict : 'A',
    		link : function(scope, element, attrs) {
    			var model = $parse(attrs.fileModel);
    			var modelSetter = model.assign;

    			element.bind('change', function() {
    				scope.$apply(function() {
    					modelSetter(scope, element[0].files[0]);
    				});
    			});
    		}
    	};
    } ]);
    
    cmpe281AwsApp.controller('CreateUserCtrl', function ($scope, $location, $http) {
        var self = this;
        
        self.add = function () {            
        	var userModel = self.model;        	
        	var savedUser;
        	
        	var formData = new FormData();
        	formData.append('firstName', userModel.firstName);
        	formData.append('lastName', userModel.lastName);
        	formData.append('image', userModel.image);
          formData.append('description', userModel.description);
        		
        	$scope.saving=true;
        	$http.post('/app/users', formData, {	
        	    transformRequest : angular.identity,
    			headers : {
    				'Content-Type' : undefined
    			}
    		}).success(function(savedUser) {
    			$scope.saving=false;
    			$location.path("/view-user/" + savedUser.id);    			
    		}).error(function(data) {
    			$scope.saving=false; 
    		});
        };
    });
  
      cmpe281AwsApp.controller('UpdateUserCtrl', function ($scope, $location, $http, $routeParams) {
        var self = this; 
        
        var userId = $routeParams.userId;    	        
          $http.get('/app/users/' + userId).then(function onSuccess(response) {
        	$scope.user = response.data;
          self.delete = function (userId) {
        	$scope.selectedUser = userId;
        	$scope.userDelete = true;
        	$http.delete('/app/users/' + userId).then(function onSuccess(response) {
            	$scope.users = _.without($scope.users, _.findWhere($scope.users, {id: userId}));
            	$scope.userDelete = false;
            }, function onError(){});
        };
        }, function onError(response) {
        	$scope.user = response.statusText;
        });
        
        self.update = function () {            
        	var userModel = self.model;        	
        	var savedUser;
        	
        	var formData = new FormData();
          formData.append('uid', $scope.user.id);
        	formData.append('firstName', $scope.user.firstName);
        	formData.append('lastName', $scope.user.lastName);
        	formData.append('image', userModel.image);
          formData.append('description', $scope.user.description);
          formData.append('iid', $scope.user.userImage.id);
          formData.append('key', $scope.user.userImage.key);
          formData.append('url', $scope.user.userImage.url);
        		
        	$scope.saving=true;
        	$http.post('/app/update', formData, {	
        	    transformRequest : angular.identity,
    			headers : {
    				'Content-Type' : undefined
    			}
    		}).success(function() {
    			$scope.saving=false;
    			$location.path("/view-user/" + userId);    			
    		}).error(function(data) {
    			$scope.saving=false; 
    		});
        };
    });
    
    cmpe281AwsApp.controller('ViewUserCtrl', function ($scope, $http, $routeParams) {
        
    	var userId = $routeParams.userId;    	        
    	$scope.currentPage = 1;
    	$scope.pageSize = 10;
    	
    	$scope.dataLoading = true;
        $http.get('/app/users/' + userId).then(function onSuccess(response) {
        	$scope.user = response.data;
        	$scope.dataLoading = false;
        }, function onError(response) {
        	$scope.user = response.statusText;
        	$scope.dataLoading = false;
        });
    });
    
    cmpe281AwsApp.controller('ViewAllUsersCtrl', function ($scope, $http) {
    	
    	var self = this;
    	$scope.users = []; 
    	$scope.searchText;
        
        $scope.dataLoading = true;
        $http.get('/app/users').then(function onSuccess(response) {
        	$scope.users = response.data;
        	$scope.dataLoading = false;
        }, function onError(response) {
        	$scope.users = response.statusText;
        	$scope.dataLoading = false;
        });        
        
        self.delete = function (userId) {
        	$scope.selectedUser = userId;
        	$scope.userDelete = true;
        	$http.delete('/app/users/' + userId).then(function onSuccess(response) {
            	$scope.users = _.without($scope.users, _.findWhere($scope.users, {id: userId}));
            	$scope.userDelete = false;
            }, function onError(){
            	
            });
        },
        
        $scope.searchFilter = function (obj) {
            var re = new RegExp($scope.searchText, 'i');
            return !$scope.searchText || re.test(obj.firstName) || re.test(obj.lastName.toString());
        };
    });
    
    cmpe281AwsApp.filter('formatDate', function() {
    	return function(input) {
    		return moment(input).format("MM/DD/YYYY HH:mm a");
    	};
    });
    
    cmpe281AwsApp.config(function ($routeProvider) {
        $routeProvider.when('/home', {templateUrl: 'pages/home.tpl.html'});
        $routeProvider.when('/create-user', {templateUrl: 'pages/createUser.tpl.html'});
        $routeProvider.when('/update-user/:userId', {templateUrl: 'pages/updateUser.tpl.html'});
        $routeProvider.when('/view-user/:userId', {templateUrl: 'pages/viewUser.tpl.html'});
        $routeProvider.when('/view-all-users', {templateUrl: 'pages/viewAllUsers.tpl.html'});
        $routeProvider.otherwise({redirectTo: '/home'});
    });
    
}());