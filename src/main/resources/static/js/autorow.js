var autoRow = (function($) {

    function addRowWhenNonEmpty($input, containerId, templateId, rowClass, blurClass, delBtnClass) {
        if ($input.val() != '') {
            addRow(containerId, templateId, rowClass, blurClass, delBtnClass);
        }
    }

    function addRow(containerId, templateId, rowClass, blurClass, delBtnClass) {

        var $oldRow = $("#" + containerId).find("." + rowClass).last();

        var template = $('#' + templateId).html();
        $("#" + containerId).append(template);

        if ($oldRow) {
            $oldRow.find("." + blurClass).off('blur');
            var $del = $oldRow.find("." + delBtnClass);
            $del.click(function() {
                $oldRow.remove();
            });
            $del.show();
        }
        var $newBlur = $("#" + containerId).find("." + rowClass).last().find("." + blurClass);
        $newBlur.blur(function() {
            addRowWhenNonEmpty($newBlur, containerId, templateId, rowClass, blurClass, delBtnClass);
        });
        $newBlur.focus();
    }

    return {
        add: function(containerId, templateId, rowClass, blurClass, delBtnClass) {
            addRow(containerId, templateId, rowClass, blurClass, delBtnClass);
        }
    };

}(jQuery));