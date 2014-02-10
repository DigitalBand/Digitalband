(function StockItemController(app, productId) {
    "use strict";
    StockItemController.$inject = ['$scope', 'StockItemService'];
    function StockItemController($scope, stockItemService) {
        this.stockItemService = stockItemService;
        $scope.vm = this;

        this.list(productId);
    }

    StockItemController.prototype = {
        list: function (productId) {
            var that = this;
            this.stockItemService.list(productId).then(function (items) {
                that.items = items;
            });
        },
        remove: function (itemId) {
            var that = this;
            console.log('remove ' + itemId);
            this.stockItemService.remove(itemId).then(function () {
                _.remove(that.items, function (item) {
                    return item.id === itemId
                });
            });
        },
        add: function () {

        }
    };
    app.controller('StockItemController', StockItemController);
    return StockItemController;
}(stockItem, stockItem.productId));