(function DealerController(app) {
    "use strict";
    DealerController.$inject = ['$scope']
    function DealerController($scope) {
        $scope.vm = this;

    }

    DealerController.prototype = {
        list: function () {
            var that = this;
            dealerService.list().then(function (items) {
                that.dealers = items;
            });
        }
    }
    app.controller('DealerController', DealerController);
}(stockItem));