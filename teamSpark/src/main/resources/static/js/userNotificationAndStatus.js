const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/textMessagingWebsocket'
});
// const userInf = JSON.parse(localStorage.getItem('user_inf'));

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/userNotification/' + userInf.user_id, (result) => {
        console.log("receive notification");
        console.log(JSON.parse(result.body));
    });
};