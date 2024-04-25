$(document).ready(async function () {

    // Check if access token exists
    const accessToken = localStorage.getItem('access_token');

    // if (accessToken) {
    //     // TODO: Redirect to Text messaging Page
    //
    // } else {
    //     // If no access token, redirect to login page
    //     $("#login").show();
    // }
    $("#login").show();

    ToggleSignInSignUpForm();
});

ToggleSignInSignUpForm = function () {

    // Initially hide the sign-up form
    $(".sign-up").hide();

    // Show the sign-up form and hide the sign-in form when "Sign Up" link is clicked
    $("#signUpLink").click(function () {
        $(".sign-in").hide();
        $(".sign-up").show();
    });

    // Show the sign-in form and hide the sign-up form when "Sign In" link is clicked
    $("#signInLink").click(function () {
        $(".sign-up").hide();
        $(".sign-in").show();
    });

    // Event listener for the login form submission
    $("#signInForm").submit(function (event) {
        event.preventDefault();
        // Get user input from form
        const email = $("#signInEmail").val();
        const password = $("#signInPassword").val();
        // Call sign in api
        signInUser(email, password);
    });

    // Event listener for the login form submission
    $("#signUpForm").submit(function (event) {
        event.preventDefault();
        // Get user input from form
        const name = $("#signUpName").val();
        const email = $("#signUpEmail").val();
        const password = $("#signUpPassword").val();
        // Call sign in api
        signUpUser(name, email, password);
    });
}