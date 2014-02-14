(function StockItemController(app, productId) {
    "use strict";
    StockItemController.$inject = ['$scope', 'StockItemService', 'DealerService'];
    function StockItemController($scope, stockItemService, dealerService) {
        this.dealerService = dealerService;
        this.stockItemService = stockItemService;
        $scope.vm = this;
        this.getDealers();
        this.list(productId);
    }

    StockItemController.prototype = {
        getDealers: function () {
            var that = this;
            this.dealerService.list().then(function (dealers) {
                that.dealers = dealers;
            });
        },
        list: function (productId) {
            var that = this;
            this.stockItemService.list(productId).then(function (items) {
                that.items = items;
            });
        },
        remove: function (itemId) {
            var that = this;
            if (confirm('Вы действительно хотите удалить эту запись?')) {
                this.stockItemService.remove(itemId).then(function () {
                    _.remove(that.items, function (item) {
                        return item.id === itemId
                    });
                });
            }
        },
        edit: function (item) {
            this.stockItem = item;
        },
        add: function () {

        }
    };
    app.controller('StockItemController', StockItemController);
    return StockItemController;
}(stockItem, stockItem.productId));