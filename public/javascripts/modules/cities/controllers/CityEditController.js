(function (app) {
    "use strict";
    CityEditController.$inject = ['$scope', '$stateParams', 'CityService', '$state'];
    function CityEditController($scope, $stateParams, cityService, $state) {
        $scope.vm = this;
        this.cityService = cityService;
        this.$state = $state;
        this.cityId = $stateParams.cityId;
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
            if(this.city.delivery === undefined || this.city.payment === undefined){
                return;
            }
            this.cityService.save(this.city).then(function () {
                that.$state.go('home1');
            });
        }
    };
    app.controller('CityEditController', CityEditController);
    return CityEditController;
}(citiesApp));