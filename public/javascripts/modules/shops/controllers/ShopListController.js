shopsApp.controller('ShopListController', ['$scope', 'ShopService', '$state', function ($scope, shopService, $state) {
  "use strict";
  $scope.href = $state.href;
  shopService.list().then(function(shops){
    $scope.shops = shops;
  });
  $scope.remove = function (shopId) {
    if (confirm('Вы действительно хотите удалить этот элемент?')) {
      shopService.remove(shopId).then(function () {
        _.remove($scope.shops, {id: shopId});
      });
    }
  };
}]);