(function (app) {
    "use strict";
    PageEditController.$inject = ['$scope', '$modal', 'PageService', '$state'];
    function PageEditController($scope,  $modal, pageService, $state) {
        $scope.vm = this;
        this.pageService = pageService;
        this.$state = $state;
        this.$modal = $modal;
        this.pageId = $state.params.pageId;
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
                this.page = {sections: []};
            }
        },
        save: function () {
            var that = this;
            if(!this.page.name || !this.page.alias) {
                return;
            }
            this.pageService.save(this.page).then(function () {
                that.$state.go('home1');
            });
        },
        addSection: function() {
            var section = {id: 0};
            this.editSection(section);
        },
        editSection: function(section) {
            var that = this;
            var modal = this.$modal.open({
                templateUrl: "editSection.html",
                windowClass: "app-modal-window",
                controller: 'SectionEditController',
                resolve: {
                    section: function() {
                        return angular.copy(section);
                    }
                }
            });

            modal.result.then(function (editedSection) {
                if(editedSection.id > 0) {
                    that.updateSection(editedSection);
                }
                else{
                    that.insertSection(editedSection);
                }
            });
        },
        insertSection: function(section) {
            this.page.sections.push(angular.copy(section));
        },
        updateSection: function(section) {
            var currentSection = _.find(this.page.sections, {id: section.id})
            currentSection.name = section.name;
            currentSection.content = section.content;
        },
        removeSection: function(section) {
            _.remove(this.page.sections, {name: section.name});
        }
    };
    app.controller('PageEditController', PageEditController);
    return PageEditController;
}(pagesApp));