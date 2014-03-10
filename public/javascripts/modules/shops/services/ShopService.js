(function (app) {
  "use strict";
  ShopService.$inject = ['$http'];
  function ShopService($http) {
    this.$http = $http;
  }

  ShopService.prototype = {
    list: function () {
      return $http(jsRoutes.controllers.admin.Shop.list()).then(function (response) {
        return response.data;
      });
    },
    remove: function (shopId) {
      return this.$http.delete(jsRoutes.controllers.admin.Shop.remove(shopId).url);
    },
    update: function (shop) {
      return this.$http({
        url: jsRoutes.controllers.admin.Shop.update().url,
        method: 'PUT',
        data: shop,
        dataType: 'json',
        headers: {
          "Content-Type": "application/json"
        }
      });
    },
    add: function (shop) {
      return this.$http({
        url: jsRoutes.controllers.admin.Shop.add().url,
        method: 'POST',
        data: shop,
        dataType: 'json',
        headers: {
          "Content-Type": "application/json"
        }
      }).then(function (response) {
        var shopId = response.data;
        shop.id = shopId;
        return shop;
      });
    }
  }
  app.service('ShopService', ShopService);
}(shopsApp));