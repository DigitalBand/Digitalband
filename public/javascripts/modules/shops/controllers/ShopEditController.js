(function (app) {
  "use strict";
  ShopEditController.$inject = ['$scope', '$stateParams', 'ShopService', 'CityService', '$state'];
  function ShopEditController($scope, $stateParams, shopService, cityService, $state) {
    $scope.vm = this;
    this.shopService = shopService;
    this.cityService = cityService;
    this.$state = $state;
    this.shopId = $stateParams.shopId;
    this.initShop();
    this.initCities();
  }

  ShopEditController.prototype = {
    initShop: function () {
      if (this.shopId) {
        var that = this;
        this.shopService.get(this.shopId).then(function (shop) {
          that.shop = shop;
        });
      }
      else {
        this.shop = {};
      }
    },
    initCities: function () {
      var that = this;
      this.cityService.listShortInfo().then(function (cities) {
        that.cities = cities;
      });
    },
    save: function () {
      var that = this;
      this.shopService.save(this.shop).then(function () {
        that.$state.go('home1');
      });
    },
    addPhone: function () {
      if (!this.shop.phoneNumbers) this.shop.phoneNumbers = [];
      this.shop.phoneNumbers.push(angular.copy(this.shop.phone));
      this.shop.phone = "";
    },
    removePhone: function (phone) {
      this.shop.phoneNumbers = this.shop.phoneNumbers.filter(function(item){
        return item !== phone;
      });
    }
  };
  app.controller('ShopEditController', ShopEditController);
  return ShopEditController;
}(shopsApp));
