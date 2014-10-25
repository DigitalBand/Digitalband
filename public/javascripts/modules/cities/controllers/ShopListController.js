(function (app) {
    "use strict";
    ShopListController.$inject = ['$scope', 'ShopService', '$stateParams', '$state'];
    function ShopListController($scope, shopService, $stateParams, $state) {
        $scope.vm = this;
        this.shopService = shopService;
        this.$state = $state;
        this.cityId = $stateParams.cityId;
        this.initShops();
    }

    ShopListController.prototype = {
        initShops: function () {
            if (this.cityId) {
                var that = this;
                this.shopService.getByCity(this.cityId).then(function (shops) {
                    that.shops = shops;
                });
            }
            else {
                this.shops = {};
            }
        },
        remove: function (shopId) {
            if (confirm('Вы действительно хотите удалить этот элемент?')) {
                var that = this;
                this.shopService.remove(shopId).then(function () {
                    _.remove(that.shops, {id: shopId});
                });
            }
        }

    };
    app.controller('ShopListController', ShopListController);
    return ShopListController;
}(citiesApp));