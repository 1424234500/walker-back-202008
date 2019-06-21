angular.module('com.simple', [])
.config(['$urlRouterProvider', '$stateProvider',  function ($urlRouterProvider, $stateProvider) {
    var mName = 'simple';
//    var tempDir = 'module/' + mName;	//	module/simple
    var tempDir = 'common';						//	common
    var ctrlDir = 'com.' + mName;				//	com.simple
    var routeDir = 'main.' + mName;		//	main.simple
    
    //定义层级路由 url路径 参数 绑定controller
    $stateProvider
        .state(routeDir, {
            url: '/' + mName,
            templateUrl: tempDir + '/template/page.html',
            controller: ctrlDir
        })
        .state(routeDir + '.list', {
            url: '/list',
            templateUrl: tempDir + '/template/list.html',
            controller: ctrlDir + '.listCtrl' 
        })
        .state(routeDir + '.add', { 
            url: '/add',
            templateUrl: tempDir + '/template/add.html',
            controller: ctrlDir + '.addCtrl' 
        })
        .state(routeDir + '.update', {
            url: '/update:id',
            templateUrl: tempDir + '/template/update.html',
            controller: ctrlDir + '.updateCtrl' 
        })  
    ; 

}])
.controller('com.simple', ['$PROJECT','$scope', '$rootScope', '$state', 'baseService', function ($PROJECT, $scope, $rootScope, $state, baseService) {
	//此处的成员变量 和 函数都能被子类ctrl使用和访问
	//若是对象类型 可修改对象成员					//可用于多 子路由共享数据 
	//若是基本类型或更改引用 则新内存隔离	//注意不会修改父类的数据
	var mName = 'simple';
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


	$scope.make = function(params){
	        if( ! params){ 
	            params = {};
	        }
	        params['TABLE_NAME'] = 'STUDENT';
	    return params;
	};
	//初始化表 主键 列信息 
	baseService.post(	'/' + $PROJECT + '/table/cols.do', $scope.make()	).then(
        function (data) {
            $rootScope.cols = data;
            $rootScope.showCols = data;//data.slice(1);
            $scope.sort.orderCol = $rootScope.cols[0];

            //page拿到表信息后自动跳转展示list页面
            $scope.goHome();
    }, error);
  
    //列表查询 
    $scope.list = function(){ 
        var page = $scope.page;
        page["ORDER"] = $scope.sort.orderCol ? $scope.sort.orderCol + ($scope.sort.order ? ' DESC': '') : ''
        var search = $scope.search;
        var params = $.extend({}, page, search);
    	baseService.post(	'/' + $PROJECT + '/table/list.do', $scope.make(params)	).then(
            function (data) {
                $scope.httplist = data.list;
                $scope.page = data.page;
                $scope.ppp = calcPage($scope.page);
                $scope.sums =  listSums($scope.httplist, $rootScope.showCols);
        }, error);   
    };
    
    //删除
    $scope.delete = function(id){
        var params = {};
        params[$rootScope.cols[0]] = id;
    	baseService.post(	'/' + $PROJECT + '/table/delete.do', $scope.make(params)	).then(
            function (data) { 
//                $scope.list(); 
        }, error);  
    };

    
}])
.controller('com.simple.addCtrl', ['$PROJECT','$scope', '$rootScope', '$state', 'baseService', function ($PROJECT, $scope, $rootScope, $state, baseService) {
    $scope.httpget = {};
    $scope.add = function(){
        var params =  $scope.httpget;
    	baseService.post(	'/' + $PROJECT + '/table/add.do', $scope.make(params)	).then(
            function (data) {
                info("操作数据:" + data + "条");
        }, error); 
    };
    
}])
.controller('com.simple.updateCtrl', ['$PROJECT','$scope', '$rootScope', '$stateParams', 'baseService', function ($PROJECT,$scope, $rootScope, $stateParams, baseService) {
    $scope.httpget = {};
    $scope.params= $stateParams;
    info("stateParams:" + JSON.stringify($scope.params));

    var params = {};
    params[$rootScope.cols[0]] = $scope.params.id;
	baseService.post(	'/' + $PROJECT + '/table/get.do', $scope.make(params)	).then(
        function (data) {
            $scope.httpget = data; 
    }, error);

    $scope.update = function(){
        var params = $scope.httpget;
    	baseService.post(	'/' + $PROJECT + '/table/update.do', $scope.make(params)	).then(
            function (data) {
                info("操作数据:" + data + "条");
                $scope.goHome();
        }, error);  
        
    };
}])
.controller('com.simple.listCtrl', ['$PROJECT','$scope', '$rootScope', '$state', 'baseService', function ($PROJECT, $scope, $rootScope, $state, baseService) {
    //默认查询第一页
    $scope.list();

}])


