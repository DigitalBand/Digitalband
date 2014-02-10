(function StockItemService(app) {
    "use strict";
    StockItemService.$inject = ['$q'];
    function StockItemService($q) {
        this.$q = $q;
    }

    StockItemService.prototype = {
        withDeferred: function(f){
            var deferred = this.$q.defer();
            var items = f.call(this);
            deferred.resolve(items);
            return deferred.promise;
        },
        list: function (productId) {
            return this.withDeferred(function(){
                console.log('productId ' + productId);
                var items = [], i;
                for (i = 0; i < 10; i++) {
                    items.push({
                        productId: productId,
                        id: i + productId,
                        dealerName: 'Музторг',
                        dealerId: 1,
                        quantity: 10,
                        dealerPrice: 1000
                    });
                }
                return items;
            });
        },
        remove: function (itemId) {
            return this.withDeferred(function (){

            });
        }
    }
    app.service('StockItemService', StockItemService);
}(stockItem));