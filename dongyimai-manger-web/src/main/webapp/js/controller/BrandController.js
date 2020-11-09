app.controller('brandController',function ($scope,$controller,brandService) {
    //控制器继承
    $controller('baseController',{$scope:$scope});
    //查询所有数据
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        })
    };


    $scope.findPage = function (pageNum,pageSize) {
        brandService.findPage(pageNum,pageSize).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    }
    //保存数据
    $scope.save = function () {
        if($scope.entity.id != null){
            brandService.update($scope.entity).success(function (response) {
                if(response.success){
                    alert(response.message);
                    $scope.reloadList();
                }else {
                    alert(response.message);
                }
            })
        }else{
            brandService.add($scope.entity).success(function (response) {
                if(response.success){
                    alert(response.message);
                    $scope.reloadList();
                }else {
                    alert(response.message);
                }
            })

        }
    }

    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity=response;
        });
    }



    //批量删除
    $scope.delete = function () {
        brandService.delete($scope.selectIds).success(function (response) {
            if(response.success){
                alert(response.message);
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        })
    }
    $scope.searchEntity={};
    $scope.search=function (pageNum,pageSize) {
        brandService.search($scope.searchEntity,pageNum,pageSize).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    }
});