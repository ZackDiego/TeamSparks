$(async function () {
    const access_token = localStorage.getItem('access_token');
    if (access_token) {
        await getUserWorkspaceInf();
    } else {
        window.location.href = '/login';
    }

    async function getUserWorkspaceInf() {
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

                if (responseBody.data.length > 0) {
                    sessionStorage.setItem('user_workspace_members', JSON.stringify(responseBody.data));
                    const first_workspace_id = responseBody.data[0].workspace_id;
                    window.location.href = '/workspace/' + first_workspace_id;
                } else {
                    alert("New user")

                    // Ask for user avatar
                    $('#addAvatarModal').modal('show');

                    $('#addAvatarForm').submit(async function (event) {
                        event.preventDefault();

                        const access_token = localStorage.getItem('access_token');

                        // Create FormData
                        const formData = new FormData();
                        formData.append('avatarImageFile', $('#userAvatar')[0].files[0]);

                        try {
                            const response = await fetch(`/api/v1/user/avatar`, {
                                method: 'POST',
                                headers: {
                                    'Authorization': 'Bearer ' + access_token
                                },
                                body: formData
                            });

                            const responseData = await response.json();

                            if (response.ok) {
                                // Close the modal after saving
                                $('#addAvatarModal').modal('hide');
                                // Update the user information
                                localStorage.setItem('user', JSON.stringify(responseData.data));
                            } else {
                                console.error(responseData.message);
                            }
                        } catch (error) {
                            alert('Please try again.')
                            console.error('Error:', error);
                        }
                    });

                    // Ask to create workspace after user avatar
                    $('#addAvatarModal').on('hidden.bs.modal', function () {
                        $('#addWorkspaceModal').modal('show');
                    });

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

            } else {
                // Handle login error
                alert(responseBody.message)
            }
        } catch (error) {
            alert(error.message + ', please try again.');
        }
    }
});