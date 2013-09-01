function initEdit(imgServiceUrl) {
    //CKEDITOR.replace("description");
    //CKEDITOR.disableAutoInline = true;
    //CKEDITOR.inline( 'editable' );
    //$("#description").wysiwyg();
    $(".existent-image").click(function () {
        var id = $(this).attr("data-imageId");
        $("#image-" + id).after('<input type="hidden" name="deletedImage" value="' + id + '"/> ');
        $("#image-" + id).remove();
        return false;
    });

    $("#title").change(function () {
        var val = $(this).val();
        loadGoogleImages(imgServiceUrl, val, 1);
    });
    loadGoogleImages(imgServiceUrl, $("#title").val(), 1);
}
function addGoogleImage(index) {
    var serviceImageSource = $("#googleimage-" + index);
    var clonedImage = serviceImageSource.clone(true);
    serviceImageSource.addClass("disabled");
    var addImageButton = $(".add-image", serviceImageSource).addClass("hidden");
    clonedImage.attr("id", "clonedimage-" + index);
    $(".google-buttons", clonedImage).empty();
    var removeButton = $("#remove-googleImage-" + index, clonedImage).css({display: "inline-block"});
    $("input", clonedImage).attr("name", "googleimage");
    $(".fileupload").first().before(clonedImage);
    removeButton.click(function () {
        var imageIndex = $(this).attr("data-imageIndex");
        addImageButton.removeClass("hidden");
        serviceImageSource.removeClass("disabled");
        $("#clonedimage-" + imageIndex).remove();
    });
}
function loadGoogleImages(imgServiceUrl, val, pageNumber) {
    if (val.trim() !== "") {
        $.get(imgServiceUrl, { search: val, pageNumber: pageNumber}, function (data) {
            setData(data);
        });
    }
}
function setData(data) {
    $(".google.images").html(data);
    $(".google.images .pagination a").click(function () {
        $.get($(this).attr("href"), function (data) {
            setData(data);
        });
        return false;
    });
    $(".add-image").click(function () {
        var imageIndex = $(this).attr("data-imageIndex");
        addGoogleImage(imageIndex);
        return false;
    });
}