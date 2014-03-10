(function(app){
  "use strict";
  ShopService.$inject = ['$http'];
  function ShopService($http){
    this.$http = $http;
  }
  ShopService.prototype = {
    list: function () {
      return this.$http(jsRoutes.controllers.admin.Shop.list()).then(function (response) {
        return response.data;
      });
    }
  };
  app.service('ShopService', ShopService);
}(stockItem));