require(["jquery",
    "lib/jquery.lazyload",
    "lib/bootstrap/bootstrap-transition",
    "lib/bootstrap/bootstrap-collapse",
    "lib/bootstrap/bootstrap-carousel"], function ($) {

        $(document).ready(function () {
            $("#myCarousel").carousel();
            var settings = {
                threshold: 100
            };
            $("img.lazy").lazyload(settings);
            $("img.lazy-brand").lazyload(settings);
        });


});