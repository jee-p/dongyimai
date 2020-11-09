 //用户表控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	//用户注册
	$scope.register = function () {
		//判断用户两次输入的密码是否是一致的
		if ($scope.entity.password!=$scope.password){
			alert("两次输入的密码不一致，请重新输入")
			return;
		}
		userService.add($scope.entity,$scope.code).success(function (response) {
			alert(response.message);
		})
	}
    //发送验证码
	$scope.sendCode=function () {
		if ($scope.entity.phone==null){
			alert("请输入手机号")
			return;
		}
		userService.sendCode($scope.entity.phone).success(function (response) {
			alert(response.message);

		})
	}
});	