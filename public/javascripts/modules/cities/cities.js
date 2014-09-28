"use strict";
var citiesApp = angular.module('citiesModule', ['ngRoute', 'ui.router', 'ui.tinymce']);
citiesApp.config(['$stateProvider', function ($stateProvider) {
    function vw(url) {
        return '/assets/javascripts/modules/cities' + url;
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
    });
}]);