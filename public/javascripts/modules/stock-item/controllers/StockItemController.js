(function StockItemController(app, productId) {
  "use strict";
  StockItemController.$inject = ['$scope', 'StockItemService', 'DealerService', 'ShopService'];
  function StockItemController($scope, stockItemService, dealerService, shopService) {
    this.dealerService = dealerService;
    this.stockItemService = stockItemService;
    this.shopService = shopService;
    this.getDealers();
    this.getShops();
    this.list(productId);
    this.stockItem = {};
    $scope.vm = this;
  }

  StockItemController.prototype = {
    updateTotals: function () {
      this.items.totalCount = this.items.reduce(function (acc, item2) {
        return acc + parseInt(item2.quantity);
      }, 0);
      this.items.totalValue = this.items.reduce(function (acc, item2) {
        return acc + parseInt(item2.dealerPrice);
      }, 0);
    },
    getShops: function () {
      var that = this;
      this.shopService.list().then(function (shops) {
        that.shops = shops;
      });
    },
    getDealers: function () {
      var that = this;
      this.dealerService.list().then(function (dealers) {
        that.dealers = dealers;
      });
    },
    list: function (productId) {
      var that = this;
      this.stockItemService.list(productId).then(function (items) {
        that.items = items;
        that.updateTotals();
      });
    },
    remove: function (itemId) {
      var that = this;
      if (confirm('Вы действительно хотите удалить эту запись?')) {
        this.stockItemService.remove(itemId).then(function () {
          that.items = that.items.filter(function(item){
            return item.id !== itemId;
          });
          that.updateTotals()
        });
      }
    },
    edit: function (item) {
      this.stockItem = angular.copy(item);
    },
    getShopTitle: function (shopId) {
      return this.shops.find(function(item){
        return item.id === shopId;
      }).title;
    },
    save: function () {
      this.stockItem.shopTitle = this.getShopTitle(this.stockItem.shopId);
      this.stockItem.dealerPrice = +this.stockItem.dealerPrice.replace(',', '.');
      if (this.stockItem.id) {
        this.update();
      } else {
        this.create();
      }
    },
    update: function () {
      var that = this;
      var itm = angular.copy(this.stockItem);
      this.stockItemService.update(itm).then(function () {
        var index = that.items.findIndex(function (item) {
          return item.id === that.stockItem.id
        });
        that.items[index] = itm;
        that.stockItem = {};
        that.updateTotals();
      });
    },
    create: function () {
      var that = this;

      this.stockItemService.create(productId, angular.copy(this.stockItem)).then(function (item) {
        that.items.push(item);
        that.stockItem = {};
        that.updateTotals();
      });
    }
  };
  app.controller('StockItemController', StockItemController);
  return StockItemController;
}(stockItem, stockItem.productId));
