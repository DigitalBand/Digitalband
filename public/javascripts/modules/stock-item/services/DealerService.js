(function DealerService(app) {
    "use strict";
    DealerService.$inject = ['$q'];
    function DealerService($q) {
        this.$q = $q;
    }

    DealerService.prototype = {
        withDeferred: function(f){
            var deferred = this.$q.defer();
            var items = f.call(this);
            deferred.resolve(items);
            return deferred.promise;
        },
        list: function () {
            return this.withDeferred(function(){
                var dealers = ['Музторг','Слами'];
                return dealers;
            });
        },
        add: function () {

        },
        remove: function () {

        }
    };
    app.service('DealerService', DealerService);
}(stockItem));