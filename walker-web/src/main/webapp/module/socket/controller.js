angular.module('com.socket', [])
.config(['$urlRouterProvider', '$stateProvider',  function ($urlRouterProvider, $stateProvider) {
    var mName = 'socket';
    var tempDir = 'module/' + mName;	//	module/simple
//    var tempDir = 'common';						//	common
    var ctrlDir = 'com.' + mName;				//	com.simple
    var routeDir = 'main.' + mName;		//	main.simple
    
    //定义层级路由 url路径 参数 绑定controller
    $stateProvider
        .state(routeDir, {
            url: '/' + mName,
            templateUrl: tempDir + '/template/page.html',
            controller: ctrlDir
        })

    ; 

}])
.controller('com.socket', ['$PROJECT','$scope', '$rootScope', '$state', 'baseService', function ($PROJECT, $scope, $rootScope, $state, baseService) {
	//此处的成员变量 和 函数都能被子类ctrl使用和访问
	//若是对象类型 可修改对象成员					//可用于多 子路由共享数据 
	//若是基本类型或更改引用 则新内存隔离	//注意不会修改父类的数据
	var mName = 'socket';
    var routeDir = 'main.' + mName;		//	main.simple
	$scope.mName = mName;
	 
	
	$scope.showCols = ['FROM', 'TO'];	//URL
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
            $scope.statis();
        }   
    };


    $scope.statis = function(){ 
        //debugger; 
        var params = $scope.search;  
    	baseService.post(	'/' + $PROJECT + '/redis/statics.do', params	).then(
        function (data) {  
            info(data);
            //debugger; 
            $scope.option = data.option;
            if($scope.items == null){ 
                data.option.xAxis.data.push("");
                $scope.items =  data.option.xAxis.data;
            }
            $scope.search = data.arg;

            toolSetChart("echarts", data.option);
        }, error);  


    };
    $scope.statis(); 
    $scope.itemSelect = function(){
        $scope.statis();   
    };  

    
}])



