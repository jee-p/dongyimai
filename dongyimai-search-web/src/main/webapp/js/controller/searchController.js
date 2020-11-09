app.controller('searchController',function ($scope,searchService,$location) {
    //加载从首页传递的参数
    $scope.loadKeywords = function(){
       $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
    $scope.searchMap={
        'keywords':"",
        'category':'',
        'brand':'',
        'spec':{},
        'price':'',
        'pageNo':1,
        'pageSize':20,
        'sortField':'',
        'sort':''

    };
    //搜索方法
    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            buildPageLable();
        })
    }
    //添加搜索项
    $scope.addSearch = function (key,value) {
        if (key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }
    //移除搜索项
    $scope.delSearch = function (key) {
        if (key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]='';
        }else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    buildPageLable = function () {
        //创建一个分页栏集合
        $scope.pageLable=[];
        //获取最后页码
        var maxPageNo=$scope.resultMap.totalPages;
        //开始页码
        var firstPageNo=1;
        //截至页码
        var lastPage=maxPageNo;
        //如果总页数大于5，显示部分页码
        $scope.firstDao=true;
        $scope.lastDao=true;
        if ($scope.resultMap.totalPages>5){
            //如果当前页小于等于3
            if ($scope.searchMap.pageNo<=3){
                lastPage=5;
                $scope.firstDao=false;
            }else if($scope.searchMap.pageNo>=lastPage-2){
                firstPageNo=maxPageNo-4;
                $scope.lastDao=false;
            }else {
                firstPageNo=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
            for (var i = firstPageNo;i<=lastPage;i++){
                $scope.pageLable.push(i);
            }
        }else {
            $scope.firstDao=false;
            $scope.lastDao=false;
        }
    }
    //根据页码查询
    $scope.selectByPage=function (pageNo) {
        //对传入页码进行验证
        if (pageNo<1||pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    //如果当前页为1
    $scope.isFirstPage=function () {
        if ($scope.searchMap.pageNo==1){
            return true;
        }else {
            return false;
        }
    }

    //判断当前页码是否为指定页码
    $scope.isCurrentPage=function (page) {
        if (parseInt(page)==parseInt($scope.searchMap.pageNo)){
            return true;
        }else {
            return false;
        }
    }
    //如果搜索关键字是品牌
    $scope.keywordsisBrand=function () {
        for (var i =0;i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }
    //设置排序规则
    $scope.sortSearch = function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }
})