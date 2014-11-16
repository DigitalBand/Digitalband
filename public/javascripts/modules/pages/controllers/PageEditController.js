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
            if(!this.page.name) {
                return;
            }
            this.pageService.save(this.page).then(function () {
                that.$state.go('home1');
            });
        },
        addSection: function() {
            if(!this.page.section.name || !this.page.section.content) return
            if (!this.page.sections) this.page.sections = [];
            this.page.section.id = 0;
            this.page.sectionsCount = this.page.sections.length;
            this.page.sections.push(angular.copy(this.page.section));
            this.page.section = {};
        },
        selectSection: function(section) {
            this.page.section = angular.copy(section);
        },
        editSection: function() {
            var section = _.find(this.page.sections, {id: this.page.section.id})
            section.name = this.page.section.name;
            section.content = this.page.section.content;
            this.page.section = {};
        },
        cancelEdit: function() {
            this.page.section = {};
        },
        removeSection: function(section) {
            _.remove(this.page.sections, {name: section.name});
        },
        deliveryNotFilled: function() {
            return !this.city.delivery;
        }
    };
    app.controller('PageEditController', PageEditController);
    return PageEditController;
}(pagesApp));