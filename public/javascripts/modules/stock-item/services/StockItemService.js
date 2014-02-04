(function StockItemService(app) {
    "use strict";
    StockItemService.$inject = ['$q'];
    function StockItemService($q) {
        this.$q = $q;
    }

    StockItemService.prototype = {
        list: function (productId) {
            var deferred = this.$q.defer();
            var items = [], i;
            for (i = 0; i < 10; i++) {
                items.push({
                    productId: productId,
                    stockItemId: i + productId,
                    dealerId: 1,
                    quantity: 10,
                    dealerPrice: 1000
                });
            }
            deferred.resolve(items);
            return deferred.promise;
        }
    }
    app.service('StockItemService', StockItemService);
}(stockItem));