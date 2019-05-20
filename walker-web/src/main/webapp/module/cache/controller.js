 
angular.module('com.cache')

.controller('com.cache.pageCtrl', ['$PROJECT','$scope', '$rootScope', '$state', 'baseService','tools', function ($PROJECT, $scope, $rootScope, $state, baseService, tools) {
    //嵌套路由 scope可访问 <任意module> 的上层html的 ctrl/scope
    var mName = 'cache';
    $scope.mName = mName;
    $scope.search = {}; //查询
    $scope.orderType = 'TYPE'; //排序
    $scope.order = '-';
    $scope.changeOrder = function(type){
        $scope.orderType = type;
        if($scope.order === ''){
            $scope.order = '-';
        }else{
            $scope.order = '';
        }
    };
    $scope.keydown = function(event){ //回车搜索
        if (event.keyCode == 13) {
            $scope.list();
        }
    };

    $scope.cols = ["URL", "KEY", "VALUE", "EXPIRE", "TYPE"]; //搜索<添加/修改>列
    $scope.showCols = ["KEY", "VALUE", "HASHCODE", "EXPIRE", "COUNT", "TYPE"]; //展示列
    $scope.page = {"NOWPAGE":1, "SHOWNUM":50, "ORDER":"","DESC":""}; //分页参数

    //long NUM = 0;	//总数据条数
    //int SHOWNUM = defaultEachPageNum;//每页数量
    //int NOWPAGE = 1;	//当前页码
    //int PAGENUM = 0;	//总页数
    //String ORDER;	//排序
    //String DESC;	//倒序
    //查询列表
    $scope.list = function(){
        var PAGE = $scope.page;
        var search = $scope.search;
        var params = $.extend({}, PAGE, search);
        var url = '/' + $PROJECT + '/tomcat/listCacheMap.do';
        baseService.post(url, params).then(
            function (data) {
                $scope.listCache = data.list;
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
                $scope.clear();
                $scope.list();
            }, error);
    };
    //清空查询条件
    $scope.clear = function(){
        $scope.search = {};//保留字段
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
                $scope.clear();
                $scope.list();
            }, error);
    };


    $scope.list();

}])


