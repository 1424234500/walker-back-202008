 
angular.module('com.file')

.controller('com.file', ['$scope', '$rootScope', '$state', 'fileService','$PROJECT','baseService', function ($scope, $rootScope, $state, fileService,$PROJECT,baseService) {
    //嵌套路由 scope可访问 <任意module> 的上层html的 ctrl/scope
    var mName = 'file';
    $scope.mName = mName;

    $scope.goHome = function(){
        $state.go('main.file.list');
    }
    $scope.goAdd = function(){
        $state.go('main.file.add');
    }
    $scope.goUpdate = function(id){
        var params = {"id":id};
        $state.go('main.file.update', params);
    };


    $scope.cols = ["NAME", "SIZE"]; //搜索<添加/修改>列
    $scope.showCols = ["ID","NAME", "UPUSERID", "FILESIZE", "TYPE", "UPTIME", "CHANGETIME", "ABOUT"]; //展示列
    $scope.page = {"nowpage":1, "shownum":50, "order":"","desc":""}; //分页参数

    $scope.search = {}; //查询
    $scope.search={};
    $scope.search['NAME'] = "";
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

    $scope.dir = "";
    $scope.back = function(){
        var path = $scope.dir;
        var ps = path.split('\\');
        console.log(ps);
        if(ps.length > 0){
            path = path.substr(0, path.length - ps[ps.length - 1].length - 1)
        }
        if(!path){
        	path = $scope.defaultDir;
        }
        
        $scope.dir = path;
        $scope.name = "";
        $scope.list();
    };
    $scope.detail = function(item){
        if(item.TYPE != 'dir'){
            var url = '/' + $PROJECT + "/file/download.do?path=" + item.PATH;
            openUrl(url);
            return;
        }
        $scope.dir = item.PATH;
        $scope.name = "";
        $scope.list();
    };
    //清空查询条件
    $scope.clear = function(){
        $scope.search = {"URL":$scope.search["URL"]};//保留字段
    };
    $scope.list = function(ifNewDir){
        var params = {};
        params["dir"] = $scope.dir;
        if($scope.search.NAME)
            params["name"] = $scope.search.NAME;
        if(ifNewDir){
            params["newdir"] = true;
            $scope.search.NAME = "";
        }
        fileService.fileDir(params).then(
            function (data) {
                $scope.httplist = data;
            }, error);
    };
    $scope.list();
    $scope.delete = function(item, event){
        event.stopPropagation(); //阻止向上传递
        event.preventDefault();

        var params = {};
        params["PATH"] = item.PATH;
        fileService.del(params).then(
            function (data) {
                info("操作数据:" + data + "条");
                $scope.list();
            }, error);
    };

    //更新一行数据
    $scope.update = function(item, event){
        event.stopPropagation(); //阻止向上传递
        event.preventDefault();

        var params = $scope.search;
        params["OLDNAME"] = item["NAME"];
        params["OLDPATH"] = item["PATH"];
        params["PATH"] = $scope.dir;
        var url = '/' + $PROJECT + '/file/update.do';
        baseService.post(url, params).then(
            function (data) {
                $scope.clear();
                $scope.list();
            }, error);
    };

    //加载表信息
    fileService.fileCols().then(
        function (data) {
            $scope.colss = data;
        }, error);
    //加载表信息
    fileService.fileDirUpload().then(
        function (data) {
            $scope.defaultDir = data;
            $scope.dir = data;
        }, error);

}])
.controller('com.file.addCtrl', ['$scope', '$rootScope', 'fileService', function ($scope, $rootScope, fileService) {


    $('#time').datetimepicker();
    $scope.ajaxSubmit = function(){
        var params =  $scope.httpget;
        fileService.update(params).then(
            function (data) {
                info("操作数据:" + data + "条");
                $scope.goHome();
        }, error);
    };
}])
.controller('com.file.updateCtrl', ['$scope', '$rootScope', '$stateParams', 'fileService', function ($scope, $rootScope, $stateParams, fileService) {
    $scope.params = $stateParams;
    info("stateParams");
    info($scope.params);
    var params = $scope.params;
    fileService.get(params).then(
        function (data) {
            $scope.httpget = data;
        }, error);


    $('#time').datetimepicker();
    $scope.ajaxSubmit = function(){
        var params = $scope.httpget;
        fileService.update(params).then(
            function (data) {
                info("操作数据:" + data + "条");
                $scope.goHome();
            }, error);

    };
}])
.controller('com.file.listCtrl', ['$scope', '$rootScope', '$state', 'fileService', function ($scope, $rootScope, $state, fileService) {


    //bootstrap日期插件使用方式
    $('#timefrom').datetimepicker();
    $('#timeto').datetimepicker();
    $scope.list();
 
}])


