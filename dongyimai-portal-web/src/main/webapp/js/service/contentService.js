app.service('contentService',function ($http) {
    this.selectByCategoryId=function (categoryId) {
        return $http.get('../content/selectByCategoryId.do?categoryId='+categoryId);
    }
})