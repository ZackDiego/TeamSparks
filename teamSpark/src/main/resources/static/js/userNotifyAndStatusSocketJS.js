(function () {
    let socket;
    if (hostName === 'localhost') {
        // Connect to WebSocket server using StompJS
        socket = new SockJS('http://' + hostName + ':8080/notificationWebsocket');
    } else {
        // Connect to WebSocket server using secure WebSocket (wss://)
        socket = new SockJS('https://' + hostName + '/notificationWebsocket');
    }

    const user = JSON.parse(localStorage.getItem('user'));

    // Connect to StompJS over the WebSocket connection
    const stompClient = Stomp.over(socket);

    // Handle socket connection
    stompClient.connect({}, function (frame) {
        console.log('Connected to WebSocket server');

        // Subscribe to user-specific notifications
        stompClient.subscribe('/userNotification/' + user.id, function (notification) {
            renderNotification(JSON.parse(notification.body));
        });
    });

    // Handle socket disconnection
    socket.onclose = function () {
        console.log('Disconnected from WebSocket server');
    };

    function renderNotification(data) {

        // check if user is not the sender & user is not on the channel
        const currentChannelId = parseInt($('#text-messaging-content').attr('data-channel-id'));

        console.log(data.message.message_id.indexName);

        // Extract the channelId from the message_index_name string
        const messageChannelId = data.message.message_id.channelId;
        console.log(messageChannelId);
        if (data.message.from_id !== getMemberId() && currentChannelId !== messageChannelId) {
            // Create a temporary element
            let tempElement = document.createElement('div');

            // Set the HTML content of the temporary element to the message
            tempElement.innerHTML = data.message.content;

            // Extract the text content from the temporary element
            const messageText = tempElement.textContent || tempElement.innerText;

            // find from user avatar
            const membersData = JSON.parse(sessionStorage.getItem('workspaceMembers'));
            // Function to find the user object by ID
            const from_user = membersData.find(user => user.id === data.message.from_id)?.user;

            console.log(data.message);
            // Create the toast HTML
            var toastHtml = `
            <div class="toast" role="alert" aria-live="assertive" aria-atomic="true" 
            data-channel-id=${messageChannelId} data-message-id="${data.message.message_id.documentId}">
                <div class="toast-header">
                    <img src=${from_user.avatar} class="avatar rounded mr-2" alt="...">
                    <strong class="mr-auto">${data.message.from_name}</strong> 
                    <small>${new Date(data.message.created_at).toLocaleString()}</small>
                    <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="toast-body">${messageText}</div>
            </div>`;

            const toast = $(toastHtml);
            $('.toast-container').eq(0).append(toast);

            // remove when close
            $('.toast .close').on('click', function () {
                $(this).closest('.toast').remove();
            });

            messageRedirect(toast);

            // Show the toast
            $(".toast").toast({
                autohide: false
            });
            $('.toast').toast('show');
        }
    }

    $(window).on('beforeunload', function () {
        stompClient.disconnect();
    });

    function messageRedirect(toast) {
        toast.click(function () {
            // Get channel ID and message ID
            const channelId = $(this).data('channel-id');
            const messageId = $(this).data('message-id');

            // Create an object to store channel ID and message ID
            const messageData = {
                channelId: channelId,
                messageId: messageId
            };


            if (window.location.href.includes("/search")) {
                sessionStorage.setItem("messageData", JSON.stringify(messageData));
                window.location.href = `/workspace/${getWorkspaceIdInSearchUrl()}`;
            } else {
                sessionStorage.setItem("messageData", JSON.stringify(messageData));

                // Redirect to the specific message's channel
                $('.details-item').each(function () {
                    if ($(this).data('channel-id') === messageData.channelId) {
                        $(this).click(); // Trigger click event on the channel
                    }
                });
            }

        });
    }
})();



