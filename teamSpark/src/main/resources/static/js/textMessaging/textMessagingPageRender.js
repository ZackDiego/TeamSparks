$(document).ready(async function () {

    // display user inf
    const user = JSON.parse(localStorage.getItem('user'));
    $('#welcome-message').text('Welcome, ' + user.name);

    const workspace_id = getWorkspaceIdInUrl();
    renderWorkspaceTab(workspace_id);

    // render user status (at left bottom)
    renderUserStatus(user);

    // workspace Inf
    await fetchAndRenderWorkspaceInf(workspace_id);

    // render channels
    const channels = await fetchChannelsByMemberId(getMemberId());
    sessionStorage.setItem("channels", JSON.stringify(channels));
    addChannelsInSideBarAndModal(channels);

    const channelIds = channels.map(channel => channel.id);
    const stompClient = addMessagingStomp(channelIds);

    renderMessageEditor(stompClient);
    // Button setting
    toggleSideBarChannel();
    videoCallButton();
    toggleChannelMember();
    $('.btn-send').click(function () {
        const $messageEditor = $('.message-editor');

        sendMessageIfNotEmpty(stompClient, $messageEditor);
    });

    workspaceSetting();
    handleCreateChannel(stompClient);
    handleAddChannelMember();

    // searchbar
    searchDropDown();

    // open the details when entering
    $("details").attr("open", true);

    const messageRedirect = JSON.parse(sessionStorage.getItem('messageData'));
    const channelRedirect = JSON.parse(sessionStorage.getItem('channelRedirect'));
    if (messageRedirect) {
        // Redirect to the specific message's channel
        $('.details-item').each(function () {
            if ($(this).data('channel-id') === messageRedirect.channelId) {
                $(this).click(); // Trigger click event on the channel
                return false;
            }
        });
    } else if (channelRedirect) {
        // Redirect to the specific message's channel
        $('.details-item').each(function () {
            if ($(this).data('channel-id') === channelRedirect.channelId) {
                $(this).click(); // Trigger click event on the channel
                return false;
            }
        });
    } else {
        // If messageRedirect doesn't exist, click on the first channel
        if ($('#channel-container .details-item').length) {
            $('#channel-container .details-item:first').click();
        }
    }
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

function findPrivateChatPartner(channel) {
    const member_id = getMemberId();
    return channel.members.find(member => member.id !== member_id);
}

function renderChannelContent(channel, messageHistory) {

    // Select text-messaging-content container
    const textMessagingContent = $('#text-messaging-content');
    textMessagingContent.attr('data-channel-id', messageHistory.channel_id);
    textMessagingContent.attr('data-is-private', messageHistory.is_private);

    // --- Channel title
    const channelTitle = $('.channel-title.chat-partner-title');

    if (messageHistory.is_private) {
        // Find the private chat partner
        const chat_partner = findPrivateChatPartner(channel);
        channelTitle.empty();
        channelTitle.append($('<img>').addClass('chat-partner-avatar').attr('src', chat_partner.user.avatar), chat_partner.user.name);
    } else {
        // Use channel name
        channelTitle.text('# ' + channel.name);
    }

    // --- Channel member
    if (!messageHistory.is_private) {
        $('.btn-channel-member').show();

        const channelMembersList = $('#channelMembersList');
        channelMembersList.empty(); // Clear existing members

        for (const member of channel.members) {
            addChannelMemberToList(member);
        }
        if (!isUserCreator()) {
            const button = $('#channelMembersList .list-group-item button');
            button.prop('disabled', true);
        }

        // render workspaceMember that isn't in the channel
        const $channelMemberSelection = $('#channel-member-selection');
        $channelMemberSelection.empty();

        const workspaceMembers = JSON.parse(sessionStorage.getItem('workspaceMembers')) || [];

        const memberIds = channel.members.map(member => member.id);

        workspaceMembers.forEach(function (member) {
            // skip user own id, and skip existing channel member
            if (member.id === getMemberId() || memberIds.includes(member.id)) {
                return;
            }

            // append to channel member selection
            const img = "<img src='" + member.user.avatar + "' class='chat-partner-avatar'>";
            const option = $('<option>')
                .attr('data-content', img + member.user.name)
                .val(member.id);

            $channelMemberSelection.append(option);
        });
        $channelMemberSelection.selectpicker('refresh');

    } else {
        $('.btn-channel-member').hide();
    }

    // --- Message history container
    const messagesContainer = $('.message-history-container');

    // Remove all message container
    messagesContainer.empty();

    // Sort messages by date
    const messages = messageHistory.messages.sort((a, b) => new Date(a.created_at) - new Date(b.created_at));
    const membersData = JSON.parse(sessionStorage.getItem('workspaceMembers'));

    // Function to create message element
    function createMessageElement(message) {
        // Function to find the user object by ID
        const from_user = membersData.find(user => user.id === message.from_id)?.user;
        const avatar = $('<img>').addClass('avatar').attr('src', from_user.avatar);

        const fromName = $('<div>').addClass('from-name').text(message.from_name);
        const timestamp = $('<div>').addClass('timestamp').text(new Date(message.created_at).toLocaleString());
        const messageHeader = $('<div>').addClass('message-header').append(fromName, timestamp);

        const content = $('<div>').addClass('message-content').html(message.content);

        // Create message container
        return $('<div>').addClass('message-container')
            .attr('data-channel-id', channel.id)
            .attr('data-message-id', message.message_id.documentId)
            .append(avatar, $('<div>').addClass('message-right').append(messageHeader, content));
    }

    // Function to check if dates are different
    function datesAreDifferent(date1, date2) {
        return new Date(date1).toDateString() !== new Date(date2).toDateString();
    }

    // Initialize previous date
    let prevDate = null;
    $.each(messages, function (index, message) {
        const currentDate = new Date(message.created_at).toLocaleDateString();

        // Check if dates are different and insert date divider
        if (!prevDate || datesAreDifferent(currentDate, prevDate)) {
            // Create date divider with date text
            const dateDivider = $('<div>').addClass('date-divider')
                .attr('data-date', currentDate)
                .text(currentDate);
            messagesContainer.append(dateDivider);
        }

        // Create message element
        const messageElement = createMessageElement(message);

        // Append message container to the messages container
        messagesContainer.append(messageElement);

        // Update previous date
        prevDate = currentDate;
    });

    let messageRedirect = JSON.parse(sessionStorage.getItem('messageData'));
    if (!messageRedirect) {
        scrollMessageContainerToBottom();
    } else {
        console.log("redirect to following message")

        const $messageContainer = $('.message-history-container').find('[data-message-id="' + messageRedirect.messageId + '"]');

        $('.message-history-container').animate({
            scrollTop: $messageContainer[0].offsetTop
        }, 500);
        // flash for 4s
        $messageContainer.addClass('flash-background');
        setTimeout(function () {
            $messageContainer.removeClass('flash-background');
        }.bind(this), 4000);

        // remove the message for redirect
        sessionStorage.removeItem('messageData');
    }
}


function scrollMessageContainerToBottom() {
    var messageContainer = $('.message-history-container');
    messageContainer.scrollTop(messageContainer[0].scrollHeight);
}

function toggleSideBarChannel() {
    $('.details-item').click(async function () {
        const channelId = $(this).data('channel-id');

        console.log("channel-" + channelId + " rendering");

        // render channel content
        try {
            // Fetch message history by channelId
            const messageHistory = await fetchMessageHistoryData(channelId);
            // Fetch channel by channelId
            const channel = await fetchChannelById(channelId);

            // Render message history
            renderChannelContent(channel, messageHistory);
        } catch (e) {
            console.error("Error when fetching message history from channel-" + channelId + ":", e);
        }

        // remove badge
        $(this).find('.notification-badge').remove();

        // button display
        $('.details-item').removeClass('active');
        $(this).addClass('active');

        // change channel direct
        sessionStorage.setItem('channelRedirect', JSON.stringify({
            workspaceId: getWorkspaceIdInUrl(),
            channelId: channelId
        }))

        // Remove the toasts of channelId
        $('.toast[data-channel-id="' + channelId + '"]').remove();
    });
}

function videoCallButton() {
    $('.btn-video-call').click(function () {
        const channelId = $('#text-messaging-content').data('channel-id');

        // save channel direct in sessionStorage
        sessionStorage.setItem('channelRedirect', JSON.stringify({
            workspaceId: getWorkspaceIdInUrl(),
            channelId: channelId
        }))

        window.location.replace("/channel/" + channelId + "/videoCall");
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

function addChannelsInSideBarAndModal(channels) {
    for (let channel of channels) {
        const channelModal = $('<div class="list-group-item d-flex justify-content-between align-items-center"></div>');

        const modalEditBtn = $('<div>')
            .append($('<button>', {class: 'btn btn-sm btn-secondary mr-2', type: 'button', text: 'Edit'}))
            .append($('<button>', {class: 'btn btn-sm btn-danger', type: 'button', text: 'Delete'}));

        // Check if the channel is private
        if (channel.is_private) {

            const channelSideBar = $('<div class="chat-partner details-item"></div>');

            // Find the private chat partner
            const chat_partner = findPrivateChatPartner(channel);

            const avatarImg = $('<img>').addClass('chat-partner-avatar').attr('src', chat_partner.user.avatar);
            channelSideBar.attr('data-channel-id', channel.id)
                .attr('data-chat-partner-id', chat_partner.id)
                .append(avatarImg, chat_partner.user.name);
            $('#chat-container').append(channelSideBar);

            channelModal.attr('data-channel-id', channel.id).append($('<div>')
                .append(avatarImg.clone(), chat_partner.user.name), modalEditBtn);
            $('#private-chat-modal-list').append(channelModal);
        } else {
            // If it's not private
            const channelSideBar = $('<div class="channel-item details-item"></div>');

            const hashSpan = $('<span class="channel-title-prefix">#</span>');
            channelSideBar.attr('data-channel-id', channel.id).append(hashSpan, channel.name);
            $('#channel-container').append(channelSideBar);

            channelModal.attr('data-channel-id', channel.id).append($('<div>').append(hashSpan.clone(), channel.name), modalEditBtn);
            $('#channel-modal-list').append(channelModal);
        }
    }
    // if not creator, disable the button
    if (!isUserCreator()) {
        const listItems = $('#channel-modal-list .list-group-item');
        const button = $('#channel-modal-list .list-group-item button');

        listItems.addClass('disabled');
        button.prop('disabled', true);
        $('#btn-create-channel').prop('disabled', true);
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

async function fetchAndRenderWorkspaceInf(workspaceId) {

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
    sessionStorage.setItem('workspaceMembers', JSON.stringify(workspace.members));

    // workspace name
    $('#workspace-name').text(workspace.name).attr('data-workspace-id', workspace.id);

    // add creator label
    if (isUserCreator()) {
        const span = $('<span>').text('Creator');
        const icon = $("<img>").attr("src", "/icons/crown.png").addClass("creator-tag-icon");
        $('.workspace-dropdown').after($('<div>').addClass('creator-tag').append(span, icon));
    }

    // members
    function populateWorkspaceMembers(members) {
        members.forEach(function (member) {
            // Create list item
            const listItem = $('<a>').addClass('list-group-item list-group-item-action d-flex align-items-center')
                .attr({
                    'href': '#',
                    'data-member-id': member.id
                });
            const avatarImg = $('<img>').addClass('avatar mr-3').attr({'src': member.user.avatar});
            const memberDetails = $('<div>').addClass('member-details d-flex align-items-center justify-content-between');
            const nameSpan = $('<span>').addClass('name').text(member.user.name);

            // Append name span to member details container
            memberDetails.append(nameSpan);

            // Check if member is the creator and add label image
            if (member.is_creator) {
                const labelImg = $('<img>').addClass('creator-label')
                    .attr({
                        'src': '/icons/crown.png'
                    });
                memberDetails.append(labelImg);
            }

            listItem.append(avatarImg, memberDetails);
            // append to workspaceMember list
            $('.workspace-member-list').append(listItem);
        });

        // Extracting existing chat partner IDs
        const existingChatPartnerIds = [];
        $('#chat-container .chat-partner.details-item').each(function () {
            existingChatPartnerIds.push($(this).data('chat-partner-id'));
        });


        // Populate create private chat modal
        members.forEach(function (member) {
            // skip user his own id
            if (member.id === getMemberId()) {
                return;
            }

            // need to check if private chat already
            const img = "<img src='" + member.user.avatar + "' class='chat-partner-avatar'>";

            // Disable existing chat partner
            let option;
            if (existingChatPartnerIds.includes(member.id)) {
                const disableText = "<small class='text-muted'>Chat already exists</small>";
                option = $('<option>')
                    .attr('data-content', img + member.user.name + disableText)
                    .val(member.id)
                    .prop('disabled', true);
            } else {
                option = $('<option>')
                    .attr('data-content', img + member.user.name)
                    .val(member.id);
            }
            $('#chat-partner-selection').append(option);
        });
        $('#chat-partner-selection').selectpicker('refresh');
    }

    populateWorkspaceMembers(workspace.members);


    $('[data-toggle="popover"]').popover({
        html: true,
        content: function () {
            return $('#notification-content').html();
        }
    });
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


function searchDropDown() {
    const conditions = ['from', 'in', 'before', 'after'];

    // Get suggestion list container
    const $searchInput = $('.header-search-input');
    const $searchDropdown = $('.search-dropdown');

    function removeHash() {
        let text = $searchInput.html();
        text = text.replace(/<span>(.*?)<\/span>/g, function (match) {
            return match.replace(/#/g, '');
        });
        $searchInput.html(text);
    }

    // Loop through suggestions array and create HTML elements
    const populateConditions = function (conditions) {
        $searchDropdown.empty();
        conditions.forEach(condition => {
            $('<div>').addClass('search-dropdown-item')
                .click(function () {
                    removeHash();

                    let contentEditableDiv = $('<div class="search-keyword" contenteditable="true"><br /></div>');

                    $searchInput.append($(this).html(), contentEditableDiv);
                })
                .append(
                    $('<span>').addClass('condition-tag')
                        .addClass(condition + '-tag')
                        .text("#" + condition))
                .appendTo($searchDropdown);
        })
    }

    // Function to populate dropdown with workspace members
    function populateWorkspaceMembers() {
        // Retrieve workspace members from sessionStorage
        const workspaceMembers = JSON.parse(sessionStorage.getItem('workspaceMembers')) || [];

        $searchDropdown.empty();

        // Create dropdown items for each workspace member
        workspaceMembers.forEach(member => {
            const $item = $('<div>').addClass('search-dropdown-item').addClass('workspace-member').data('member-id', member.id);
            const $avatar = $('<img>').addClass('avatar').attr('src', member.user.avatar);
            const $name = $('<span>').addClass('member-name').text(member.user.name);
            $item.append($avatar, $name).appendTo($searchDropdown);

            // Event listener for clicking on workspace members
            $item.on('click', function () {
                console.log('pick member');
                // Get the name of the clicked workspace member
                const memberId = $(this).data('member-id');
                const memberName = $(this).find('.member-name').text();
                // Find the 'from' tag and insert the member name
                $searchInput.find('.from-tag').text('#from: ' + memberName).data('from-id', memberId);
            });
        });

        $searchDropdown.css('display', 'block');
    }

    // Function to populate dropdown with channels
    function populateChannels() {
        // Retrieve channels from sessionStorage
        const channels = JSON.parse(sessionStorage.getItem('channels')) || [];
        channels.sort((a, b) => {
            return a.is_private - b.is_private;
        });
        $searchDropdown.empty();

        // Create dropdown items for each workspace member
        channels.forEach(channel => {
            const $item = $('<div>').addClass('search-dropdown-item').addClass('channel-dropdown-item').data('channel-id', channel.id);

            if (channel.is_private) {
                $item.data('is_private', true);
                // Find the private chat partner
                const chat_partner = findPrivateChatPartner(channel);
                const avatarImg = $('<img>').addClass('chat-partner-avatar').attr('src', chat_partner.user.avatar);
                const $name = $('<span>').addClass('channel-name').text("Chat with " + chat_partner.user.name);
                $item.attr('data-channel-id', channel.id)
                    .append(avatarImg, $name);

            } else {
                // If it's not private
                $item.data('is_private', false);
                const hashSpan = $('<span class="channel-title-prefix">#</span>');
                const $name = $('<span>').addClass('channel-name').text(channel.name);
                $item.attr('data-channel-id', channel.id).append(hashSpan, $name);
            }

            $item.appendTo($searchDropdown);

            // Event listener for clicking on channels
            $item.on('click', function () {
                console.log('pick channel');
                // Get the name of the clicked workspace member
                const channelId = $(this).data('channel-id');
                const channelName = $(this).find('.channel-name').text();

                // Find the 'in' tag and insert the channel name
                $searchInput.find('.in-tag').text('#in: ' + channelName).data('channel-id', channelId);
            });
        });

        $searchDropdown.css('display', 'block');
    }

    // Function to show suggestion list when input is focused
    $searchInput.on('keyup', function () {
        // Get the text content of the search input excluding the content within span elements
        let text = $searchInput.clone()
            .find('span').remove().end().text().trim();
        console.log(text);
        // Count the occurrences of '#' outside the span
        let hashCount = text.split('#').length - 1;

        if (hashCount === 1) {
            populateConditions(conditions);
            $('.search-dropdown').css('display', 'block');
        } else {
            $('.search-dropdown').css('display', 'none');
        }
    });

    // Function to hide suggestion list when input is blurred
    $searchInput.on('blur', function () {
        setTimeout(function () {
            $('.search-dropdown').css('display', 'none');

        }, 100); // Adjust the delay time as needed
    });

    // placeholder
    const $placeholder = $('.placeholder');
    $searchInput.on('focus input', function () {
        if ($(this).text().trim() === '') {
            // Show the placeholder
            $placeholder.show();
        } else {
            // Hide the placeholder
            $placeholder.hide();
        }
    });

    $searchInput.on('input', function () {

        const searchKeyWord = $(this).clone().find('.condition-tag').remove().end().text().trim();

        conditions.forEach(condition => {
            // if match the condition
            if (searchKeyWord.startsWith("#" + condition)) {

                // remove anything except span
                $(this).contents().filter(function () {
                    return this.nodeType !== 1 || this.nodeName !== 'SPAN';
                }).remove();

                let span = $('<span class="condition-tag" contenteditable="true">' + '#' + condition + '</span>').addClass(condition + "-tag");

                // Create a contenteditable div
                let contentEditableDiv = $('<div class="search-keyword" contenteditable="true"><br /></div>');
                // Append the span and contenteditable div
                $(this).append(span, contentEditableDiv);

                // Focus on the contenteditable div
                setTimeout(function () {
                    contentEditableDiv.focus();
                }, 500);
            }
        });
    });

    // ---  Conditions Tag Click Event
    // from
    $searchInput.on('click', '.from-tag', function () {
        console.log('click on from tag');
        // Populate dropdown with workspace members
        populateWorkspaceMembers();

    });

    // in
    $searchInput.on('click', '.in-tag', function () {
        console.log('click on in tag');
        // Populate dropdown with workspace members
        populateChannels();
    });

    // before, after
    $searchInput.on('click', '.before-tag, .after-tag', function () {
        const selectTag = $(this);

        // Show the datepicker
        $('#datepicker').datepicker({
            onSelect: function (selectedDate) {
                // Determine whether it's a "before" or "after" tag
                let tagType;
                if (selectTag.hasClass('before-tag')) {
                    tagType = 'before';
                } else if (selectTag.hasClass('after-tag')) {
                    tagType = 'after';
                }

                // Insert the selected date inside the corresponding tag
                $searchInput.find(`.${tagType}-tag`).text('#' + tagType + ': ' + selectedDate);

                // Hide the datepicker
                $('#datepicker').datepicker('destroy');
            }
        }).show();
    });

    $('.header-search-button').click(() => {
        // Extract search keyword
        let searchKeyword = $('.search-keyword').text().trim();
        const headerSearchInput = $('.header-search-input');

        if (searchKeyword === "" && headerSearchInput.find('span').length === 0) {
            searchKeyword = $('.header-search-input').text().trim();
        }

        // Extract other search criteria
        const fromName = headerSearchInput.find('.from-tag').text().split(': ')[1] || null;
        const fromId = headerSearchInput.find('.from-tag').data('from-id') || null;
        const channelId = headerSearchInput.find('.in-tag').data('channel-id') || null;
        const beforeDate = headerSearchInput.find('.before-tag').text().trim().split(': ')[1];
        const afterDate = headerSearchInput.find('.after-tag').text().trim().split(': ')[1];
        let containLink = null;
        let containImage = null;
        let containFile = null;

        function formatDate(dateString) {
            if (!dateString) return null;
            const [month, day, year] = dateString.replace(/before|after/g, '').split('/');
            return `${year}-${month}-${day}T00:00:00`;
        }

        // Wrap dates with single quotes
        const formattedBeforeDate = beforeDate ? formatDate(beforeDate) : null;
        const formattedAfterDate = afterDate ? formatDate(afterDate) : null;

        // Extract contain condition
        $('.contain-tag').each(function () {
            const containText = $(this).text().toLowerCase();
            if (containText.includes('link')) {
                containLink = true;
            } else if (containText.includes('image')) {
                containImage = true;
            } else if (containText.includes('file')) {
                containFile = true;
            }
        });

        // Form the JSON object
        const searchBody = {
            "search_keyword": searchKeyword,
            "from_id": fromId,
            "channel_id": channelId,
            "before_date": formattedBeforeDate,
            "after_date": formattedAfterDate,
            "contain_link": containLink,
            "contain_image": containImage,
            "contain_file": containFile
        };

        const searchCondition = {
            "search_keyword": searchKeyword,
            "from_name": fromName, // Change from "from_id" to "from_name"
            "channel_id": channelId,
            "before_date": beforeDate,
            "after_date": afterDate,
            "contain_link": containLink,
            "contain_image": containImage,
            "contain_file": containFile
        };

        // Save search data to session storage
        sessionStorage.setItem('searchBody', JSON.stringify(searchBody));
        sessionStorage.setItem('searchCondition', JSON.stringify(searchCondition));

        // Redirect to the search results page
        window.location.href = `${window.location.href}/search`;
    });
}

// Handle click event for btn-add-workspace
function workspaceSetting() {
    $('.btn-add-workspace').click(function () {
        // Show the modal when the button is clicked
        $('#addWorkspaceModal').modal('show');
    });

    // saveWorkspaceBtn
    $('#addWorkspaceForm').submit(async function (event) {
        event.preventDefault();

        const access_token = localStorage.getItem('access_token');

        // Create FormData
        const formData = new FormData();
        formData.append('name', $('#workspaceName').val().trim());
        formData.append('avatarImageFile', $('#workspaceAvatar')[0].files[0]);

        try {
            const response = await fetch(`/api/v1/workspace`, {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + access_token
                },
                body: formData
            });

            const responseData = await response.json();

            if (response.ok) {
                // Close the modal after saving
                $('#addWorkspaceModal').modal('hide');

                // update the user workspace member
                await getUserWorkspaceInf();

                // Redirect to created workspace
                window.location.href = `/workspace/${responseData.data.id}`;
            } else {
                console.error(responseData.message);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    });
}


async function getUserWorkspaceInf() {

    const access_token = localStorage.getItem('access_token');
    try {
        // Function to fetch the first workspace ID associated with the user

        const response = await fetch('/api/v1/user/workspaceMembers', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + access_token
            }
        });

        const responseBody = await response.json();

        if (response.ok) {
            const first_workspace_id = responseBody.data[0].workspace_id;

            if (first_workspace_id) {
                sessionStorage.setItem('user_workspace_members', JSON.stringify(responseBody.data));
                window.location.href = '/workspace/' + first_workspace_id;
            } else {
                // TODO: show user page for creating workspace
                alert("User doesn't have any workspace")
            }

        } else {
            // Handle login error
            alert(responseBody.message)
        }
    } catch (error) {
        alert(error.message + ', please try again.');
    }
}

function renderUserStatus(user) {
    // Create an image element for the user avatar
    const avatarImg = $('<img>').addClass('avatar-image').attr('src', user.avatar).attr('alt', 'User Avatar');

    // Avatar
    let status = user.status ? user.status.toLowerCase() : 'online';
    $('.user-avatar').addClass(status).append(avatarImg);

    $('.username').text(user.name);

    $('.user-status').text(status[0].toUpperCase() + status.substring(1));
}

function renderWorkspaceTab(workspace_id) {

    const workspaces = JSON.parse(sessionStorage.getItem("user_workspace_members"));
    console.log(workspaces);

    for (let workspace of workspaces) {
        // Create a button element for the workspace
        const button = $('<button>')
            .addClass('workspace-card')
            .attr('data-workspace-id', workspace.workspace_id)
            .attr('data-member-id', workspace.member_id)
            .css('background-image', 'url(' + workspace.workspace_avatar + ')')
            .on('click', function () {
                window.location.href = '/workspace/' + workspace.workspace_id; // Redirect to workspace
            });
        // Check if the workspace_id matches the input workspace_id
        if (workspace.workspace_id === workspace_id) {
            button.addClass('active'); // Add 'active' class to the button
        }
        // Prepend the button to the workspaceTab
        button.prependTo('#workspaces-tab');
    }
}

$('#btn-create-channel, #btn-create-private-chat').click(function () {
    // Clear the form
    $('#createChannelForm')[0].reset();
    // Open the modal
    // $('#manageChannelModal').modal('show');
    $('#manageChannelModal').modal('hide');
});

function handleCreateChannel(stompClient) {
    $('#createChannelForm').submit(async function (event) {
        // Prevent the default form submission
        event.preventDefault();

        const create_channel_id = await fetchCreateChannel($('#channelName').val(), false);
        if (create_channel_id) {
            await fetchAndRenderChannelById(create_channel_id, stompClient);
        }
    });

    // create private form submit
    $('#createPrivateForm').submit(async function (event) {
        // Prevent the default form submission behavior
        event.preventDefault();

        const create_channel_id = await fetchCreateChannel("private", true);
        console.log('Create channel: ' + create_channel_id);
        const selectedMemberId = $('#chat-partner-selection').val();
        console.log('Add selected chat partner: ', selectedMemberId);

        await fetchAddMember(create_channel_id, selectedMemberId);

        if (create_channel_id) {
            await fetchAndRenderChannelById(create_channel_id, stompClient);
            $('#createPrivateChatModel').modal('hide');
        }
    });
}

async function fetchAndRenderChannelById(id, stompClient) {
    try {
        const accessToken = localStorage.getItem('access_token');

        // Fetch the channel data by its ID
        options = {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        };

        const response = await fetch(`/api/v1/channel/${id}`, options);

        if (!response.ok) {
            throw new Error('Failed to fetch channel data');
        }

        // Parse the channel data
        const requestBody = await response.json();

        const channelData = requestBody.data;

        // Add the channel to the sidebar
        let channelElement;
        if (channelData.is_private) {
            channelElement = $('<div class="chat-partner details-item"></div>');
            const chat_partner = findPrivateChatPartner(channelData);
            const avatarImg = $('<img>').addClass('chat-partner-avatar').attr('src', chat_partner.user.avatar);
            channelElement
                .attr('data-chat-partner-id', chat_partner.id)
                .attr('data-channel-id', channelData.id)
                .append(avatarImg, chat_partner.user.name);
            $('#chat-container').append(channelElement);
        } else {
            channelElement = $('<div class="channel-item details-item"></div>');
            channelElement.attr('data-channel-id', channelData.id);
            const hashSpan = $('<span class="channel-title-prefix">#</span>');
            channelElement.append(hashSpan);
            channelElement.append(channelData.name);

            $('#channel-container').append(channelElement);
        }
        toggleSideBarChannel();
        channelElement.click();
        subscribeChannel(stompClient, channelData.id);
        // close the modal
        $('#createChannelModal').modal('hide');
    } catch (error) {
        // Handle any errors
        console.error('Error fetching channel by id: ', error);
        alert('Error fetching channel by id: ' + error);
    }
}

async function fetchCreateChannel(channel_name, is_private) {
    try {
        // Get the form data
        const formData = {
            workspace_id: getWorkspaceIdInUrl(),
            name: channel_name,
            is_private: is_private
        };

        const accessToken = localStorage.getItem('access_token');

        if (!accessToken) {
            console.error("Access token not found in local storage. Redirecting to login page.");
            // window.location.href = '/login'; // Redirect to profile page
            alert("please add access token!")
            return;
        }

        // Fetch options
        let options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(formData)
        };

        // Send the fetch request to create the channel
        const response = await fetch('/api/v1/channel', options);

        if (!response.ok) {
            throw new Error('Create channel failed');
        }

        // Parse the response JSON data
        const responseBody = await response.json();
        return responseBody.data.id;
    } catch (error) {
        // Handle any errors
        console.error('Error creating channel: ', error);
        alert('Error creating channel: ' + error);
    }
}

async function fetchAddMember(channelId, memberId) {
    try {
        // Get the form data
        const accessToken = localStorage.getItem('access_token');

        if (!accessToken) {
            console.error("Access token not found in local storage. Redirecting to login page.");
            // window.location.href = '/login'; // Redirect to profile page
            alert("please add access token!")
            return;
        }

        // Fetch options
        let options = {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        };

        // Send the fetch request to create the channel
        const response = await fetch(`/api/v1/channel/${channelId}/member/${memberId}`, options);

        if (!response.ok) {
            throw new Error('Add member in private chat failed: ' + response);
        }
    } catch (error) {
        // Handle any errors
        console.error('Error adding member in private chat: ', error.message);
        alert('Error adding member in private chat: ' + error.message);
    }
}

function isUserCreator() {
    const members = JSON.parse(sessionStorage.getItem('workspaceMembers'));
    const userMember = members.find(member => member.id === getMemberId());
    return userMember.is_creator;
}

function handleAddChannelMember() {
    $('#addMemberForm').submit(async function (event) {
        event.preventDefault();

        const channelId = $('#text-messaging-content').data('channel-id');

        const $channelMemberSelection = $('#channel-member-selection');
        const selectedMemberId = $channelMemberSelection.val();

        await fetchAddMember(channelId, selectedMemberId);

        const members = JSON.parse(sessionStorage.getItem('workspaceMembers'));
        const addedMember = members.find(member => member.id === parseInt(selectedMemberId, 10));

        // add to channel member list
        addChannelMemberToList(addedMember);

        if (!isUserCreator()) {
            const button = $('#channelMembersList .list-group-item button');
            button.prop('disabled', true);
        }

        // remove from selection
        $channelMemberSelection.find('option').filter(function () {
            return $(this).val() === selectedMemberId;
        }).remove();

        $channelMemberSelection.selectpicker('refresh');

        $('#channelMembersModal').modal('hide');
    })
}


function addChannelMemberToList(member) {
    const listItem = $('<a class="list-group-item d-flex justify-content-between align-items-center"></a>')
        .attr('data-member-id', member.id);

    const avatarImg = $('<img>').addClass('chat-partner-avatar').attr('src', member.user.avatar);
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
    const nameSpan = $('<span></span>').text(member.user.name);
    const deleteBtn = $('<button class="btn btn-sm btn-danger">Delete</button>');

    listItem.append($('<div>').append(avatarImg, nameSpan), deleteBtn).appendTo($('#channelMembersList'));
}