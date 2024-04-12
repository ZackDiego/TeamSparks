const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/textChatWebsocket'
});

stompClient.onConnect = (frame) => {
    const from = $("#from").val();

    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/privateTextChat/' + from, function (result) {
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
        destination: "/websocket/privateTextChat",
        body: JSON.stringify({
            'content': $("#content").val(),
            'to': $("#to").val(),
            'from': $("#from").val()
        })
    });
}

function showContent(body) {
    $("#conversation").append("<tr><td>" + body.content + "</td> <td>" + new Date(body.time).toLocaleString() + "</td> </tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendMessage();
    });
});