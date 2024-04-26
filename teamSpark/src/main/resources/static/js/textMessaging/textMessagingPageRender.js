$(document).ready(async function () {

    const channels = await fetchChannelsByMemberId(getMemberId());

    for (let channel of channels) {
        // Add channel in sideBar
        addChannelInSideBar(channel);
    }

    renderMessageEditor();

    // display user inf
    const user = JSON.parse(localStorage.getItem('user'));
    $('#welcome-message').text('Welcome, ' + user.name);
    $('.username').text(user.name);

    const workspace_id = getWorkspaceIdInUrl();
    // workspace Inf
    await getWorkspaceInf(workspace_id);

    scrollMessageContainerToBottom();
    // Button setting
    toggleSideBarChannel();
    videoCallButton();
    toggleChannelMember();

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

function renderChannelContent(channel, messageHistory, member_id) {

    // Select text-messaging-content container
    const textMessagingContent = $('.text-messaging-content');
    textMessagingContent.attr('data-channel-id', messageHistory.channel_id);
    textMessagingContent.attr('data-is-private', messageHistory.is_private);

    // --- Channel title
    const channelTitleContainer = $('.content-header');
    const channelTitle = $('.channel-title.chat-partner-title');

    if (messageHistory.is_private) {
        // Find the private chat partner
        const chat_partner = channel.members.find(member => member.id !== member_id);
        channelTitle.html('<img class="chat-partner-avatar" src="' + chat_partner.user.avatar + '">' + chat_partner.user.name); // TODO: replace fixd value
    } else {
        // Use channel name
        channelTitle.text('# ' + channel.name);
    }

    // --- Channel member
    if (!messageHistory.is_private) {
        $('.btn-channel-member').show();

        const channelMemberContainer = $('.channel-member-container');
        // Remove all channel members in container
        $('.channel-member').remove();
        // Populate the channel member container
        for (const member of channel.members) {
            // member
            const memberDiv = $('<div class="channel-member"></div>').attr('data-id', member.id);
            // member avatar
            const avatarImg = $('<img>').addClass('channel-member-avatar').attr('src', member.user.avatar);
            // member name
            const nameSpan = $('<span></span>').text(member.user.name);
            // member status
            switch (member.user.status) {
                case "ONLINE":
                    avatarImg.addClass('online');
                    break;
                case "OFFLINE":
                    avatarImg.addClass('offline');
                    break;
                case "DND":
                    avatarImg.addClass('dnd');
                    break;
                case "INVISIBLE":
                    avatarImg.addClass('invisible');
                    break;
                default:
                    break;
            }
            memberDiv.append(avatarImg, nameSpan);
            // Append the member
            channelMemberContainer.append(memberDiv);
        }
    } else {
        $('.btn-channel-member').hide();
    }

    // --- Message history container
    const messagesContainer = $('.message-history-container');
    // Remove all message container
    $('.message-container').remove();
    // Sort messages by date
    const messages = messageHistory.messages.sort((a, b) => new Date(a.created_at) - new Date(b.created_at));

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

function toggleSideBarChannel() {
    $('.details-item').click(async function () {
        // button display
        $('.details-item').removeClass('active');
        $(this).addClass('active');

        // channel page render
        const channelId = $(this).data('channel-id');
        console.log("channel-" + channelId + " rendering")
        // render channel content
        try {
            // Fetch message history by channelId
            const messageHistory = await fetchMessageHistoryData(channelId);
            // Fetch channel by channelId
            const channel = await fetchChannelById(channelId);

            // Render message history
            renderChannelContent(channel, messageHistory, getMemberId());
        } catch (e) {
            console.error("Error when fetching message history from channel-" + channelId + ":", e);
        }

        addMessagingStomp();
    });
}

function videoCallButton() {
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
        const avatarImg = $('<img class="chat-partner-avatar">').attr('src', '/img/profile2.png');// TODO: replace fixed value
        channelElement.append(avatarImg);
        channelElement.append("Alice");  // TODO: replace fixed value

        channelElement.attr('data-channel-id', channel.id);
        $('#chat-container').append(channelElement);
        console.log("add private channel in left sidebar");
    } else {
        // If it's not private
        const channelElement = $('<div class="channel-title details-item"></div>');
        channelElement.text(channel.name);

        channelElement.attr('data-channel-id', channel.id);
        $('#channel-container').append(channelElement);
        console.log("add channel in left sidebar");
    }
}

function toggleChannelMember() {
    // Add an event listener to the channel member button to toggle the visibility of the container
    $('.btn-channel-member').click(function () {
        $('.channel-member-container').toggleClass('collapse expand');
    });

    $('#collapse-channel-member').click(function () {
        $('.channel-member-container').toggleClass('collapse expand');
    });
}

async function getWorkspaceInf(workspaceId) {

    async function fetchWorkspace() {
        const url = `/api/v1/workspace/${workspaceId}`;

        const accessToken = localStorage.getItem('access_token');

        if (!accessToken) {
            console.error("Access token not found in local storage. Redirecting to login page.");
            // window.location.href = '/login'; // Redirect to login page
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

    const workspace = await fetchWorkspace();

    // name
    $('#workspace-name').text(workspace.name).attr('data-workspace-id', workspace.id);

    // TODO: avatar

    // members
    workspace.members.forEach(function (member) {
        // Create a div element for the member
        var memberDiv = $('<div>', {
            'class': 'member',
            'data-member-id': member.id
        });

        // Add avatar to the div
        var avatarImg = $('<img>', {
            'class': 'avatar',
            'src': member.user.avatar,
            'alt': member.user.name + ' Avatar'
        });
        memberDiv.append(avatarImg);

        // Add member name to the div
        var nameSpan = $('<span>', {
            'class': 'name',
            'text': member.user.name
        });
        memberDiv.append(nameSpan);

        // Add label if member is the creator
        if (member.is_creator) {
            var labelSpan = $('<span>', {
                'class': 'label',
                'text': 'Creator'
            });
            memberDiv.append(labelSpan);
        }

        // Append the member div to the modal body
        $('.modal-body').append(memberDiv);
    });

    $('[data-toggle="popover"]').popover({
        html: true,
        content: function () {
            return $('#notification-content').html();
        }
    });
}


fetchNotifications = async function () {
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

fetchChannelById = async function (channelId) {
    const access_token = localStorage.getItem('access_token');

    const response = await fetch(`/api/v1/channel/${channelId}`, {
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