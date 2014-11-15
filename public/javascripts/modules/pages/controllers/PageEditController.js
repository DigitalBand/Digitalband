(function (app) {
    "use strict";
    PageEditController.$inject = ['$scope', '$stateParams', 'PageService', '$state'];
    function PageEditController($scope, $stateParams, pageService, $state) {
        $scope.vm = this;
        this.pageService = pageService;
        this.$state = $state;
        this.pageId = $stateParams.pageId;
        this.initPage();
    }

    PageEditController.prototype = {
        initPage: function () {
            if (this.pageId) {
                var that = this;
                this.pageService.get(this.pageId).then(function (page) {
                    that.page = page;
                });
            }
            else {
                this.page = {};
            }
        },
        save: function () {
            var that = this;
            if(!this.page.name){
                return;
            }
            this.pageService.save(this.page).then(function () {
                that.$state.go('home1');
            });
        },
        removeSection: function(section) {
            _.remove(this.page.sections, {id: section.id});
        },
        deliveryNotFilled: function() {
            return !this.city.delivery;
        }
    };
    app.controller('PageEditController', PageEditController);
    return PageEditController;
}(pagesApp));