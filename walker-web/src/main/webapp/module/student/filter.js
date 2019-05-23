
//diy过滤器  结合 ng-repeat实现本页面静态查询
angular.module('com.student')
.filter('filterQuery', function() { 
    return function (collection, params) {
        var res = [];
        angular.forEach(collection, function (item) {
            res.push(item);
        });
        return res;
    }
});
