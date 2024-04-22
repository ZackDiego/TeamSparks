// const userInf = JSON.parse(localStorage.getItem('user_inf'));
//
// const channelInf = JSON.parse(localStorage.getItem('channel_inf'));


// TODO: load the message history
$(document).ready(async function () {

    // TODO: load the channelId
    let channelId = 1;

    // const urlParams = new URLSearchParams(window.location.search);
    // const id = urlParams.get('id');

    // Fetch message history by channelId
    const url = `/api/v1/channelId/${channelId}/message`;
    const messageHistoryData = await fetchMessageHistoryData(url);

    // Render message history
    renderMessageHistory(messageHistoryData);

    scrollMessageContainerToBottom();
    // // Button setting
    // addInteractiveSelectorButton();
});

async function fetchMessageHistoryData(url) {

    const accessToken = localStorage.getItem('access_token');

    if (!accessToken) {
        console.error("Access token not found in local storage. Redirecting to login page.");
        window.location.href = '/login'; // Redirect to profile page
        return;
    }

    const response = await fetch(url, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`
        }
    });

    let responseBody = null;
    if (!response.ok) {
        console.error("Error fetching messaging");
        return;
    }
    responseBody = await response.json();
    // Check if the fetched data is null
    if (!responseBody.data) {
        return {data: null};
    }

    return responseBody.data;
}

function renderMessageHistory(data) {
    // Update right-container HTML with channel_id and isPrivate
    $('.right-content').attr('data-channel-id', data.channel_id);
    $('.right-content').attr('data-is-private', data.is_private);

    // Add the messages into the messages container
    const messagesContainer = $('.message-history-container');

    const messages = data.messages;
    $.each(messages, function (index, message) {
        const avatarSrc = '/img/profile.png'; // Fixed avatar source for now, you can replace it with actual avatar source
        const avatar = $('<img>').addClass('avatar').attr('src', avatarSrc);
        const fromName = $('<div>').addClass('from-name').text(message.from_name);
        const content = $('<div>').addClass('message-content').html(message.content)
        const timestamp = $('<div>').addClass('timestamp').text(new Date(message.created_at).toLocaleString());

        // Create message container
        const messageDiv = $('<div>').addClass('message-container')
            .append(avatar, fromName, content, timestamp);

        // Append message container to the messages container
        messagesContainer.append(messageDiv);
    });

}


function scrollMessageContainerToBottom() {
    var messageContainer = $('.message-history-container');
    messageContainer.scrollTop(messageContainer[0].scrollHeight);
}