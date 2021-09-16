app.controller("baseController", function ($scope) {
    //分页处理
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30],
        onChange: function () {
            $scope.reloadList();
        }
    };

    $scope.selectIds = [];
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            let idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);
        }
    }

    $scope.reloadList = function () {
        $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }


})