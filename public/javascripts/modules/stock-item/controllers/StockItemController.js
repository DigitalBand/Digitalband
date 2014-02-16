(function StockItemController(app, productId) {
    "use strict";
    StockItemController.$inject = ['$scope', 'StockItemService', 'DealerService'];
    function StockItemController($scope, stockItemService, dealerService) {
        this.dealerService = dealerService;
        this.stockItemService = stockItemService;
        $scope.vm = this;
        this.getDealers();
        this.list(productId);
        this.stockItem = {};
    }

    StockItemController.prototype = {
        updateTotals: function() {
            this.items.totalCount = _.reduce(this.items, function(acc, item2){
                return acc + parseInt(item2.quantity);
            }, 0);
            this.items.totalValue = _.reduce(this.items, function(acc, item2){
                return acc + parseInt(item2.dealerPrice);
            }, 0);
        },
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
                that.updateTotals();
            });
        },
        remove: function (itemId) {
            var that = this;
            if (confirm('Вы действительно хотите удалить эту запись?')) {
                this.stockItemService.remove(itemId).then(function () {
                    _.remove(that.items, function (item) {
                        return item.id === itemId
                    });
                    that.updateTotals()
                });
            }
        },
        edit: function (item) {
            this.stockItem = angular.copy(item);
        },
        save: function () {
            if (this.stockItem.id) {
                this.update();
            } else {
                this.create();
            }
        },
        update: function(){
            var that = this;
            var itm = angular.copy(this.stockItem);
            this.stockItemService.update(itm).then(function(){
                var index = _.findIndex(that.items, function(item){return item.id === that.stockItem.id});
                that.items[index] = itm;
                that.stockItem = {};
                that.updateTotals();
            });
        },
        create: function () {
            var that = this;
            this.stockItemService.create(productId, angular.copy(this.stockItem)).then(function(item){
                that.items.push(item);
                that.stockItem = {};
                that.updateTotals();
            });
        }
    };
    app.controller('StockItemController', StockItemController);
    return StockItemController;
}(stockItem, stockItem.productId));