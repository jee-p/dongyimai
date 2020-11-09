var app = angular.module('dongyimai',[]);
//编写一个angularJS的过滤器
app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);