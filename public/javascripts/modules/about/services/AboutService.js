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
        update: function (aboutInfo) {
            return this.$http({
                url: jsRoutes.controllers.admin.About.update().url,
                method: 'PUT',
                data: aboutInfo,
                dataType: 'json',
                headers: {
                    "Content-Type": "application/json"
                }
            });
        },
        save: function (aboutInfo) {
            if (aboutInfo.about) {
                return this.update(aboutInfo);
            } else {
                return this.add(aboutInfo);
            }
        },
        add: function (aboutInfo) {
            return this.$http({
                url: jsRoutes.controllers.admin.About.add().url,
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