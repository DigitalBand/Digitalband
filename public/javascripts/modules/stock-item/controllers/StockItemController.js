(function StockItemController(app) {
    "use strict";
    StockItemController.$inject = ['$scope', 'StockItemService'];
    function StockItemController($scope, stockItemService) {
        this.stockItemService = stockItemService;
        $scope.vm = this;

        this.list();
    }

    StockItemController.prototype = {
        list: function (productId) {
            var that = this;
            this.stockItemService.list(productId).then(function (items) {
                that.items = items;
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