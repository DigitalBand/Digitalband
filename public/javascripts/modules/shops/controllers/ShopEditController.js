(function (app) {
  "use strict";
  ShopEditController.$inject = ['$scope', '$stateParams', 'ShopService', '$state'];
  function ShopEditController($scope, $stateParams, shopService, $state) {
    $scope.vm = this;
    this.shopService = shopService;
    this.$state = $state;
    this.shopId = $stateParams.shopId;
    this.initShop();
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
    save: function () {
      var that = this;
      this.shopService.save(this.shop).then(function () {
        that.$state.go('home1');
      });
    },
    addPhone: function(){
      if (!this.shop.phoneNumbers) this.shop.phoneNumbers = [];
      this.shop.phoneNumbers.push(angular.copy(this.shop.phone));
      this.shop.phone = "";
    },
    removePhone: function(phone) {
      _.remove(this.shop.phoneNumbers, function(item){
        return item === phone;
      });
    }
  };
  app.controller('ShopEditController', ShopEditController);
  return ShopEditController;
}(shopsApp));