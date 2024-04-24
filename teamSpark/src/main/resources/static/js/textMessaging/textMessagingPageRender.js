$(document).ready(async function () {

    // TODO: load the channelId
    let channelId = 1;

    // Fetch message history by channelId
    const url = `/api/v1/channelId/${channelId}/message`;
    const messageHistoryData = await fetchMessageHistoryData(url);

    // Render message history
    renderMessageHistory(messageHistoryData);

    $('#welcome-message').text('Welcome, ' + userInf.name);
    $('.username').text(userInf.name);

    scrollMessageContainerToBottom();
    // // Button setting
    toggleChannelOrChat();
    toggleRightContent();
    toggleSidebarBtn();

    // Initially hide the video call content
    $(".video-call-content").hide();
    $("details").attr("open", true);
});

async function fetchMessageHistoryData(url) {

    const accessToken = localStorage.getItem('access_token');

    if (!accessToken) {
        console.error("Access token not found in local storage. Redirecting to login page.");
        // window.location.href = '/login'; // Redirect to profile page
        alert("please add access token!")
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

    // Sort messages by date
    const messages = data.messages.sort((a, b) => new Date(a.created_at) - new Date(b.created_at));

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

function toggleChannelOrChat() {
    $('.details-item').click(function () {
        // Remove active class from all details-item elements
        $('.details-item').removeClass('active');
        // Add active class to the details-item elements inside the clicked details element
        $(this).addClass('active');
    });
}

function toggleSidebarBtn() {
    $('.sidebar-btn').click(function () {
        // Remove active class from all details-item elements
        $('.sidebar-btn').removeClass('active');
        // Add active class to the details-item elements inside the clicked details element
        $(this).addClass('active');
    });
}


// Add event listener to the video call button
function toggleRightContent() {
    var $videoCallContent = $(".video-call-content");
    var $textMessagingContent = $(".text-messaging-content");

    $(".btn-video-call").click(function () {
        switchToVideoCall();

        // Add event listener to switch back to text messaging when clicked
        $(this).click(function () {
            switchToTextMessaging();
        });
    });

    // Function to switch to video call content
    function switchToVideoCall() {
        $textMessagingContent.hide();
        $videoCallContent.show();
    }

    // Function to switch back to text messaging content
    function switchToTextMessaging() {
        $textMessagingContent.show();
        $videoCallContent.hide();
    }
}

