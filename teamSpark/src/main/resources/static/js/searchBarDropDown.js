$(document).ready(async function () {
    // searchbar
    searchDropDown();
});


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

    $searchInput.on('click', '.from-tag', function () {
        console.log('click on from tag');
        // Populate dropdown with workspace members
        populateWorkspaceMembers();

    });


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
        const channelRedirect = JSON.parse(sessionStorage.getItem('channelRedirect'));
        window.location.href = `/workspace/${channelRedirect.workspaceId}/search`;
    });
}