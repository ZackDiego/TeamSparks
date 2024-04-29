addMessagingStomp = function (channelIds) {
    const stompClient = new StompJs.Client({
        brokerURL: 'ws://' + hostName + ':8080/textMessagingWebsocket'
    });

    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        for (let channelId of channelIds) {
            stompClient.subscribe('/textMessagingChannel/' + channelId, (result) => {
                console.log("receive message");
                renderMessage(JSON.parse(result.body), channelId);
                scrollMessageContainerToBottom();
            });
        }
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    function setConnected(connected) {
        $("#connect").prop("disabled", connected);
        $("#disconnect").prop("disabled", !connected);
        if (connected) {
            $("#conversation-container").show();
        } else {
            $("#conversation-container").hide();
        }
        $("#conversation").html("");
    }

    function connect() {
        stompClient.activate();
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.deactivate();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function sendMessage($messageEditor) {

        const content = $messageEditor.summernote('code');
        const plainTextContent = $(content).text()

        const containsLink = /(?:http|https):\/\/\S+/i.test(content);

        const channelId = $messageEditor.closest('#text-messaging-content').data('channel-id')

        const user = JSON.parse(localStorage.getItem('user'))
        // send message to websocket endpoint
        stompClient.publish({
            destination: "/websocket/textMessagingEndpoint",
            body: JSON.stringify({
                channel_id: channelId,
                message: {
                    from_id: getMemberId(),
                    from_name: user.name,
                    content: content,
                    plain_text_content: plainTextContent,
                    contain_link: containsLink,
                    file_url: null,
                    image_url: null
                }
            })
        });

        // clear the message editor
        $messageEditor.summernote('code', '');
    }

    function renderMessage(data, channelId) {
        const currentChannelId = $('#text-messaging-content').data('channel-id')
        // check if user is on the page where receive message
        if (channelId === currentChannelId) {
            // Add the messages into the messages container
            const messagesContainer = $('.message-history-container');

            const avatarSrc = '/img/profile.png';
            const avatar = $('<img>').addClass('avatar').attr('src', avatarSrc);
            const fromName = $('<div>').addClass('from-name').text(data.from_name);
            const content = $('<div>').addClass('message-content').html(data.content)
            const timestamp = $('<div>').addClass('timestamp').text(new Date(data.created_at).toLocaleString());

            // Create message container
            const messageDiv = $('<div>').addClass('message-container')
                .append(avatar, fromName, content, timestamp);

            // Append message container to the messages container
            messagesContainer.append(messageDiv);
        } else {
            // if not add notification on corresponding channel sidebar avatar
            const channelReceive = $('.details-item').filter(function () {
                return $(this).data('channel-id') === channelId;
            });

            // Check if the badge already exists
            let badge = channelReceive.find('.notification-badge');
            if (badge.length) {
                // Badge already exists, update the number inside
                let badgeNumber = parseInt(badge.text()) + 1;
                badge.text(badgeNumber);
            } else {
                // Badge doesn't exist, create a new one with the number 1
                channelReceive.append('<span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger notification-badge">1</span>');
            }
        }
    }

    connect();
    $('.btn-send').click(function () {
        console.log("send message");
        const $messageEditor = $(this)
            .closest('.bottom-toolbar')
            .closest('.note-editor')
            .siblings('.message-editor');
        sendMessage($messageEditor);
    });

    $(window).on('beforeunload', function () {
        disconnect();
    });
}