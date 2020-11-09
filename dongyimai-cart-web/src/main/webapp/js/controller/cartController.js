app.controller('cartController',function ($scope,cartService) {
    //查询购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            $scope.totalValue=cartService.sum($scope.cartList);
        })
    }
    //添加商品到购物车
    $scope.addToCart=function (itemId,num) {
        cartService.addToCart(itemId,num).success(function (response) {
            if (response.success){
                $scope.findCartList();
            }else {
                alert(response.message);
            }
        })
    }

    //获取地址列表
    $scope.findAddress = function () {
        cartService.findAddress().success(function (response) {
            $scope.addressList=response;
            //设置默认地址
            for (var i =0;i<$scope.addressList.length;i++){
                if ($scope.addressList[i].isDefault=='1'){
                    $scope.address=$scope.addressList[i];
                    break;
                }
            }
        })
    }

    //选择地址
    $scope.selectAddress = function (address) {
        $scope.address=address;
    }
    //判断是否为当前选中的地址
    $scope.isSelectedAddress = function (address) {
        if (address==$scope.address){
            return true;
        }else {
            return false;
        }
    }

    //支付对象
    $scope.order = {
        paymentType:'1'
    }
    //选择支付方式
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }

    $scope.submitOrder=function () {
        //设置地址，手机号，收件人
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;
        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success){
                //如果是扫码支付跳转到支付页面
                if ($scope.order.paymentType=='1'){
                    location.href="pay.html";
                }else {
                    location.href="paySuccess.html";
                }
            }else {
                alert(response.message);
            }
        })
    }
})