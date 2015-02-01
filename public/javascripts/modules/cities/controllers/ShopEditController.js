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
    this.initCity();
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
    initCity: function() {
        var that = this;
        this.cityService.get(this.$state.params.cityId).then(function (city) {
            that.city = city;
            that.shop.cityId = city.id;
            that.shop.cityName = city.name;
        });
    },
    save: function () {
      var that = this;
      if(this.shop.phone) this.addPhone();
      this.shopService.save(this.shop).then(function () {
        that.$state.go('cityEdit', { cityId: that.$state.params.cityId });
      });
    },
    addPhone: function(){
      if (!this.shop.phoneNumbers) this.shop.phoneNumbers = [];
      this.shop.phoneNumbers.push(angular.copy(this.shop.phone));
      this.shop.phone = "";
    },
    removePhone: function(phone) {
      this.shop.phoneNumbers = this.shop.phoneNumbers.filter(function(item){
        return item !== phone;
      });
    }
  };
  app.controller('ShopEditController', ShopEditController);
  return ShopEditController;
}(citiesApp));
