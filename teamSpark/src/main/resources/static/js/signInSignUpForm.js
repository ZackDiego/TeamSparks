// Function to handle the login request
async function signInUser(email, password) {
    try {
        const response = await fetch('/api/v1/user/signin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const responseData = await response.json();

        // Check if login was successful
        if (response.ok) {
            localStorage.setItem('access_token', responseData.data.access_token);
            localStorage.setItem('user', JSON.stringify(responseData.data.user));

            // channel
            localStorage.setItem('user', JSON.stringify(responseData.data.user));

            window.location.href = '/textMessaging/1';
        } else {
            // Handle login error
            alert(responseData.message)
        }
    } catch (error) {
        alert(error.message + ', please try again.');
    }
}

async function signUpUser(name, email, password) {
    try {
        const response = await fetch('/api/v1/user/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
                email: email,
                password: password
            })
        });

        const responseData = await response.json();

        // Check if login was successful
        if (response.ok) {
            // Save access token to localStorage
            localStorage.setItem('access_token', responseData.data.access_token);
            localStorage.setItem('user', responseData.data.user);

            // TODO: Redirect to textMessaging page
            // window.location.href = '/profile';
        } else {
            // Handle login error
            alert(responseData.message)
        }
    } catch (error) {
        alert(error.message + ', please try again.')
    }
}
