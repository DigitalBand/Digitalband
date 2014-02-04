(function DealerService(app) {
    "use strict";
    DealerService.$inject = ['$q'];
    function DealerService($q) {
        this.$q = $q;
    }

    DealerService.prototype = {
        list: function () {

        },
        add: function () {

        },
        remove: function () {

        }
    };
    app.service('DealerService', DealerService);
}(stockItem));