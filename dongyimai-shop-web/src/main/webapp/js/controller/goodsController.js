 //InnoDB free: 5120 kB控制层 
app.controller('goodsController' ,function($scope,$controller ,$location,typeTemplateService,itemCatService,goodsService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id = $location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html($scope.entity.goodsDesc.introduction);
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//sku列表转换
				for(var i = 0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	};
	//根据规格选项名称和规格名称返回当前选项是否被勾选
	$scope.checkAttributeValue=function(specName,optionName){
		var items = $scope.entity.goodsDesc.specificationItems;
		var object=$scope.searchObjectByKey(items,'attributeName',specName);
		if (object==null){
			return false;
		}else {
			if (object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else {
				return false;
			}
		}
	}
	
	//保存 
	$scope.save=function(){
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					location.href="goods.html"
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.add=function () {
		$scope.entity.goodsDesc.introduction = editor.html();
		goodsService.add($scope.entity).success(function (response) {
			if(response.success){
				alert(response.message);
				editor.html("");
				$scope.entity={};

			}else {
				alert(response.message);
			}
		})
	}
	//上传图片
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(function (response) {
			if (response.success){
				$scope.imageEntity.url=response.message;
			}else {
				alert(response.message);
			}
		}).error(function () {
			alert("上传出错");
		})
	}

	//定义复合实体
	$scope.entity={
		goods:{
			isEnableSpec:'0'
		},
		goodsDesc:{
			itemImages:[],
			specificationItems:[]
		}
	}

	$scope.addImages = function () {
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
	}
	$scope.delImages = function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	//获取一级分类
	$scope.selectItemCatList = function () {
		itemCatService.selectByParentId2(0).success(function (response) {
			$scope.itemCat1List = response;
		})
	}
	//获取二级分类
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
		itemCatService.selectByParentId2(newValue).success(function (response) {
			$scope.itemCat2List = response;
		})
	})
	//获取三级分类
	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
		itemCatService.selectByParentId2(newValue).success(function (response) {
			$scope.itemCat3List = response;
		})
	})
	//获取模板ID
	$scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
		itemCatService.findOne(newValue).success(function (response) {
			$scope.entity.goods.typeTemplateId = response.typeId;
		})
	})

	//获取品牌数据,确定模板Id
	$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
		if(newValue){
			typeTemplateService.findOne(newValue).success(function (response) {
				$scope.typeTemplate = response;
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
				if ($location.search()['id']==null) {
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems)
				}
			});
			//查询规格列表
			typeTemplateService.selectSpecList(newValue).success(function (response) {
				$scope.specList = response;
			})
		}
	})

	//用户选中规格
	$scope.updateSpec = function ($event,name,value) {
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name)
		if (object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else {
				//如果用户取消勾选当前选项
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				//如果全部取消
				if (object.attributeValue.length == 0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.valueOf(object,1));
				}
			}
		}else {
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}

	}

	//创建SKU列表
	$scope.creatSkuList = function () {
		//定义一个不带规格的实体对象
		$scope.entity.itemList=[
			{
				spec:{},
				price:0,
				num:999,
				status:'0',
				isDefault:'0'
			}
		];
		var items = $scope.entity.goodsDesc.specificationItems;
		for(var i = 0;i<items.length;i++){
			$scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}
	//添加列
	addColumn = function (list,columnName,columnValues) {
		//创建一个新的集合，用于存储用户选中的内容
		var newList = [];
		for(var i = 0;i<list.length;i++){
			var oldRow = list[i];
			for(var j = 0;j<columnValues.length;j++){
				//深克隆
				var newRow=JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}

	$scope.itemCatList=[];
	$scope.findItemCat=function () {
		itemCatService.findAll().success(function (response) {
			for(var i = 0;i<response.length;i++){
				$scope.itemCatList[response[i].id] = response[i].name;
			}
		})
	}
	$scope.state = ['未申请','审核通过','申请中','驳回'];
	$scope.deleMarket=function (id,isMarketable) {
		goodsService.deleMarket(id,isMarketable).success(function (response) {
			if (response.success){
				alert(response.message);
				$scope.reloadList();
			}else {
				alert(response.message);
			}
		})
	}
});	