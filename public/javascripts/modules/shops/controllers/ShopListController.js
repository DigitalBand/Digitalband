shopsApp.controller('ShopListController', ['$scope', 'ShopService', function ($scope, shopService) {
  "use strict";
  console.log('load');
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