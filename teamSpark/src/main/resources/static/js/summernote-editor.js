// specify emoji img source
document.emojiSource = 'tam-emoji/img';

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


$('.message-editor').summernote({
    height: 100,
    toolbar: [
        // [groupName, [list of button]]
        ['style', ['bold', 'italic', 'strikethrough']],
        ['para', ['ul', 'ol']],
        ['insert', ['link', 'picture', 'video', 'emoji']],
        ['mybutton', ['codeBlock']],
        ['view', ['undo', 'redo', 'codeview', 'help']],
    ],
    popover: {
        image: [
            // ['image', ['resizeFull', 'resizeHalf', 'resizeQuarter', 'resizeNone']],
            // ['float', ['floatLeft', 'floatRight', 'floatNone']],
            ['remove', ['removeMedia']]
        ],
        // link: [
        //   ['link', ['linkDialogShow', 'unlink']]
        // ]
    },
    focus: true,
    disableResizeEditor: true,
    // callbacks: {
    //   onImageLinkInsert: function(url) {
    //     // url is the image url from the dialog
    //     $img = $('<img>').attr({ src: url })
    //     $summernote.summernote('insertNode', $img[0]);
    //   }
    // }
    // icons: {
    //   bold: '<i class="fa fa-bold"></i>',
    //   italic: '<i class="fa fa-italic"></i>',
    //   strikethrough: '<i class="fa fa-strikethrough"></i>'
    //   // Define other custom icons as needed
    // }
    callbacks: {
        onInit: function () {
            // Move the emoji button to the bottom of the toolbar
            var toolbar = $('.note-toolbar');
            var insertToolBox = toolbar.find('.note-insert');
            // Create a new div element with the desired class
            var customToolbar = $('<div class="note-toolbar card-header bottom-toolbar p-2 d-flex justify-content-between align-items-center" role="toolbar"></div>');
            // Append the insertToolBox to the customToolbar
            customToolbar.append(insertToolBox);
            // Append the customToolbar after the original toolbar
            $('.note-editing-area').after(customToolbar);

            // Add send button
            var sendButton = $('<button type="button" class="note-btn btn btn-sm btn-primary btnSend mr-2">Send</button>');
            customToolbar.append(sendButton);
        }
    },


    buttons: {
        codeBlock: BtnCodeBlock
    },

    //   // Add the enter and shiftEnter options
    // enter: '\n', // New line within the code block
    // shiftEnter: '<br>' // Move cursor outside the code block
});
$('.message-editor').summernote('lineHeight', 1)
$('.message-editor').summernote('fontSize', 14)

$('.note-statusbar').hide();


// // summernote.image.link.insert
// $('.message-editor').on('summernote.image.link.insert', function(we, url) {
//   // url is the image url from the dialog
//   $img = $('<img>').attr({ src: url })
//   $summernote.summernote('insertNode', $img[0]);
// });