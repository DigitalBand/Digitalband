var stockItem = angular.module('stockItem', []);

(function StockItemController(app) {
    "use strict";
    StockItemController.$inject = ['$scope', 'StockItemService'];
    function StockItemController($scope, stockItemService) {
        this.stockItemService = stockItemService;
        this.vm = $scope;
        this.list();
    }

    StockItemController.prototype = {
        list: function (productId) {
            var that = this;
            this.stockItemService.list(productId).then(function (items) {
                that.vm.items = items;
                console.log('items length: ' + items.length);
            });
        },
        remove: function () {

        },
        add: function () {

        }
    };
    app.controller('StockItemController', StockItemController);
    return StockItemController;
}(stockItem));
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

