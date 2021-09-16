//控制层
app.controller('cartController', function ($scope, cartService) {

    $scope.findCartList = function () {
        cartService.findCartList().success(function (resp) {
            $scope.cartList = resp;
            //计算合计数量，金额
            $scope.totalValue = cartService.sum($scope.cartList);
        })
    }

    $scope.addItemToCartList = function (itemId, num) {
        cartService.addItemToCartList(itemId, num).success(function (resp) {
            if (resp.success) {
                $scope.findCartList();
            } else {
                alert(resp.message);
            }
        })

    }
    //------------------------------------
    //结算页


    $scope.findAddressListByUsername = function () {
        cartService.findAddressListByUsername().success(function (resp) {
            $scope.addressList = resp;
            for (var i = 0; i < $scope.addressList.length; i++) {
                if ($scope.addressList[i].isDefault == "1") {
                    //初始化默认地址
                    $scope.address = $scope.addressList[i];
                    break;
                }
            }
        })
    }
    $scope.selectedAddress = function (addr) {
        $scope.address = addr;
    }
    $scope.isSelectedAddress = function (addr) {
        return (($scope.address == addr) ? 'selected' : '');
    }
    //支付方式
    $scope.order = {paymentType: 1};
    $scope.selectedPaytype = function (pt) {
        $scope.order.paymentType = pt;
    }
    $scope.isSelectedPaytype = function (pt) {
        return (($scope.order.paymentType == pt) ? 'selected' : '')
    }

    //提交订单
    $scope.submitOrder = function () {
        $scope.order.receiver = $scope.address.contact;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiverAreaName = $scope.address.address;

        cartService.add($scope.order).success(function (resp) {
            alert(resp.message);
        })

    }


});