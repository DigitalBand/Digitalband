(function (app) {
    "use strict";
    CityService.$inject = ['$http'];
    function CityService($http) {
        this.$http = $http;
    }

    CityService.prototype = {
        listShortInfo: function () {
            return this.$http(jsRoutes.controllers.admin.City.listShortInfo()).then(function (response) {
                return response.data;
            });
        }
    }
    app.service('CityService', CityService);
}(shopsApp));