"use strict";
var citiesApp = angular.module('citiesModule', ['ngRoute', 'ui.router', 'ngCkeditor']);
citiesApp.config(['$stateProvider', function ($stateProvider) {
    function vw(url) {
        return '/assets/javascripts/modules/cities' + url;
    }

    function sw(url) {
        return '/assets/javascripts/modules/shops' + url;
    }

    $stateProvider.state('home0', {
        url: '',
        controller: 'CityListController',
        templateUrl: vw('/partials/city-list.html')
    }).state('home1', {
        url: '/',
        controller: 'CityListController',
        templateUrl: vw('/partials/city-list.html')
    }).state('cityEdit', {
        url: '/edit/:cityId',
        controller: 'CityEditController',
        templateUrl: vw('/partials/city-edit.html')
    }).state('cityNew', {
        url: '/new',
        controller: 'CityEditController',
        templateUrl: vw('/partials/city-edit.html')
    }).state('shopEdit', {
        url: '/city/:cityId/shops/edit/:shopId',
        controller: 'ShopEditController',
        templateUrl: sw('/partials/shop/shop-edit.html')
    }).state('shopNew', {
        url: '/city/:cityId/shops/new',
        controller: 'ShopEditController',
        templateUrl: sw('/partials/shop/shop-edit.html')
    }).state('shopRemove', {
        url: '/city/:cityId',
        controller: 'ShopListController',
        templateUrl: vw('/partials/city-edit.html')
    });
}]);
