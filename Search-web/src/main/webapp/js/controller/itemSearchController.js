//控制层
app.controller('itemSearchController', function ($scope, $controller, itemSearchService,$location) {

    $controller('baseController', {$scope: $scope});//继承
    //读取列表数据绑定到表单中
    $scope.search = function () {
        itemSearchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                $scope.list = response.rows;
                $scope.buildPageLabel();
            }
        );
    }
    //增加价格域
    $scope.searchMap = {"keywords": "", "category": "", "brand": "", "spec": {}, "price": "", "pageNo": 1, "pageSize": 5, "sort": "", "sortField": "",};//搜索对象
//增加价格判断
    $scope.addSearchItem = function (key, value) {

        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        console.log($scope.searchMap)
        $scope.search();
    }
//删除价格判断
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        console.log($scope.searchMap)
        $scope.search();
    }
    $scope.buildPageLabel = function () {
        $scope.pageLabel = [];  //页码，最多只存5个
        var totalPages = $scope.resultMap.totalPages;//设置最大页码
        var firstPage = 1;
        var lastPage = totalPages;
        //默认显示5页，如果比5页多则显示部分页面
        if (totalPages > 5) {
            if ($scope.searchMap.pageNo < 3) {//如果当前页是前三页，则显示前5页
                lastPage = 5;
            }
            else if ($scope.searchMap.pageNo > lastPage - 2) {//如果是后三页，则显示最后5页
                firstPage = totalPages - 4;
            }
            else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
        //计算上一页，下一页
        $scope.prePageNo = ($scope.searchMap.pageNo > 1) ? ($scope.searchMap.pageNo - 1) : 1;
        $scope.postPageNo = ($scope.searchMap.pageNo < $scope.resultMap.totalPages ) ? ($scope.searchMap.pageNo + 1) : $scope.resultMap.totalPages;
    }

    $scope.queryByPage = function (pageNo) {
        //如果不在页面数字范围则不处理
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = parseInt(pageNo);
        $scope.search();
    }
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    }

    $scope.keywordsIsBrand = function () {
        //alert(1)
        //如果搜索关键字中包含品牌名称则返回true，否则返回false
        var brandList = $scope.resultMap.brandList;
        var keywords = $scope.searchMap.keywords;
        for (var i = 0; i < brandList.length; i++) {
            if (keywords.indexOf(brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    }

    $scope.loadKeywords = function () {
        var keywords = $location.search()["keywords"];
        if (keywords != null) {
            $scope.searchMap.keywords = keywords;
            $scope.search();
        }
    }

});
