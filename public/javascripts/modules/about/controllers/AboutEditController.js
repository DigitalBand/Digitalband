(function (app) {
    "use strict";
    AboutEditController.$inject = ['$scope', '$stateParams', 'AboutService', '$state'];
    function AboutEditController($scope, $stateParams, aboutService, $state) {
        $scope.vm = this;
        this.aboutService = aboutService;
        this.$state = $state;
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
            if(!this.aboutInfo.about || !this.aboutInfo.legalInfo){
                return;
            }
            this.aboutService.save(this.aboutInfo).then(function () {
                that.$state.go('home1');
            });
        }
    };
    app.controller('AboutEditController', AboutEditController);
    return AboutEditController;
}(aboutApp));