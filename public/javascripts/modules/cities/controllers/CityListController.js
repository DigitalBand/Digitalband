citiesApp.controller('CityListController', ['$scope', 'CityService', '$state', function ($scope, cityService, $state) {
  "use strict";
  $scope.href = $state.href;
  cityService.list().then(function (cities) {
    $scope.cities = cities;
  });
  $scope.remove = function (cityId) {
    if (confirm('Вы действительно хотите удалить этот элемент?')) {
      cityService.remove(cityId).then(function () {
        $scope.cities = $scope.cities.filter(function(el){
          return el.id !== cityId;
        });
      });
    }
  };
}]);
