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
});