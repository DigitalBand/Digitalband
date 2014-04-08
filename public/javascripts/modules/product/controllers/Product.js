(function (app, routes) {
  "use strict";
  ProductController.$inject = ['$scope', '$http'];
  function ProductController($scope, $http) {
    $scope.vm = this;
    this.$scope = $scope;
    this.$http = $http;
    $http(routes.Product.listAllNotInStock()).then(function (response) {
      $scope.items = response.data;
    });
  }

  ProductController.prototype = {
    deleteAll: function (items) {
      var that = this;
      this.isDeleting = true;
      var item = _.head(items);
      this.$scope.itemsDeletedCount = this.$scope.items.length - items.length;
      this.$scope.progress = (this.$scope.itemsDeletedCount / this.$scope.items.length) * 100;
      that.$http(routes.Product.deleteById(item)).then(function (response) {
        that.deleteAll(_.tail(items))
      });

    }
  };
  app.controller('ProductController', ProductController);
  return ProductController;
}(angular.module('productModule'), jsRoutes.controllers.admin));