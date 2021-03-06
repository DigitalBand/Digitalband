(function (app) {
    "use strict";
    CityEditController.$inject = ['$scope', '$stateParams', 'CityService', 'ShopService', '$state'];
    function CityEditController($scope, $stateParams, cityService, shopService, $state) {
        $scope.vm = this;
        this.cityService = cityService;
        this.shopService = shopService;
        this.$state = $state;
        this.cityId = $stateParams.cityId;
        $scope.editorOptions = {
          language: 'ru'
        };
        this.initCity();
    }

    CityEditController.prototype = {
        initCity: function () {
            if (this.cityId) {
                var that = this;
                this.cityService.get(this.cityId).then(function (city) {
                    that.city = city;
                });
            }
            else {
                this.city = {};
            }
        },
        save: function () {
            var that = this;
            if(!this.city.delivery || !this.city.payment){
                return;
            }
            this.cityService.save(this.city).then(function () {
                that.$state.go('home1');
            });
        },
        deliveryNotFilled: function() {
            return !this.city.delivery;
        },
        paymentNotFilled: function() {
            return !this.city.payment;
        }
    };
    app.controller('CityEditController', CityEditController);
    return CityEditController;
}(citiesApp));
