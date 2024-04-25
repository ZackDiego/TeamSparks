localStorage.setItem('user_inf', JSON.stringify({
    "name": "Zack",
    "member_id": 1,
    "user_id": 1
}));
localStorage.setItem('channel_inf', JSON.stringify({
    "channel_id": 1,
    "channel_name": "Back-End"
}));

const channelInf = JSON.parse(localStorage.getItem('channel_inf'));
const userInf = JSON.parse(localStorage.getItem('user_inf'));