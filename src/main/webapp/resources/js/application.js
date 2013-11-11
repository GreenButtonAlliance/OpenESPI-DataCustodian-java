$(function() {
    $(".navbar button.btn-navbar").on("click", function() {
        var $nav = $(".nav-collapse");
        if($nav.hasClass("in")) {
            $nav.css("height", "0px").removeClass("in");
        } else {
            $nav.css("height", "auto").addClass("in");
        }
    });
});