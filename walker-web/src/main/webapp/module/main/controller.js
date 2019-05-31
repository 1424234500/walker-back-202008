 
angular.module('com.main') 

.controller('com.main.pageCtrl', ['$scope', '$rootScope', '$state', function ($scope, $rootScope, $state) {
    //嵌套路由 scope可访问 <任意module> 的上层html的 ctrl/scope
    
    $scope.goHome = function(){ 
        $state.go('main.home');
    }

    var logined = $rootScope.isLogined ;
    if (logined == true) { // 已经登录 
       // $state.go('main'); 
    } else { // 没有登录   
       $state.go('login'); 
    }  

    info($state.current.name);


    $scope.itemList = [];
    $scope.itemList.push({"route":"main.http", "name":"Http-Rest"});
    $scope.itemList.push({"route":"main.tomcat", "name":"Tomcat"});
    $scope.itemList.push({"route":"main.socket", "name":"Socket"});
    $scope.itemList.push({"route":"main.cache", "name":"Cache"});
    $scope.itemList.push({"route":"main.file.list", "name":"File"});
    $scope.itemList.push({"route":"main.class", "name":"Class"});

    $scope.itemList.push({"route":"main.table", "name":"Table"});
    $scope.itemList.push({"route":"main.student", "name":"Student"});
    $scope.itemList.push({"route":"main.simple", "name":"Simple"});
    $scope.itemList.push({"route":"main.system", "name":"RaspberryPi"});

    $scope.goHome();
}]) 
.controller('com.main.homeCtrl', ['$scope', '$rootScope', '$state', 'studentService', function ($scope, $rootScope, $state, studentService) {
 






}]) 