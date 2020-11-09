app.controller('contentController',function ($scope,contentService) {
    //创建一个广告集合
    $scope.contentList=[];
    $scope.selectByCategoryId=function (categoryId) {
        contentService.selectByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId]=response;
        })
    }
    //搜索跳转页面
    $scope.search = function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
})