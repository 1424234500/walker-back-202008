 
angular.module('com.cache')

.controller('com.cache', ['$PROJECT','$scope', '$rootScope', '$state', 'baseService','tools', function ($PROJECT, $scope, $rootScope, $state, baseService, tools) {
	//此处的成员变量 和 函数都能被子类ctrl使用和访问
	//若是对象类型 可修改对象成员					//可用于多 子路由共享数据 
	//若是基本类型或更改引用 则新内存隔离	//注意不会修改父类的数据
	var mName = 'cache';
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
	
    $scope.cols = ["URL", "KEY", "VALUE", "EXPIRE", "TYPE"]; //搜索<添加/修改>列
    $scope.showCols = ["KEY", "VALUE", "EXPIRE", "COUNT"]; //展示列
    $scope.page = {"nowpage":1, "shownum":50, "order":"","desc":""}; //分页参数

    //查询列表
    $scope.list = function(){
        var page = $scope.page;
        var search = $scope.search;
        var params = $.extend({}, page, search);
        var url = '/' + $PROJECT + '/tomcat/listCacheMap.do';
        baseService.post(url, params).then(
            function (data) {
                $scope.httplist = data.list;
                $scope.search.URL = data.urls;
                $scope.oftype = data.oftype;
                $scope.page = data.page;
                $scope.pages = calcPage($scope.page); //计算序列
            }, error);
    };
    //添加一行数据
    $scope.add = function(item){
        var params = $scope.search;
        var url = '/' + $PROJECT + '/tomcat/addCacheMap.do';
        baseService.post(url, params).then(
            function (data) {
//                $scope.clear();
//                $scope.list();
            }, error);
    };
    //上级目录
    $scope.back = function(){
        var lastUrl = $scope.search["URL"] || "";
        if(lastUrl == "")
            return;
        var res = "";
        //list[03] -> list, map.list -> map
        var urls = lastUrl.split('.'); //map list[03] map
        var top = urls[urls.length - 1]; //
        var tops = top.split('[');
        if(tops.length > 1){//list 03]
            urls[urls.length - 1] = tops[0];
        }else{ //map  map.map
            urls.splice(urls.length - 1);
        }
        $scope.search["URL"] = urls.join('.');
        $scope.list();
    };
    $scope.oftype = 0;
    //点中一行数据
    $scope.detail = function(item){
        if(item["TYPE"] == "0") return; //基本元素无子集 不可点击

        var lastUrl = $scope.search["URL"] || "";
        if($scope.oftype == "2" || lastUrl == ""){ //list -> [01]
            $scope.search["URL"] = lastUrl + "" + item["KEY"];
        }else{ //map
            $scope.search["URL"] = lastUrl + "." + item["KEY"];
        }
        $scope.list();
    };
    //删除一行数据
    $scope.delete = function(item, event){
        event.stopPropagation(); //阻止向上传递
        event.preventDefault();

        var params = {};
        params["URL"] = $scope.search["URL"] || "";
        params["KEY"] = item["KEY"]
        //del map.list[02].key1 map.list[02]
        var url = '/' + $PROJECT + '/tomcat/deleteCacheMap.do';
        baseService.post(url, params).then(
            function (data) {
                if($scope.oftype == "2"){//删除list中元素需要刷新key
                    $scope.list();
                }else{
                    for(var i = 0; i < $scope.listCache.length; i++){
                        var obj = $scope.listCache[i];
                        if(obj["KEY"] == item["KEY"]){
                            $scope.listCache.splice(i,i+1);
                        }
                    }
                }
            });

    };
    //更新一行数据
    $scope.update = function(item, event){
        event.stopPropagation(); //阻止向上传递
        event.preventDefault();

        var params = $scope.search;
        params["KEY"] = item["KEY"];
        var url = '/' + $PROJECT + '/tomcat/addCacheMap.do';
        baseService.post(url, params).then(
            function (data) {
//                $scope.clear();
                $scope.list();
            }, error);
    };


    $scope.list();

}])


