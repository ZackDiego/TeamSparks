const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/textChatWebsocket'
});

stompClient.onConnect = (frame) => {
    const group = $("#chat-group").val();

    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/textChatGroup/' + group, (result) => {
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
    stompClient.publish({
        destination: "/websocket/groupTextChat",
        body: JSON.stringify({
            'content': $("#content").val(),
            'from': $("#from").val(),
            'chatGroup': $("#chat-group").val()
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