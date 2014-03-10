"use strict";
var shopsApp = angular.module('shopsModule', ['ngRoute', 'ui.router']);
shopsApp.config(['$stateProvider', function ($stateProvider) {
  function vw(url) {
    return '/assets/javascripts/modules/shops' + url;
  }

  $stateProvider.state('home0', {
    url: '',
    controller: 'ShopListController',
    templateUrl: vw('/partials/shop/shop-list.html')
  }).state('home1', {
    url: '/',
    controller: 'ShopListController',
    templateUrl: vw('/partials/shop/shop-list.html')
  }).state('edit', {
    url: '/edit?id',
    controller: 'ShopEditControoler',
    templateUrl: vw('/partials/shop/shop-edit.html')
  });
}]);