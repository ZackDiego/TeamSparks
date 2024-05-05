addMessagingStomp = function (channelIds) {
    let socket;
    if (hostName === 'localhost') {
        // Connect to WebSocket server using SocketJS
        socket = new SockJS('http://' + hostName + ':8080/notificationWebsocket');
    } else {
        // Connect to WebSocket server using secure WebSocket (wss://)
        socket = new SockJS('https://' + hostName + '/notificationWebsocket');
    }

    // Connect to StompJS over the WebSocket connection
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        for (let channelId of channelIds) {
            subscribeChannel(stompClient, channelId);
        }
    });

    socket.onclose = function () {
        console.log('Disconnected from WebSocket server');
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

    function sendMessage($messageEditor) {

        // Get the HTML content from the Summernote editor
        const content = $messageEditor.summernote('code');
        // Create a temporary div element using jQuery
        const $tempDiv = $('<div>');
        // Set the HTML content to the div
        $tempDiv.html(content);
        // Get the text content from the div, which will strip HTML tags but preserve line breaks
        const plainTextContent = $tempDiv.text();

        const containsLink = /(?:http|https):\/\/\S+/i.test(content);

        const channelId = parseInt($('#text-messaging-content').attr('data-channel-id'));
        console.log("send message to channel " + channelId);

        const user = JSON.parse(localStorage.getItem('user'))
        const messageJson = JSON.stringify({
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
        });
        stompClient.send("/websocket/textMessagingEndpoint", {}, messageJson);

        // clear the message editor
        $messageEditor.summernote('code', '');
    }

    $('.btn-send').click(function () {
        const $messageEditor = $(this)
            .closest('.bottom-toolbar')
            .closest('.note-editor')
            .siblings('.message-editor');
        sendMessage($messageEditor);
    });

    $(window).on('beforeunload', function () {
        disconnect();
    });

    return stompClient;
}

function subscribeChannel(stompClient, channelId) {
    stompClient.subscribe('/textMessagingChannel/' + channelId, (result) => {
        console.log("receive message from channel " + channelId);
        renderMessage(JSON.parse(result.body), channelId);
        scrollMessageContainerToBottom();
    });

    function renderMessage(data, channelId) {
        const currentChannelId = parseInt($('#text-messaging-content').attr('data-channel-id'));

        // check if user is on the page where receive message
        if (channelId === currentChannelId) {
            // Add the messages into the messages container
            const messagesContainer = $('.message-history-container');

            const membersData = JSON.parse(sessionStorage.getItem('workspaceMembers'));

            function createMessageElement(message) {
                // Function to find the user object by ID
                const from_user = membersData.find(user => user.id === message.from_id)?.user;
                const avatar = $('<img>').addClass('avatar').attr('src', from_user.avatar);

                const fromName = $('<div>').addClass('from-name').text(message.from_name);
                const timestamp = $('<div>').addClass('timestamp').text(new Date(message.created_at).toLocaleString());
                const messageHeader = $('<div>').addClass('message-header').append(fromName, timestamp);

                const content = $('<div>').addClass('message-content').html(message.content);
                // Create message container
                return $('<div>').addClass('message-container')
                    .append(avatar, $('<div>').addClass('message-right').append(messageHeader, content));
            }

            const messageElement = createMessageElement(data);

            function datesAreDifferent(date1, date2) {
                return new Date(date1).toDateString() !== new Date(date2).toDateString();
            }

            // check if the last message date is the same with added date
            const lastMessage = messagesContainer.children('.message-container:last-child');

            const lastMessageTimeStamp = lastMessage.length ?
                lastMessage.find('.message-right .message-header .timestamp').text().split(' ')[0] : '';

            const currentDate = new Date(data.created_at).toLocaleDateString();

            // Check if dates are different and insert date divider
            if (!lastMessage.length || datesAreDifferent(currentDate, lastMessageTimeStamp)) {
                // Create date divider with date text
                const dateDivider = $('<div>').addClass('date-divider')
                    .attr('data-date', currentDate)
                    .text(currentDate);
                messagesContainer.append(dateDivider);
            }

            // Append message container to the messages container
            messagesContainer.append(messageElement);
        } else {
            // if not add badge on corresponding channel sidebar avatar
            const channelReceive = $('.details-item').filter(function () {
                return parseInt($(this).attr('data-channel-id')) === channelId;
            });


            // Check if the badge already exists
            let badge = channelReceive.find('.notification-badge');
            if (badge.length) {
                // Badge already exists, update the number inside
                let badgeNumber = parseInt(badge.text()) + 1;
                badge.text(badgeNumber);
            } else {
                // Badge doesn't exist, create a new one with the number 1
                channelReceive.append('<span class="position-absolute translate-middle badge bg-danger notification-badge">1</span>');
            }
        }
    }
}