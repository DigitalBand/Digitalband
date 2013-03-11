define(function () {
    return {
        initialize: function () {
            $("#myCarousel").carousel();
            var settings = {
                threshold: 100
            };
            $("img.lazy").lazyload(settings);
            $("img.lazy-brand").lazyload(settings);
        }
    }
});