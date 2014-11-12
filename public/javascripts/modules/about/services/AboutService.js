(function (app) {
    "use strict";
    AboutService.$inject = ['$http'];
    function AboutService($http) {
        this.$http = $http;
    }

    AboutService.prototype = {
        get: function() {
            return this.$http(jsRoutes.controllers.admin.About.get()).then(function(response) {
                return response.data;
            });
        },

        save: function (aboutInfo) {
            return this.$http({
                url: jsRoutes.controllers.admin.About.save().url,
                method: 'POST',
                data: aboutInfo,
                dataType: 'json',
                headers: {
                    "Content-Type": "application/json"
                }
            });
        }
    }
    app.service('AboutService', AboutService);
}(aboutApp));