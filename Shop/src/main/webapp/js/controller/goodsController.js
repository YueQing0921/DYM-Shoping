//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

        $controller('baseController', {$scope: $scope});//继承

        //读取列表数据绑定到表单中
        $scope.findAll = function () {
            goodsService.findAll().success(
                function (response) {
                    $scope.list = response;
                }
            );
        }

        //分页
        $scope.findPage = function (page, rows) {
            goodsService.search(page, rows, $scope.searchEntity).success(
                function (response) {
                    $scope.list = response.rows;
                    $scope.paginationConf.totalItems = response.total;//更新总记录数
                }
            );
        }

        //查询实体
        $scope.findOne = function () {
            var id = $location.search()["id"];
            if (id == null) { //如果id没找到，说明是新增
                return;
            }
            //如果有，则查询对象
            goodsService.findOne(id).success(
                function (response) {
                    $scope.entity = response;
                    editor.html($scope.entity.goodsDesc.introduction);
                    $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                    $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                    for (var i = 0; i < $scope.entity.itemList.length; i++) {
                        $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                    }
                }
            );
        }

        //保存
        $scope.save = function () {
            $scope.entity.goodsDesc.introduction = editor.html();

            var serviceObject;//服务层对象
            if ($scope.entity.id != null) {//如果有ID
                serviceObject = goodsService.update($scope.entity); //修改
            } else {
                serviceObject = goodsService.add($scope.entity);//增加
            }
            serviceObject.success(
                function (response) {
                    alert(response.message);
                    if (response.success) {
                        //重新查询
                        // $scope.reloadList();//重新加载
                        location.href = "goods.html";
                    }
                }
            );
        }


        //批量删除
        $scope.dele = function () {
            //获取选中的复选框
            goodsService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();//刷新列表
                        $scope.selectIds = [];
                    }
                }
            );
        }

        $scope.searchEntity = {};//定义搜索对象

        //搜索
        $scope.search = function (page, rows) {
            goodsService.search(page, rows, $scope.searchEntity).success(
                function (response) {
                    $scope.list = response.rows;
                    $scope.paginationConf.totalItems = response.total;//更新总记录数
                }
            );
        }

        //TODO ENTITY在这里！！！！
        $scope.entity = {isEnableSpec: 0, goodsDesc: {customAttributeItems: [], itemImages: [], specificationItems: []}} //初始化entity(tb_goods)
        $scope.$watch("entity.isEnableSpec", function (newValue, oldValue) {
            if (newValue == 0) {
                $scope.entity.goodsDesc.specificationItems = [];
                $scope.entity.itemList = [];
            }
        })

        //图片
        $scope.uploadImage = function () {
            uploadService.uploadFile().success(function (resp) {
                if (resp.success) {
                    $scope.image_entity.url = resp.message;
                } else {
                    alert(resp.message);
                }
            })
        }
        $scope.addImages = function () {
            $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
        }
        $scope.delImages = function (idx) {
            $scope.entity.goodsDesc.itemImages.splice(idx, 1);
        }

        //商品分类
        $scope.selectItemCat1List = function () {//1级分类
            itemCatService.findByParentId(0).success(function (resp) {
                $scope.itemCat1List = resp;
            })
        }
        $scope.$watch("entity.category1Id", function (newValue, oldValue) {
            if (newValue) {
                itemCatService.findByParentId(newValue).success(function (resp) {//2级分类
                    $scope.itemCat2List = resp;
                })
            }
        })
        $scope.$watch("entity.category2Id", function (newValue, oldValue) {
            if (newValue) {
                itemCatService.findByParentId(newValue).success(function (resp) {//3级分类
                    $scope.itemCat3List = resp;
                })
            }
        })
        $scope.$watch("entity.category3Id", function (newValue, oldValue) {
            if (newValue) {
                itemCatService.findOne(newValue).success(function (resp) {//获取模版id
                    $scope.entity.typeTemplateId = resp.typeId;
                })
            }
        })
        //根据模版获取品牌列表
        $scope.$watch("entity.typeTemplateId", function (newValue, oldValue) {
            if (newValue) {
                //获取品牌，自定义属性
                typeTemplateService.findOne(newValue).success(function (resp) {//获取模版id
                    $scope.typteTemplate = resp;
                    //JSON String -> JSON object/array
                    $scope.typteTemplate.brandIds = JSON.parse($scope.typteTemplate.brandIds);//品牌列表
                    //如果是新增，从模版获取
                    if ($location.search()["id"] == null) {
                        $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typteTemplate.customAttributeItems);//自定义属性列表
                    }

                })
                //获取规格
                typeTemplateService.selectSpecList(newValue).success(function (resp) {
                    $scope.specList = resp;
                })
            }
        })
        //处理规格选择
        $scope.searchSpecObject = function (list, key, val) {
            for (var i = 0; i < list.length; i++) {
                if (list[i][key] == val) {
                    return list[i];
                }
            }
            return null;
        }
        $scope.checkAttributeValue = function (specName, optionName) {
            var specObject = $scope.searchSpecObject($scope.entity.goodsDesc.specificationItems, "attributeName", specName);
            if (specObject == null) {
                return false;
            } else {
                return (specObject.attributeValue.indexOf(optionName) >= 0);
            }
        }

        $scope.updateSelectSpec = function ($event, specName, optionName) {
            var specObject = $scope.searchSpecObject($scope.entity.goodsDesc.specificationItems, "attributeName", specName);
            if (specObject == null) {
                //[{"attributeName":"网络制式","attributeValue":["移动3G"]}]
                $scope.entity.goodsDesc.specificationItems.push({
                    "attributeName": specName,
                    "attributeValue": [optionName]
                })
            } else {
                if ($event.target.checked) {
                    specObject.attributeValue.push(optionName);
                } else {
                    //反选
                    //移除规格选项
                    var optIdx = specObject.attributeValue.indexOf(optionName);
                    specObject.attributeValue.splice(optIdx, 1);
                    //如果选项为空，则移除规格
                    if (specObject.attributeValue.length == 0) {
                        var specIdx = $scope.entity.goodsDesc.specificationItems.indexOf(specObject)
                        $scope.entity.goodsDesc.specificationItems.splice(specIdx, 1);
                    }

                }
            }

        }

        //SKU
        $scope.createItemList = function () {
            $scope.entity.itemList = [{"spec": {}, "price": 0, "num": 0, "status": "0", "isDefault": "0"}];
            var specList = $scope.entity.goodsDesc.specificationItems;
            for (var i = 0; i < specList.length; i++) {
                $scope.entity.itemList = $scope.addColumn($scope.entity.itemList, specList[i].attributeName, specList[i].attributeValue);
            }
        }
        $scope.addColumn = function (list, attributeName, attributeValue) {
            var newList = [];
            for (var i = 0; i < list.length; i++) {
                var oldItem = list[i];
                for (var j = 0; j < attributeValue.length; j++) {
                    var newItem = JSON.parse(JSON.stringify(oldItem));//深层拷贝，获得一个新的内存空间，同时值保持不变
                    newItem.spec[attributeName] = attributeValue[j];
                    newList.push(newItem);
                }
            }
            return newList;
        }


        //列表页

        //状态显示
        $scope.status = ["未审核", "已审核"]
        $scope.itemCatList = {};
        $scope.selectAllCat = function () {
            itemCatService.findAll().success(function (resp) {
                for (var i = 0; i < resp.length; i++) {
                    $scope.itemCatList[resp[i].id] = resp[i].name;
                }
            })
        }

    }
);