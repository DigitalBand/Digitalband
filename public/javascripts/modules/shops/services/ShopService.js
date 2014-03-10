(function (app) {
  "use strict";
  ShopService.$inject = ['$http'];
  function ShopService($http) {
    this.$http = $http;
  }

  ShopService.prototype = {
    get: function(shopId) {
      return this.$http(jsRoutes.controllers.admin.Shop.get(shopId)).then(function(response){
        return response.data;
      });
    },
    list: function () {
      return this.$http(jsRoutes.controllers.admin.Shop.list()).then(function (response) {
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
    save: function (shop) {
      if (shop.id) {
        return this.update(shop);
      } else {
        shop.id = 0;
        return this.add(shop);
      }
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