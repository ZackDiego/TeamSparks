const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/textMessagingWebsocket'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/textMessagingChannel/' + $("#channel_id").val(), (result) => {
        showContent(JSON.parse(result.body));
    });
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

function sendMessage() {


    // get User inf from localStorage
    const userInf = JSON.parse(localStorage.getItem('user_inf'));

    const channelInf = JSON.parse(localStorage.getItem('channel_inf'));


    const content = $("#content").val();
    const containsLink = /(?:http|https):\/\/\S+/i.test(content);

    // send message to websocket endpoint
    stompClient.publish({
        destination: "/websocket/textMessagingEndpoint",
        body: JSON.stringify({
            channel_id: channelInf.channel_id,
            from_id: userInf.member_id,
            from_name: userInf.name,
            content: content,
            contain_link: containsLink,
            file_url: null,
            image_url: null
        })
    });
}

function showContent(body) {
    $("#conversation").append("<tr><td>" + body.content + "</td> <td>" + new Date(body.time).toLocaleString() + "</td> </tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendMessage());
});