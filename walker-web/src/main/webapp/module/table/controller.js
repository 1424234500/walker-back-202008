 
angular.module('com.table')

.controller('com.table', ['$scope', '$rootScope', '$state', '$stateParams', 'tableService', function ($scope, $rootScope, $state, $stateParams, tableService) {
    //此处的成员变量 和 函数都能被子类ctrl使用和访问
	//若是对象类型 可修改对象成员					//可用于多 子路由共享数据 
	//若是基本类型或更改引用 则新内存隔离	//注意不会修改父类的数据
	var mName = 'table';
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
    $scope.table = 'STUDENT';
    //初始化路由页面跳转
    $scope.goHome = function(){
    	tableService.setTable($scope.table);
        $state.go(routeDir + '.list');
    };
    $scope.goAdd = function(){
        $state.go(routeDir + '.add');
    };
    $scope.goUpdate = function(id){
        var params = {"id":id};
        $state.go(routeDir + '.update', params);
    };

    
    $scope.page = {};
//    $scope.search = {}; //查询
    $scope.sort = {'orderCol': '', 'order': ''};
    $scope.changeOrder = function(type){
        $scope.sort.orderCol = type;
        if($scope.sort.order == ''){
            $scope.sort.order = '-';
        }else{
            $scope.sort.order = '';
        }
    };
    $scope.sql = 'select * from STUDENT';
    $scope.list = function(){ 
        var page = $scope.page;
        page["order"] = $scope.sort.orderCol ? $scope.sort.orderCol + ($scope.sort.order ? ' desc': '') : ''
        var params = $.extend({"sql":$scope.sql}, page);
        tableService.find(params).then(
            function (data) {
            	$scope.showCols = data.cols;
                $scope.httplist = data.list;
                $scope.page = data.page;
//                $scope.ppp = calcPage($scope.page);
                $scope.sums =  listSums($scope.httplist, $scope.showCols);
                $scope.err = data["info"];
        }, function(error){
        	$scope.err = error["info"];
        });   
    };
    
    
}])
.controller('com.table.addCtrl', ['$scope', '$rootScope', 'tableService', function ($scope, $rootScope, tableService) {
    $scope.httpget = {};
    $scope.add = function(){
        var params =  $scope.httpget;
        tableService.add(params).then(
            function (data) {
                info("操作数据:" + data + "条");
//                $scope.goHome();
        }, error); 
    };
}])
.controller('com.table.updateCtrl', ['$scope', '$rootScope', '$stateParams', 'tableService', function ($scope, $rootScope, $stateParams, tableService) {

    $scope.httpget = {};

    $scope.params= $stateParams;
    info("stateParams:" + JSON.stringify($scope.params));

    var params = {};
    params[$rootScope.cols[0]] = $scope.params.id;
    tableService.get(params).then(
        function (data) {
            $scope.httpget = data; 
    }, error);
 

    $scope.update = function(){
        var params = $scope.httpget;
        tableService.update(params).then(
            function (data) {
                info("操作数据:" + data + "条");
                $scope.goHome(); 
            }, error);  

    };
}])
.controller('com.table.listCtrl', ['$scope', '$rootScope', '$state', 'tableService', function ($scope, $rootScope, $state, tableService) { 

    //加载页面
    var loadPage = function(){
        $scope.page = {};
        $scope.search = {}; //查询
        $scope.orderType = $rootScope.cols[0];
        $scope.order = '-';
        $scope.changeOrder = function(type){
            $scope.orderType = type;
            if($scope.order === ''){
                $scope.order = '-';
            }else{
                $scope.order = '';
            }
        };
        $scope.list = function(){ 
            var page = $scope.page;
            var search = $scope.search;
            var params = $.extend({}, page, search);
            tableService.list(params).then(
                function (data) {
                    $scope.httplist = data.list;
                    $scope.page = data.page;
                    $scope.ppp = calcPage($scope.page);

                    $scope.sums =  listSums($scope.httplist, $rootScope.cols);

            }, error);   
        };
        $scope.list();
        $scope.keydown = function(event){
            if (event.keyCode == 13) {
                $scope.list();
            }   
        };
        $scope.delete = function(id){
            var params = {};
            params[$rootScope.cols[0]] = id;
            tableService.del(params).then(
                function (data) { 
                    info("操作数据:" + data + "条");
                    $scope.list(); 
            }, error);  

        };

    };
    //加载表信息
    tableService.cols().then(
        function (data) {
            $rootScope.cols = data;
            $scope.showCols = data;
            
            loadPage();
    }, error);


}])


