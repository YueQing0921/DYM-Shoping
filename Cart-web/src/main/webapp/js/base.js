var app = angular.module("dongyimai", []);

app.filter('to_trusted',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);