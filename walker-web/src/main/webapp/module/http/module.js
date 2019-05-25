 

 
angular.module('com.http', [])
.config(['$urlRouterProvider', '$stateProvider',  function ($urlRouterProvider, $stateProvider) {
   
    var mName = 'http';

    //定义层级路由 url路径 参数 绑定controller
    $stateProvider
        .state('main.' + mName, {
            url: '/' + mName,
            templateUrl: 'module/' + mName + '/template/page.html',
            controller: 'com.' + mName + ''
        })
        .state('main.' + mName + '.simple', {
            url: '/list',
            templateUrl: 'common/template/list.html',
            controller: 'com.simple' 
        })


}]);



 