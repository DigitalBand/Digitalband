"use strict";
var pagesApp = angular.module('pagesModule', ['ngRoute', 'ui.router', 'ui.tinymce']);
pagesApp.config(['$stateProvider', function ($stateProvider) {
    function vw(url) {
        return '/assets/javascripts/modules/pages' + url;
    }

    $stateProvider.state('home0', {
        url: '',
        controller: 'PageListController',
        templateUrl: vw('/partials/page-list.html')
    }).state('home1', {
        url: '/',
        controller: 'PageListController',
        templateUrl: vw('/partials/page-list.html')
    }).state('pageEdit', {
        url: '/edit/:pageId',
        controller: 'PageEditController',
        templateUrl: vw('/partials/page-edit.html')
    }).state('pageNew', {
        url: '/new',
        controller: 'PageEditController',
        templateUrl: vw('/partials/page-edit.html')
    });
}]);