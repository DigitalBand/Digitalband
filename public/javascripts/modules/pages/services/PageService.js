(function (app) {
    "use strict";
    PageService.$inject = ['$http'];
    function PageService($http) {
        this.$http = $http;
    }

    PageService.prototype = {
        get: function(pageId) {
            return this.$http(jsRoutes.controllers.admin.Page.get(pageId)).then(function(response) {
                return response.data;
            });
        },
        list: function () {
            return this.$http(jsRoutes.controllers.admin.Page.list()).then(function (response) {
                return response.data;
            });
        },
        remove: function (pageId) {
            return this.$http.delete(jsRoutes.controllers.admin.Page.remove(pageId).url);
        },
        update: function (page) {
            return this.$http({
                url: jsRoutes.controllers.admin.Page.update().url,
                method: 'PUT',
                data: page,
                dataType: 'json',
                headers: {
                    "Content-Type": "application/json"
                }
            });
        },
        save: function (page) {
            if (page.id) {
                return this.update(page);
            } else {
                page.id = 0;
                return this.add(page);
            }
        },
        add: function (page) {
            return this.$http({
                url: jsRoutes.controllers.admin.Page.add().url,
                method: 'POST',
                data: page,
                dataType: 'json',
                headers: {
                    "Content-Type": "application/json"
                }
            }).then(function (response) {
                var pageId = response.data;
                page.id = pageId;
                return page;
            });
        }
    };
    app.service('PageService', PageService);
}(pagesApp));