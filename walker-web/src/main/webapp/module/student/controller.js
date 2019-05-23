 
angular.module('com.student')

.controller('com.student', ['$scope', '$rootScope', '$state', '$stateParams', 'studentService', function ($scope, $rootScope, $state, $stateParams, studentService) {
    //父级路由ctrl 可被子路由ctrl使用 基类?	将公用函数 数据放在父类
	
	//初始化表 主键 列信息 
    studentService.cols().then(
        function (data) {
            $rootScope.cols = data;
            $scope.orderType = $rootScope.cols[0];
    }, error);

    //初始化分页查询 排序参数
    $scope.page = {};
    $scope.search = {}; //查询
    $scope.orderType = '';
    $scope.order = '-';
    $scope.changeOrder = function(type){
        $scope.orderType = type;
        if($scope.order === ''){
            $scope.order = '-';
        }else{
            $scope.order = '';
        }
    };
    
    //列表查询
    $scope.list = function(){ 
        var page = $scope.page;
        var search = $scope.search;
        var params = $.extend({}, page, search);
        studentService.list(params).then(
            function (data) {
                $scope.httplist = data.list;
                $scope.page = data.page;
                $scope.ppp = calcPage($scope.page);

                $scope.sums =  listSums($scope.httplist, $rootScope.cols);

        }, error);   
    };
    //回车事件触发
    $scope.keydown = function(event){
        if (event.keyCode == 13) {
            $scope.list();
        }   
    };
    $scope.delete = function(id){
        var params = {};
        params[$rootScope.cols[0]] = id;
        studentService.del(params).then(
            function (data) { 
                info("操作数据:" + data + "条");
                $scope.list(); 
        }, error);  
    };
    $state.go('main.student.list');

}])
.controller('com.student.addCtrl', ['$scope', '$rootScope', 'studentService', function ($scope, $rootScope, studentService) {
    $scope.httpget = {};

    $scope.add = function(){
        var params =  $scope.httpget;
        studentService.add(params).then(
            function (data) {
                info("操作数据:" + data + "条");
                $scope.list();
        }, error); 
    };
    
}])
.controller('com.student.updateCtrl', ['$scope', '$rootScope', '$stateParams', 'studentService', function ($scope, $rootScope, $stateParams, studentService) {

    $scope.httpget = {};
    $scope.params= $stateParams;
    info("stateParams:" + JSON.stringify($scope.params));

    var params = {};
    params[$rootScope.cols[0]] = $scope.params.id;
    studentService.get(params).then(
        function (data) {
            $scope.httpget = data; 
    }, error);

    $scope.update = function(){
        var params = $scope.httpget;
        studentService.update(params).then(
            function (data) {
                info("操作数据:" + data + "条");
                $scope.list(); 
            }, error);  

    };
}])
.controller('com.student.listCtrl', ['$scope', '$rootScope', '$state', 'studentService', function ($scope, $rootScope, $state, studentService) { 

    //默认查询第一页
    $scope.list();

}])


