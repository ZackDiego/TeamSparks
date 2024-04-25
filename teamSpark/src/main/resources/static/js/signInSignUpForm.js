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
            saveUserInfAndRedirect(responseData);
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
            saveUserInfAndRedirect(responseData);
        } else {
            // Handle login error
            alert(responseData.message)
        }
    } catch (error) {
        alert(error.message + ', please try again.')
    }
}


saveUserInfAndRedirect = function (responseData) {
    localStorage.setItem('access_token', responseData.data.access_token);
    localStorage.setItem('user', JSON.stringify(responseData.data.user));

    // redirect to user page
    window.location.href = '/user';
}