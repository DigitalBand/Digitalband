deliveryApp.controller('DeliveryListController', ['$scope', 'DeliveryService', '$state', function ($scope, deliveryService, $state) {
    "use strict";
    $scope.href = $state.href;
    deliveryService.list().then(function(deliveries){
        $scope.deliveries = deliveries;
    });
    $scope.remove = function (deliveryId) {
        if (confirm('Вы действительно хотите удалить этот элемент?')) {
            deliveryService.remove(deliveryId).then(function () {
                _.remove($scope.deliveries, {id: deliveryId});
            });
        }
    };
}]);