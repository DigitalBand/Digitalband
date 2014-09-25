(function (app) {
    "use strict";
    CityService.$inject = ['$http'];
    function CityService($http) {
        this.$http = $http;
    }

    CityService.prototype = {
        get: function(cityId) {
            return this.$http(jsRoutes.controllers.admin.City.get(cityId)).then(function(response){
                return response.data;
            });
        },
        list: function () {
            return this.$http(jsRoutes.controllers.admin.City.list()).then(function (response) {
                return response.data;
            });
        },
        remove: function (cityId) {
            return this.$http.delete(jsRoutes.controllers.admin.City.remove(cityId).url);
        },
        update: function (city) {
            return this.$http({
                url: jsRoutes.controllers.admin.City.update().url,
                method: 'PUT',
                data: city,
                dataType: 'json',
                headers: {
                    "Content-Type": "application/json"
                }
            });
        },
        save: function (city) {
            if (city.id) {
                return this.update(city);
            } else {
                city.id = 0;
                return this.add(city);
            }
        },
        add: function (city) {
            return this.$http({
                url: jsRoutes.controllers.admin.City.add().url,
                method: 'POST',
                data: city,
                dataType: 'json',
                headers: {
                    "Content-Type": "application/json"
                }
            }).then(function (response) {
                var cityId = response.data;
                city.id = cityId;
                return city;
            });
        }
    }
    app.service('CityService', CityService);
}(citiesApp));