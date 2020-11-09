app.controller('baseController',function ($scope) {
    $scope.reloadList = function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)

    }
    //对分页进行配置
    $scope.paginationConf = {
        currentPage:1,
        totalItems:10,
        itemsPerPage:10,
        perPageOptions:[5,10,15,20],
        onChange:function () {
            $scope.reloadList();

        }

    }
    //定义一个数组存放选中的id
    $scope.selectIds = [];
    $scope.updateSelection = function ($event,id) {
        //判断当前复选框是否被选中1
        if($event.target.checked){
            $scope.selectIds.push(id)
        }else {
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    }
    //提取JSON中某个属性 返回字符串 用逗号拼接
    $scope.jsonToString = function (jsonString,key) {
        var jsonObject = JSON.parse(jsonString);
        var value = "";
        for(var i = 0;i < jsonObject.length;i++){
            if(i>0){
                value += ",";
            }
            value += jsonObject[i][key];
        }
        console.log(value);
        return value;
    }
})