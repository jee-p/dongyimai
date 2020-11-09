app.controller('itemController',function ($scope) {
    $scope.addNum = function (num) {
        $scope.num=$scope.num + num;
        if ($scope.num < 1){
            $scope.num=1;
        }
    }

    $scope.specificationItems={};
    //用户要选择规格
    $scope.selectSpec = function (name,value) {
        $scope.specificationItems[name] = value;
        searchSku();
    }
    //判断用户是否选中当前规格选项
    $scope.isSelected = function (name,value) {
        if ($scope.specificationItems[name] == value){
            return true;
        }else {
            return false;
        }
    }
    //加载默认的sku信息
    $scope.loadSku=function () {
        $scope.sku=skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec))
    }
    //用于匹配两个对象的方法
    matchObject = function (map1,map2) {
        for (var k in map1){
            if (map1[k]!=map2[k]){
                return false;
            }
        }
        for (var k in map2){
            if (map2[k]!=map1[k]){
                return false;
            }
        }
        return true;
    }
    //查询sku
    searchSku = function () {
        for (var i = 0;i<skuList.length;i++){
            if (matchObject(skuList[i].spec,$scope.specificationItems)){
                $scope.sku=skuList[i];
                return;
            }
        }
        $scope.sku={
            id:0,
            title:'-------------------------',
            price:0
        }
    }

    //添加商品到购物车
    $scope.addToCart = function () {
        alert('skuid:'+$scope.sku.id);
    }
})