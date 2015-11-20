(function (app) {
  "use strict";
  ShopEditController.$inject = ['$scope', '$stateParams', 'ShopService', 'CityService', '$state', '$q'];

  function ShopEditController($scope, $stateParams, shopService, cityService, $state, $q) {
    $scope.vm = this;
    this.shopService = shopService;
    this.cityService = cityService;
    this.$state = $state;
    this.$q = $q;
    this.shopId = $stateParams.shopId;
    var that = this;
    this.initShop().then(that.initCity.bind(this));
  }

  ShopEditController.prototype = {
    initShop: function () {
      if (this.shopId) {
        var that = this;
        return this.shopService.get(this.shopId).then(function (shop) {
          that.shop = shop;
          return shop;
        });
      }
      else {
        this.shop = {};
        return this.$q.when(this.shop);
      }
    },
    initCity: function () {
      if (!this.shop) return;
      var that = this;
      this.cityService.get(this.$state.params.cityId).then(function (city) {
        that.city = city;
        that.shop.cityId = city.id;
        that.shop.cityName = city.name;
      });
    },
    save: function () {
      var that = this;
      if (this.phone) this.addPhone();
      this.shopService.save(this.shop).then(function () {
        that.$state.go('cityEdit', {cityId: that.$state.params.cityId});
      });
    },
    addPhone: function () {
      if (!this.shop.phoneNumbers) this.shop.phoneNumbers = [];
      this.shop.phoneNumbers.push(angular.copy(this.phone));
      this.phone = "";
    },
    removePhone: function (phone) {
      this.shop.phoneNumbers = this.shop.phoneNumbers.filter(function (item) {
        return item !== phone;
      });
    }
  };
  app.controller('ShopEditController', ShopEditController);
  return ShopEditController;
}(citiesApp));
