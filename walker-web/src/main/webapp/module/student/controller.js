 
angular.module('com.student')

.controller('com.student', ['$scope', '$rootScope', '$state', '$stateParams', 'studentService', function ($scope, $rootScope, $state, $stateParams, studentService) {
	//此处的成员变量 和 函数都能被子类ctrl使用和访问
	//若是对象类型 可修改对象成员					//可用于多 子路由共享数据 
	//若是基本类型或更改引用 则新内存隔离	//注意不会修改父类的数据
	var mName = 'STUDENT';
    var routeDir = 'main.' + mName;		//	main.simple
	$scope.mName = mName;
	
	 //初始化分页
    $scope.page = {};
    //初始化排序
    $scope.sort = {'orderCol': '', 'order': ''};
    $scope.changeOrder = function(type){
        $scope.sort.orderCol = type;
        if($scope.sort.order == ''){
            $scope.sort.order = '-';
        }else{
            $scope.sort.order = '';
        }
    };
   //初始化查询
    $scope.search = {}; //查询
    $scope.clear = function(){
    	for(var key in $scope.search){
    		delete $scope.search[key];
		}
    }
    //回车事件触发
    $scope.keydown = function(event){
        if (event.keyCode == 13) {
            $scope.list();
        }   
    };
    
    //初始化路由页面跳转
    $scope.goHome = function(){
        $state.go(routeDir + '.list');
    };
    $scope.goAdd = function(){
        $state.go(routeDir + '.add');
    };
    $scope.goUpdate = function(id){
        var params = {"id":id};
        $state.go(routeDir + '.update', params);
    };
    
	//初始化表 主键 列信息 
    studentService.cols().then(
        function (data) {
            $rootScope.cols = data;
            $rootScope.showCols = data.slice(1);
            $scope.orderCol = $rootScope.cols[0];
            //page拿到表信息后自动跳转展示list页面
            $scope.goHome();
    }, error);
 
    //列表查询 
    $scope.list = function(){ 
        var page = $scope.page;
        page["ORDER"] = $scope.sort.orderCol ? $scope.sort.orderCol + ($scope.sort.order ? ' DESC': '') : ''
        var search = $scope.search;
        var params = $.extend({}, page, search);
        studentService.list(params).then(
            function (data) {
                $scope.httplist = data.list;
                $scope.page = data.page;
                $scope.ppp = calcPage($scope.page);
                $scope.sums =  listSums($scope.httplist, $rootScope.showCols);
        }, error);   
    };
    $scope.delete = function(id){
        var params = {};
        params[$rootScope.cols[0]] = id;
        studentService.del(params).then(
            function (data) { 
                $scope.list(); 
        }, error);  
    };

    
}])
.controller('com.student.addCtrl', ['$scope', '$rootScope', 'studentService', function ($scope, $rootScope, studentService) {
    $scope.httpget = {};
    $scope.add = function(){
        var params =  $scope.httpget;
        studentService.add(params).then(
            function (data) {
                info("操作数据:" + data + "条");
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
                $scope.goHome();
        }, error);  
        
    };
}])
.controller('com.student.listCtrl', ['$scope', '$rootScope', '$state', 'studentService', function ($scope, $rootScope, $state, studentService) { 

    //默认查询第一页
    $scope.list();

}])


