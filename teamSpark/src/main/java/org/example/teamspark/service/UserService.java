package org.example.teamspark.service;

import org.example.teamspark.data.dto.SignInAndUpDto;
import org.example.teamspark.data.dto.UserDto;
import org.example.teamspark.data.dto.UserWorkspaceMemberDto;
import org.example.teamspark.data.form.SignInForm;
import org.example.teamspark.data.form.SignUpForm;
import org.example.teamspark.exception.EmailAlreadyExistsException;
import org.example.teamspark.exception.UserAuthenticationException;
import org.example.teamspark.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    public SignInAndUpDto signUpUser(SignUpForm signUpForm) throws EmailAlreadyExistsException;

    SignInAndUpDto signInUser(SignInForm signInForm) throws UserAuthenticationException;

    List<UserWorkspaceMemberDto> getUserWorkspaceMembers(User user);

    UserDto setUserAvatar(User user, MultipartFile avatarImageFile) throws IOException;
}
