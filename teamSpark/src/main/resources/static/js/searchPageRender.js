$(document).ready(async function () {

    // display user inf
    const user = JSON.parse(localStorage.getItem('user'));
    $('#welcome-message').text('Welcome, ' + user.name);

    const workspace_id = getWorkspaceIdInSearchUrl();
    renderWorkspaceTab(workspace_id);

    const channels = await fetchChannelsByMemberId(getMemberId());
    sessionStorage.setItem("channels", JSON.stringify(channels));

    const searchResultMessages = await fetchSearchResult();

    renderSearchResult(searchResultMessages);
    renderSearchConditions();

    // searchbar
    searchDropDown();
});


function getWorkspaceIdInSearchUrl() {
    // Get the current URL path
    const urlPath = window.location.pathname;

    // Define the pattern for matching the workspace ID in the URL path
    const pattern = /^\/workspace\/(\d+)\/search$/;

    // Execute the regular expression pattern matching on the URL path
    const match = urlPath.match(pattern);

    // Extract the workspace ID from the matched result
    const workspaceId = match ? parseInt(match[1], 10) : null;

    return workspaceId;
}

function getMemberId() {
    const workspace_id = getWorkspaceIdInSearchUrl();

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

async function fetchSearchResult() {

    const access_token = localStorage.getItem('access_token');
    const search = JSON.parse(sessionStorage.getItem('searchBody'));

    console.log(search);
    console.log(JSON.stringify(search));
    try {
        const response = await fetch(`/api/v1/message/search`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + access_token
            },
            body: JSON.stringify(search) // Pass the searchCondition object as JSON string in the body
        });

        const responseData = await response.json();

        // Check if login was successful
        if (response.ok) {
            return responseData.data;
        } else {
            console.error(responseData.message);
        }
    } catch (e) {
        console.error(e);
    }
}


function renderSearchResult(messagesData) {
    // --- Message history container
    const messagesContainer = $('.search-result');
    // Remove all message container
    $('.message-container').remove();
    // Sort messages by date
    const messages = messagesData.sort((a, b) => new Date(b.created_at) - new Date(a.created_at));

    const membersData = JSON.parse(sessionStorage.getItem('workspaceMembers'));

    // Display total number of results
    $('.result-number').text(messages.length);

    $.each(messages, function (index, message) {
        const channelIdString = message.message_id.indexName.replace('channel-', ''); // Remove 'channel-' prefix
        const channelId = parseInt(channelIdString, 10);

        // message channel tag
        const channelName = getChannelNameById(channelId);

        const channelTag = $('<span>').addClass('in-channel').text('# ' + channelName);

        // message
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
                .attr('data-channel-id', channelId)
                .attr('data-message-id', message.message_id.documentId)
                .append(avatar, $('<div>').addClass('message-right').append(messageHeader, content));
        }

        // Create message container
        const messageDiv = createMessageElement(message);

        // Create wrapper for channel tag and message
        const messageWrapper = $('<div>').addClass('message-wrapper');
        messageWrapper.append(channelTag, messageDiv);

        // Append message wrapper to the messages container
        messagesContainer.append(messageWrapper);
    });
}

function getChannelNameById(id) {
    const channels = JSON.parse(sessionStorage.getItem('channels'));
    // Loop through the channels array to find the channel with the matching ID
    for (const channel of channels) {
        if (channel.id === id) {
            return channel.name; // Return the name of the channel
        }
    }
    // Return null if the channel with the given ID is not found
    return null;
}

function renderSearchConditions() {
    const condition = JSON.parse(sessionStorage.getItem('searchCondition'));

    function displaySearchConditions(condition) {

        // Get the search conditions container
        const $searchConditionsContainer = $('.search-conditions');

        // Clear existing content
        $('.search-keyword').empty();
        $searchConditionsContainer.empty();

        // Fill in the search keyword
        $('.search-keyword').text(condition.search_keyword);

        // Display other conditions if not null
        if (condition.from_name) $('<span>').addClass('condition-tag').text(`From: ${condition.from_name}`).appendTo($searchConditionsContainer);
        if (condition.channel_id) $('<span>').addClass('condition-tag').text(`In: ${condition.channel_id}`).appendTo($searchConditionsContainer);
        if (condition.before_date) $('<span>').addClass('condition-tag').text(`Before: ${condition.before_date}`).appendTo($searchConditionsContainer);
        if (condition.after_date) $('<span>').addClass('condition-tag').text(`After: ${condition.after_date}`).appendTo($searchConditionsContainer);
        if (condition.contain_link) $('<span>').addClass('condition-tag').text('Contain link').appendTo($searchConditionsContainer);
        if (condition.contain_image) $('<span>').addClass('condition-tag').text('Contain image').appendTo($searchConditionsContainer);
        if (condition.contain_file) $('<span>').addClass('condition-tag').text('Contain file').appendTo($searchConditionsContainer);
    }

    displaySearchConditions(condition);
}


function searchDropDown() {
    const conditions = ['from', 'in', 'before', 'after', 'during', 'contains'];

    // Get suggestion list container
    const $searchInput = $('.header-search-input');
    const $searchDropdown = $('.search-dropdown');

    // Loop through suggestions array and create HTML elements
    const populateConditions = function (conditions) {
        $searchDropdown.empty();
        conditions.forEach(condition => {
            $('<div>').addClass('search-dropdown-item')
                .append(
                    $('<span>').addClass('condition-tag')
                        .addClass(condition + '-tag')
                        .text(condition))
                .appendTo($searchDropdown);
        })
    }
    populateConditions(conditions);

    // Function to populate dropdown with workspace members
    function populateWorkspaceMembers() {
        // Retrieve workspace members from sessionStorage
        const workspaceMembers = JSON.parse(sessionStorage.getItem('workspaceMembers')) || [];

        $searchDropdown.empty();

        // Create dropdown items for each workspace member
        workspaceMembers.forEach(member => {
            console.log(member);
            const $item = $('<div>').addClass('search-dropdown-item').addClass('workspace-member').data('member-id', member.id);
            const $avatar = $('<img>').addClass('avatar').attr('src', member.user.avatar);
            const $name = $('<span>').addClass('member-name').text(member.user.name);
            $item.append($avatar, $name).appendTo($searchDropdown);
        });

        $searchDropdown.css('display', 'block');
    }

    // dropdown item
    $('.search-dropdown-item').on('click', function () {
        console.log("add dropdown item");
        // Create a contenteditable div
        let contentEditableDiv = $('<div class="search-keyword" contenteditable="true"><br /></div>');

        // Set the HTML content of the input field
        $searchInput.append($(this).html(), contentEditableDiv);
    });

    // Function to show suggestion list when input is focused
    $searchInput.on('focus', function () {
        if ($(this).text().trim() === '') {
            $('.search-dropdown').css('display', 'block');
        }
    });

    // Function to hide suggestion list when input is blurred
    $searchInput.on('blur', function () {
        setTimeout(function () {
            $('.search-dropdown').css('display', 'none');

        }, 100); // Adjust the delay time as needed
    });

    $searchInput.on('input', function () {
        const searchKeyWord = $(this).clone().find('.condition-tag').remove().end().text().trim();

        conditions.forEach(condition => {
            // if match the condition
            if (searchKeyWord.startsWith(condition)) {
                console.log('turn to tag');

                // remove anything except span
                $(this).contents().filter(function () {
                    return this.nodeType !== 1 || this.nodeName !== 'SPAN';
                }).remove();

                let span = $('<span class="condition-tag" contenteditable="true">' + condition + '</span>').addClass(condition + "-tag");

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

    $searchInput.add($searchDropdown).on('click', '.from-tag', function () {
        // Populate dropdown with workspace members
        populateWorkspaceMembers();
        console.log("click from tag");
    });

    // Event listener for clicking on workspace members
    $searchDropdown.on('click', '.workspace-member', function () {
        // Get the name of the clicked workspace member
        const memberId = $(this).data('member-id');
        const memberName = $(this).find('.member-name').text();
        // Find the 'from' tag and insert the member name
        $searchInput.find('.from-tag').text('from: ' + memberName).data('from-id', memberId);
    });

    $searchInput.on('click', '.before-tag, .after-tag, .during-tag', function () {
        const selectTag = $(this);

        // Show the datepicker
        $('#datepicker').datepicker({
            onSelect: function (selectedDate) {
                // Determine whether it's a "before" or "after" tag
                // Determine whether it's a "before", "after", or "during" tag
                let tagType;
                if (selectTag.hasClass('before-tag')) {
                    tagType = 'before';
                } else if (selectTag.hasClass('after-tag')) {
                    tagType = 'after';
                } else if (selectTag.hasClass('during-tag')) {
                    tagType = 'during';
                }

                // Insert the selected date inside the corresponding tag
                $searchInput.find(`.${tagType}-tag`).text(tagType + ': ' + selectedDate);

                // Hide the datepicker
                $('#datepicker').datepicker('destroy');
            }
        }).show();
    });

    $('.header-search-button').click(() => {
        // Extract search keyword
        const searchKeyword = $('.search-keyword').text().trim();

        // Extract other search criteria
        const fromId = $('.from-tag').data('from-id');
        const channelId = $('.in-tag').data('channel-id');
        const beforeDate = $('.before-tag').text().split(': ')[1];
        const afterDate = $('.after-tag').text().split(': ')[1];
        let containLink = null;
        let containImage = null;
        let containFile = null;

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
            "before_date": beforeDate,
            "after_date": afterDate,
            "contain_link": containLink,
            "contain_image": containImage,
            "contain_file": containFile
        };

        // Save search data to session storage
        sessionStorage.setItem('searchBody', JSON.stringify(searchBody));

        // Redirect to the search results page
        window.location.href = `${window.location.href}/search`;
    });
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