// specify emoji img source
// document.emojiSource = 'tam-emoji/img';

// custom button
var BtnCodeBlock = function (context) {
    var ui = $.summernote.ui;

    // create button
    var button = ui.button({
        contents: 'Code block',
        tooltip: 'Code block',
        click: function () {
            $('.message-editor').summernote('editor.pasteHTML', '<pre></pre>');
        }
    });
    return button.render();   // return button as jquery object
}

renderMessageEditor = function () {
    $('.message-editor').each(function () {
        var $messageEditor = $(this);
        $messageEditor.summernote({
            height: 100,
            toolbar: [
                ['style', ['bold', 'italic', 'strikethrough']],
                ['para', ['ul', 'ol']],
                // ['insert', ['link', 'picture', 'video', 'emoji']],
                ['mybutton', ['codeBlock']],
                ['view', ['undo', 'redo']],
            ],
            popover: {
                image: [
                    ['remove', ['removeMedia']]
                ],
            },
            focus: true,
            disableResizeEditor: true,
            buttons: {
                codeBlock: BtnCodeBlock
            },
            callbacks: {
                onInit: function () {
                    // Move the emoji button to the bottom of the toolbar
                    // var toolbar = $messageEditor.siblings('.note-editor').children('.note-toolbar');
                    // var insertToolBox = toolbar.find('.note-insert');
                    // // Create a new div element with the desired class
                    const customToolbar = $('<div class="note-toolbar card-header bottom-toolbar p-2 d-flex justify-content-end align-items-center" role="toolbar"></div>');
                    // // Append the insertToolBox to the customToolbar
                    // customToolbar.append(insertToolBox);

                    // Add send button
                    const sendButton = $('<button type="button" class="note-btn btn btn-sm btn-send mr-2">Send</button>');
                    customToolbar.append(sendButton);

                    // Append the customToolbar after the original toolbar
                    $messageEditor.siblings('.note-editor').children('.note-editing-area').after(customToolbar);
                }
            },
        });

        $messageEditor.summernote('lineHeight', 1);
        $messageEditor.summernote('fontSize', 14);
        // clear the message editor
        $messageEditor.summernote('code', '');
        $('.note-statusbar').hide();
    });
}

