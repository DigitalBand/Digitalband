(function StockItemService(app, routes) {
  "use strict";
  StockItemService.$inject = ['$http'];
  function StockItemService($http) {
    this.$http = $http;
  }

  StockItemService.prototype = {
    list: function (productId) {
      return this.$http(routes.StockItem.list(productId)).then(function (response) {
        return response.data;
      });
    },
    remove: function (itemId) {
      return this.$http(routes.StockItem.remove(itemId));
    },
    update: function (item) {
      return this.$http(angular.extend(routes.StockItem.update(item.id), {data: item})).then(function (response) {
        return item;
      });
    },
    create: function (productId, item) {
      item.id = 0;
      return this.$http(angular.extend(routes.StockItem.create(productId), {data: item})).then(function (response) {
        item.id = response.data;
        return item;
      });

    }
  }
  app.service('StockItemService', StockItemService);
}(stockItem, jsRoutes.controllers.admin));