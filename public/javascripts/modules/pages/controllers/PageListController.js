pagesApp.controller('PageListController', ['$scope', 'PageService', '$state', function ($scope, pageService, $state) {
    "use strict";
    $scope.href = $state.href;
    pageService.list().then(function(pages) {
        $scope.pages = pages;
    });
    $scope.remove = function (pageId) {
        if (confirm('Вы действительно хотите удалить этот элемент?')) {
            pageService.remove(pageId).then(function () {
                _.remove($scope.pages, {id: pageId});
            });
        }
    };
}]);