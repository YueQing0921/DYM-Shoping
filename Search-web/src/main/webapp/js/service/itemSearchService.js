//服务层
app.service('itemSearchService', function ($http) {
    //增加
    this.search = function (searchMap) {
        return $http.post('/itemsearch/search.do', searchMap);
    }
});