
// 自定义服务


angular.module('com.student')
.factory('studentService',['$PROJECT','baseService','cacheService', function($PROJECT,baseService,cacheService){

    //管理表的服务名
    var mName = 'student';

    var service = {};
    //操作表名
    var tableName = mName;

    service.setTable = function(name){
        tableName = name;
    };
    service.getTable = function(){
        return tableName;   
    };

    service.make = function(params){
        if( ! params){ 
            params = {};
        }
        params['TABLE_NAME'] = tableName;
        return params;
    }

    //获取表字段列表
    service.cols = function(params){ 
        params = service.make(params);
        return cacheService.post('/' + $PROJECT + '/' + mName + '/student_cols.do', params);
    };   

    service.list = function(params){ 
        params = service.make(params);
        return baseService.post('/' + $PROJECT + '/' + mName + '/student_list.do', params);
    };   
    service.get = function(params){ 
        params = service.make(params);
        return baseService.post('/' + $PROJECT + '/' + mName + '/student_get.do', params);
    }; 
    service.del = function(params){ 
        params = service.make(params);
        return baseService.post('/' + $PROJECT + '/' + mName + '/student_delete.do', params);
    };
    service.update = function(params){ 
        params = service.make(params);
        return baseService.post('/' + $PROJECT + '/' + mName + '/student_update.do', params);
    };
    service.add = function(params){
        params = service.make(params);
        return baseService.post('/' + $PROJECT + '/' + mName + '/student_add.do', params);
    };
    service.statis  = function(params){
        params = service.make(params);
        return baseService.post('/' + $PROJECT + '/' + mName + '/student_statis.do', params);
    }; 
    
    service.do = function(url, params){ 
        return baseService.post(url, params);
    }; 

    return service;
    
}]);

