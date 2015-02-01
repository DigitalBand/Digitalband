pagesApp.controller('PageListController', ['$scope', 'PageService', '$state', function ($scope, pageService, $state) {
  "use strict";
  $scope.href = $state.href;
  pageService.list().then(function (pages) {
    $scope.pages = pages;
  });
  $scope.remove = function (pageId) {
    if (confirm('Вы действительно хотите удалить этот элемент?')) {
      pageService.remove(pageId).then(function () {
        $scope.pages = $scope.pages.filter(function(item) {
          return item.id !== pageId;
        });
      });
    }
  };
}]);
