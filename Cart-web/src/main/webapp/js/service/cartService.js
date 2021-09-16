//服务层
app.service('cartService', function ($http) {

    this.add = function (order) {
        return $http.post('/order/add.do', order);
    }
    this.findAddressListByUsername = function () {
        return $http.get('/address/findByUsername.do');
    }

    this.findCartList = function () {
        return $http.get('/cart/findCartList.do');
    }

    this.addItemToCartList = function (itemId, num) {
        return $http.get('/cart/addItemToCartList.do?itemId=' + itemId + "&num=" + num);
    }
    this.sum = function (cartList) {
        var totalValue = {totalNum: 0, totalMoney: 0.00};
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];//明细
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    }

});