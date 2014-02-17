(function DealerService(app) {
    "use strict";
    DealerService.$inject = ['$q', '$http'];
    function DealerService($q, $http) {
        this.$q = $q;
        this.$http = $http;
    }

    DealerService.prototype = {
        list: function () {
            return this.$http(jsRoutes.controllers.admin.Dealer.list()).then(function(response){
                return response.data;
            });
        },
        add: function () {

        },
        remove: function () {

        }
    };
    app.service('DealerService', DealerService);
}(stockItem));