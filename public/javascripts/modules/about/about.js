"use strict";
var aboutApp = angular.module('aboutModule', ['ngRoute', 'ui.router', 'ui.tinymce']);
aboutApp.config(['$stateProvider', function ($stateProvider) {
    function vw(url) {
        return '/assets/javascripts/modules/about' + url;
    }

    $stateProvider.state('home0', {
        url: '',
        controller: 'AboutEditController',
        templateUrl: vw('/partials/about-edit.html')
    }).state('home1', {
        url: '/',
        controller: 'AboutEditController',
        templateUrl: vw('/partials/about-edit.html')
    });
}]);