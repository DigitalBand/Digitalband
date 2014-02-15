(function StockItemService(app) {
    "use strict";
    StockItemService.$inject = ['$q', '$http'];
    function StockItemService($q, $http) {
        this.$http = $http;
        this.$q = $q;
    }

    StockItemService.prototype = {
        withDeferred: function (f) {
            var deferred = this.$q.defer();
            var items = f.call(this);
            deferred.resolve(items);
            return deferred.promise;
        },
        list: function (productId) {
            return this.$http(jsRoutes.controllers.admin.StockItem.list(productId)).then(function (response) {
                return response.data;
            });
        },
        remove: function (itemId) {
            return this.$http(jsRoutes.controllers.admin.StockItem.remove(itemId));
        },
        update: function (item) {
            return this.$http(
                {
                    url: jsRoutes.controllers.admin.StockItem.update(item.id).url,
                    method: 'PUT',
                    data: item,
                    dataType: 'json',
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
            ).then(function (response) {
                    return item;
                });
        },
        create: function (productId, item) {
            item.id = 0;
            return this.$http(
                {
                    url: jsRoutes.controllers.admin.StockItem.create(productId).url,
                    method: 'POST',
                    data: item,
                    dataType: 'json',
                    headers: {
                        "Content-Type": "application/json"
                    }
                }).then(function (response) {
                    item.id = response.data;
                    return item;
                });

        }
    }
    app.service('StockItemService', StockItemService);
}(stockItem));