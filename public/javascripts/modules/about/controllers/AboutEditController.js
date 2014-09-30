(function (app) {
    "use strict";
    AboutEditController.$inject = ['$scope', '$stateParams', 'AboutService', '$state'];
    function AboutEditController($scope, $stateParams, aboutService, $state) {
        $scope.vm = this;
        this.aboutService = aboutService;
        this.$state = $state;
        this.resultSuccess = false;
        this.aboutAlert = false;
        this.legalInfoAlert = false;
        this.initAboutInfo();
    }

    AboutEditController.prototype = {
        initAboutInfo: function () {
            var that = this;
            this.aboutService.get().then(function (aboutInfo) {
                that.aboutInfo = aboutInfo;
            });
        },
        save: function () {
            var that = this;
            this.aboutAlert = !this.aboutInfo.about;
            this.legalInfoAlert = !this.aboutInfo.legalInfo;

            if(this.aboutAlert || this.legalInfoAlert) {
                return;
            }
            this.aboutService.save(this.aboutInfo).then(function () {
                that.resultSuccess = true;
            });
        }
    };
    app.controller('AboutEditController', AboutEditController);
    return AboutEditController;
}(aboutApp));