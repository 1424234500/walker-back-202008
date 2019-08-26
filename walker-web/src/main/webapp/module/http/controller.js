 
angular.module('com.http')

.controller('com.http', ['$PROJECT','$scope', '$rootScope', '$state', 'baseService','tools', function ($PROJECT, $scope, $rootScope, $state, baseService, tools) {
    //嵌套路由 scope可访问 <任意module> 的上层html的 ctrl/scope
    var mName = 'http-rest';
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

    $scope.cols = ["ID", "NAME", "TIME"]; //搜索<添加/修改>列
    $scope.page = {"nowpage":1, "shownum":50, "order":"","desc":""}; //分页参数
    //查询列表
    $scope.get = function(){
        var url = $scope.urlGet;
        $scope.args = "";
        baseService.get(url, {}).then(
            function (data) {
                $scope.res = JSON.stringify(data);
            }, error);
    };
    $scope.post = function(){
        var url = $scope.urlPost;
        var params = $scope.search;
        baseService.post(url, params).then(
            function (data) {
                $scope.res = JSON.stringify(data);
            }, error);
    };
    $scope.put = function(){
        var url = $scope.urlPut;
        var params = $scope.search;
        baseService.put(url, params).then(
            function (data) {
                $scope.res = JSON.stringify(data);
            }, error);
    };
    $scope.delete = function(){
        var url = $scope.urlDelete;
        $scope.args = "";
        baseService.delete(url, {}).then(
            function (data) {
                $scope.res = JSON.stringify(data);
            }, error);
    };
    $scope.make = function(){
        $scope.argsStr = JSON.stringify($scope.search);
    };
    $scope.urlGet = '/' + $PROJECT + '/restful/2203/make.do';
    $scope.urlPost = '/' + $PROJECT + '/restful/make.do';
    $scope.urlPut = '/' + $PROJECT + '/restful/make.do';
    $scope.urlDelete = '/' + $PROJECT + '/restful/2204/make.do';


//    $state.go("main.http.simple");

}])


