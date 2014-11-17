(function (app) {
    "use strict";
    SectionEditController.$inject = ['$scope', '$modalInstance', 'section'];
    function SectionEditController($scope, $modalInstance, section) {
        $scope.vm = this;
        this.section = section;
        this.$modalInstance = $modalInstance;
        this.alerts = []
    }

    SectionEditController.prototype = {
        save: function () {
            if(!this.section.name || !this.section.content) {
                this.showRequiredAlert();
                return;
            }
            this.$modalInstance.close(this.section)
        },
        cancel: function() {
            this.$modalInstance.dismiss('cancel');
        },
        showRequiredAlert: function() {
            this.alerts.push({ type: 'danger', message: 'Оба поля являются обязательными для заполнения.' });
        },
        closeAlert: function(index) {
            this.alerts.splice(index, 1);
        }
    };
    app.controller('SectionEditController', SectionEditController);
    return SectionEditController;
}(pagesApp));