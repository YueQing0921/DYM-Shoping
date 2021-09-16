//控制层
app.controller('indexController', function ($scope, $controller, indexService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.getName = function () {
        indexService.getName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }


});	