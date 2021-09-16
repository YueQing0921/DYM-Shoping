//控制层
app.controller('pageController', function ($scope, $http) {

    $scope.add = function (x) {
        $scope.num += parseInt(x);
        if ($scope.num < 1) $scope.num = 1;
    }
    //$scope.specificationItems保存的是当前选中的规格
    $scope.specificationItems = {};
    $scope.selectSpec = function (key, val) {
        $scope.specificationItems[key] = val;
        $scope.searchSUK();
    }
    $scope.isSelectSpec = function (key, val) {
        return $scope.specificationItems[key] == val ? "selected" : "";
    }

    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec))
    }
    $scope.matchObject = function (map1, map2) {
        for (var k in map1) {
            if (map1[k] != map2[k]) return false;
        }
        for (var k in map2) {
            if (map1[k] != map2[k]) return false;
        }
        return true;
    }
    $scope.searchSUK = function () {
        for (var i = 0; i < skuList.length; i++) {
            if ($scope.matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
            }
        }
    }
    $scope.addCart = function () {
        // alert("sku:" + JSON.stringify($scope.sku) + ",num:" + $scope.num);
        $http.get("http://localhost:9013/cart/addItemToCartList.do?itemId=" + $scope.sku.id + "&num=" + $scope.num, {withCredentials: true}).success(function (resp) {
            if (resp.success) {
                location.href = "http://localhost:9013/cart.html";
            } else {
                alert(resp.message);
            }
        })
    }

});	