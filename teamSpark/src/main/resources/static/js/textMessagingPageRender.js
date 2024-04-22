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
        const content = $('<div>').addClass('message-content').text(message.content)
        const timestamp = $('<div>').addClass('timestamp').text(new Date(message.created_at).toLocaleString());

        // Create message container
        const messageDiv = $('<div>').addClass('message-container')
            .append(avatar, fromName, content, timestamp);

        // Append message container to the messages container
        messagesContainer.append(messageDiv);
    });

}

function addInteractiveSelectorButton() {
    // color and size selectors
    $('.variant-color').on('click', '.product-color', function () {
        $(this).addClass('selected').siblings().removeClass('selected');
        updateVariantIndex();
        checkSelection();
    });

    $('.variant-size').on('click', '.product-size', function () {
        $(this).addClass('selected').siblings().removeClass('selected');
        updateVariantIndex();
        checkSelection();
    });

    function checkSelection() {
        if ($('.product-color.selected').length > 0 && $('.product-size.selected').length > 0) {
            $('.product-add-to-cart-button').text('加入購物車').addClass('product-variant-selected');
        }
    }

    function updateVariantIndex() {
        const colorSelected = $('.product-color.selected');
        const sizeSelected = $('.product-size.selected');
        const variants = $('.product-quantity-value').data('variants');
        let variantIndex = -1;

        if (colorSelected.length > 0 && sizeSelected.length > 0) {
            variantIndex = variants.findIndex(variant => variant.color_code === colorSelected.data('color-code') && variant.size === sizeSelected.text());
        }

        $('.product-quantity-value').attr('data-variant-index', variantIndex);
    }

    // quantity selector buttons
    $('.product-quantity-selector')
        .on('click', '#minus', function () {
            let quantity = parseInt($('.product-quantity-value').text());
            if (quantity > 1) {
                $('.product-quantity-value').text(quantity - 1);
            }
        })
        .on('click', '#plus', function () {
            const variants = $('.product-quantity-value').data('variants');
            const currentQuantity = parseInt($('.product-quantity-value').text());
            const variantIndex = parseInt($('.product-quantity-value').attr('data-variant-index'));

            if (variantIndex >= 0 && variantIndex < variants.length) {
                const currentVariant = variants[variantIndex];
                if (currentQuantity < currentVariant.stock) {
                    $('.product-quantity-value').text(currentQuantity + 1);
                } else {
                    alert('Exceeded available stock!');
                }
            }
        });

    // add to cart button
    $('.product').on('click', '.product-variant-selected', function () {
        // Check if access_token exists in local storage
        const accessToken = localStorage.getItem('access_token');
        if (!accessToken) {
            // Redirect to profile page
            window.location.href = '/profile';
        } else {
            // Disable the button to prevent multiple clicks
            $(this).prop('disabled', true);

            // Show creditCardForm.html content
            $('.credit-card-container').show();
            // Scroll to the credit card form
            $('html, body').animate({scrollTop: $(this).offset().top}, 800);
        }
    });
}


function orderCheckout() {
    $('#credit-card-form').on('submit', function (event) {
        event.preventDefault()

        // fix keyboard issue in iOS device
        forceBlurIos()
        const tappayStatus = TPDirect.card.getTappayFieldsStatus()
        console.log(tappayStatus)

        // Check TPDirect.card.getTappayFieldsStatus().canGetPrime before TPDirect.card.getPrime
        if (tappayStatus.canGetPrime === false) {
            alert('can not get prime')
            return
        }

        // Get prime
        TPDirect.card.getPrime(async function (result) {
            if (result.status !== 0) {
                alert('get prime error ' + result.msg)
                return
            }
            alert('get prime 成功，prime: ' + result.card.prime)

            await fetchOrderCheckoutData(result.card.prime);
        })
    })
}


async function fetchOrderCheckoutData(prime) {

    // construct request body
    let requestBody = {};
    requestBody.prime = prime;

    const price = $('.product-price').data('price');
    console.log(price)
    const colorSelected = $('.product-color.selected');
    const qty = parseInt($('.product-quantity-value').text());
    requestBody.order = {
        shipping: "delivery",
        payment: "credit_card",
        subtotal: price * qty,
        freight: 0, // You may need to retrieve freight from somewhere else
        total: price * qty + 0, // Adjust total accordingly
        recipient: {
            name: "Luke",
            phone: "0987654321",
            email: "luke@gmail.com",
            address: "市政府站",
            time: "morning"
        },
        list: [
            {
                id: parseInt($('.product-id').text()),
                name: $('.product-title').text(),
                price: price,
                color: {
                    code: colorSelected.data('color-code'),
                    name: colorSelected.data('color-name')
                },
                size: $('.product-size.selected').text(),
                qty: qty
            }
        ]
    }

    console.log(requestBody)

    // call order checkout api
    try {
        // get access token
        const accessToken = localStorage.getItem('access_token');
        if (!accessToken) {
            console.error("Access token not found in local storage. Redirecting to profile page.");
            window.location.href = '/profile'; // Redirect to profile page
            return {data: null};
        }

        const response = await fetch('api/1.0/order/checkout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(requestBody)
        });

        if (response.ok) {
            console.log("Order checkout successful. Redirecting to thank-you page.");
            window.location.href = '/thankyou'; // Redirect to thank-you page
        } else {
            const responseBody = await response.json();
            console.error("Error fetching product details:", responseBody.message);
            alert("Order checkout failed. Your access token might expired.");
        }
    } catch (error) {
        console.error("Error fetching product details:", error);
        alert("Error fetching product details. Please try again.");
    }
}


