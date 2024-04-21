// for test, later should get from fetch
const userInf = {
    "name": "Zack",
    "member_id": 1
}

const channelInf = {
    "channel_id": 1,
    "channel_name": "Back-End"
}

localStorage.setItem('user_inf', JSON.stringify(userInf));
localStorage.setItem('channel_inf', JSON.stringify(channelInf));

