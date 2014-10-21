(function (app) {
    "use strict";
    CityService.$inject = ['$http'];
    function CityService($http) {
        this.$http = $http;
    }

    CityService.prototype = {
        list: function () {
            return this.$http(jsRoutes.controllers.admin.City.list()).then(function (response) {
                return response.data;
            });
        }
    }
    app.service('CityService', CityService);
}(shopsApp));