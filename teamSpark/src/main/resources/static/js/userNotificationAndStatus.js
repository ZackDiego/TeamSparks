(function () {
    const stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/notificationWebsocket'
    });

    const userInf = JSON.parse(localStorage.getItem('user_inf'));

    stompClient.onConnect = (frame) => {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/userNotification/' + userInf.user_id, (result) => {
            console.log("receive notification");
            renderNotification(JSON.parse(result.body));
        });
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

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

    function renderNotification(data) {

        console.log(data)
        // Create a temporary element
        var tempElement = document.createElement('div');

        // Set the HTML content of the temporary element to the message
        tempElement.innerHTML = data.message.content;

        // Extract the text content from the temporary element
        const messageText = tempElement.textContent || tempElement.innerText;

        // Create the toast HTML
        var toastHtml = `
            <div class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <strong class="mr-auto">${data.from_user.name}</strong>
                    <small>${new Date(data.message.created_at).toLocaleString()}</small>
                    <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="toast-body">${messageText}</div>
            </div>
        `;

        // Append the toast HTML to the body
        $('.toast-container').eq(0).append(toastHtml);
        // Show the toast

        $(".toast").toast({
            autohide: false
        });
        $('.toast').toast('show');
    }

    $(function () {
        connect();
    });

    $(window).on('beforeunload', function () {
        disconnect();
    });
})();

