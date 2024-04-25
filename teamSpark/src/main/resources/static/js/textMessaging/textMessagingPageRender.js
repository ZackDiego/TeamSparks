$(document).ready(async function () {

    const member_id = getMemberId();

    const channels = await fetchChannelsByMemberId(member_id);

    for (let channel of channels) {
        // Add channel in sideBar
        addChannelInSideBar(channel);

        // Get message history of each channel
        try {
            // Fetch message history by channelId
            const messageHistoryData = await fetchMessageHistoryData(channel.id);
            // Render message history
            renderMessageHistory(messageHistoryData, member_id);
        } catch (e) {
            console.error("Error when fetching message history from" + channel.id + ":", e);
        }
    }

    // display user inf
    const user = localStorage.getItem('user');
    $('#welcome-message').text('Welcome, ' + user.name);
    $('.username').text(user.name);

    scrollMessageContainerToBottom();
    // Button setting
    toggleChannelOrChat();
    toggleSidebarBtn();
    startVideoCall();

    // open the details when entering
    $("details").attr("open", true);
});

async function fetchMessageHistoryData(channel_id) {

    const url = `/api/v1/channelId/${channel_id}/message`;

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

    const responseBody = await response.json();
    if (response.ok) {
        return responseBody.data;
    } else {
        console.error("Error fetching messaging");
        return null;
    }
}

function renderMessageHistory(data, member_id) {

    // Create a new text-messaging-content container
    const textMessagingContent = $('<div class="text-messaging-content d-none"></div>');
    textMessagingContent.attr('data-channel-id', data.channel_id);
    textMessagingContent.attr('data-is-private', data.is_private);

    // Update channel title and chat partner name/avatar based on is_private flag
    const channelTitleContainer = $('<div class="content-header"></div>');
    const channelTitle = $('<div class="channel-title chat-partner-title"></div>');
    if (data.is_private) {
        // Find the private chat partner
        const chat_partner = data.members.find(member => member.id !== member_id);
        channelTitle.html('<img class="chat-partner-avatar" src="' + chat_partner.avatar + '">' + chat_partner.name);
    } else {
        // Use channel name
        channelTitle.text('# ' + data.channel.name);
    }

    // Add buttons to the header
    const contentHeaderBtnContainer = $('<div class="content-header-btn-container"></div>');
    contentHeaderBtnContainer.append('<button class="content-header-btn btn-phone-call"></button>');
    contentHeaderBtnContainer.append('<button class="content-header-btn btn-video-call"></button>');

    // Append channel title and buttons to the header container
    channelTitleContainer.append(channelTitle);
    channelTitleContainer.append(contentHeaderBtnContainer);

    // Create a message history container
    const messagesContainer = $('<div class="message-history-container d-none"></div>');

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

    // Append the header, message history container, and message editor to the text messaging content container
    textMessagingContent.append(channelTitleContainer);
    textMessagingContent.append(messagesContainer);
    textMessagingContent.append('<div class="message-editor"></div>');

    $('.right-content').append(textMessagingContent);
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

function startVideoCall() {
    $('.btn-video-call').click(function () {
        window.location.replace("/channel/" + channelInf.id + "/videoCall");
    });
}


function getWorkspaceIdInUrl() {
    // Get the current URL path
    const urlPath = window.location.pathname;

    // Define the pattern for matching the workspaceId in the URL path
    const pattern = /^\/workspace\/(\w+)$/;

    // Execute the regular expression pattern matching on the URL path
    const match = urlPath.match(pattern);

    // Extract the workspaceId from the matched result
    const workspaceId = match ? parseInt(match[1], 10) : null;

    if (workspaceId !== null) {
        return workspaceId;
    } else {
        window.location.href = '/user';
    }
}

function getMemberId() {
    const workspace_id = getWorkspaceIdInUrl();

    const members = JSON.parse(sessionStorage.getItem('user_workspace_members'));

    // find the memberId that match workspaceId
    const member = members.find(member => member.workspace_id === workspace_id);

    if (member) {
        // If a workspace with the matching workspace_id is found, extract the member_id
        return member.member_id;
    } else {
        alert('User is forbidden from the workspace.');
        // window.location.href = "/user"
    }
}

async function fetchChannelsByMemberId(member_id) {

    const access_token = localStorage.getItem('access_token');

    const response = await fetch(`/api/v1/member/${member_id}/channel`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + access_token
        }
    });

    const responseData = await response.json();

    // Check if login was successful
    if (response.ok) {
        return responseData.data;
    } else {
        console.error(responseData.message);
    }
}

function addChannelInSideBar(channel) {


    // Check if the channel is private
    if (channel.is_private) {

        // Create a new channel element
        const channelElement = $('<div class="chat-partner details-item"></div>');
        channelElement.append(<img className="chat-partner-avatar" src=channel.chatpartner.avatar/>)
        channelElement.text(channel.chatpartner.name);

        // If it's private, add it to the private chat container
        $('#chat-container').append(channelElement);
    } else {
        // If it's not private
        const channelElement = $('<div class="channel-title details-item"></div>');
        channelElement.text(channel.name);
        $('#channel-container').append(channelElement);
    }
}