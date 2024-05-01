package org.example.teamspark.service;

import lombok.extern.apachecommons.CommonsLog;
import org.example.teamspark.data.dto.SignInAndUpDto;
import org.example.teamspark.data.dto.UserDto;
import org.example.teamspark.data.dto.UserWorkspaceMemberDto;
import org.example.teamspark.data.form.SignInForm;
import org.example.teamspark.data.form.SignUpForm;
import org.example.teamspark.exception.EmailAlreadyExistsException;
import org.example.teamspark.exception.UserAuthenticationException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.model.workspace.WorkspaceMember;
import org.example.teamspark.repository.UserRepository;
import org.example.teamspark.repository.WorkspaceMemberRepository;
import org.example.teamspark.util.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@CommonsLog
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final FileUploadService fileUploadService;

    @Value("${jwt.expireTimeAsSec}")
    private long jwtExpireTimeAsSec;

    @Value("${s3.cdn.prefix}")
    private String s3CdnPrefix;

    public UserService(UserRepository userRepository, JwtService jwtService, WorkspaceMemberRepository workspaceMemberRepository, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.fileUploadService = fileUploadService;
    }

    public SignInAndUpDto signUpUser(SignUpForm signUpForm) throws EmailAlreadyExistsException {
        User existUser = userRepository.findUserByEmail(signUpForm.getEmail());
        if (existUser != null) {
            throw new EmailAlreadyExistsException(signUpForm.getEmail() + " is already exist");
        }

        String originalPassword = signUpForm.getPassword();

        // Hashing password
        String encodedPassword = bCryptPasswordEncoder.encode(originalPassword);

        User user = new User();
        user.setName(signUpForm.getName());
        user.setEmail(signUpForm.getEmail());
        user.setPassword(encodedPassword);

        // save new user
        User savedUser = userRepository.save(user);

        // build response object
        SignInAndUpDto signUpDto = new SignInAndUpDto();
        UserDto userDto = UserDto.from(savedUser);
        signUpDto.setAccessToken(jwtService.generateToken(savedUser.getEmail(), userDto.toMap()));
        signUpDto.setAccessExpired(jwtExpireTimeAsSec);
        signUpDto.setUser(userDto);

        return signUpDto;
    }

    public SignInAndUpDto signInUser(SignInForm signInForm) throws UserAuthenticationException {

        User userFound;
        try {
            userFound = userRepository.findUserByEmail(signInForm.getEmail());
        } catch (EmptyResultDataAccessException e) {
            throw new UserAuthenticationException("Invalid credentials. Please check your email and password and try again.");
        }
        // check password matching
        boolean passwordMatch = bCryptPasswordEncoder.matches(signInForm.getPassword(), userFound.getPassword());
        if (passwordMatch) {
            // build response object
            SignInAndUpDto signInDto = new SignInAndUpDto();
            UserDto userDto = UserDto.from(userFound);
            signInDto.setAccessToken(jwtService.generateToken(userFound.getEmail(), userDto.toMap()));
            signInDto.setAccessExpired(jwtExpireTimeAsSec);
            signInDto.setUser(userDto);

            return signInDto;
        } else {
            throw new UserAuthenticationException("Invalid credentials. Please check your email and password and try again.");
        }
    }

    public List<UserWorkspaceMemberDto> getUserWorkspaceMembers(User user) {
        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByUser(user);

        return workspaceMembers.stream().
                map(workspaceMember -> {
                    UserWorkspaceMemberDto dto = new UserWorkspaceMemberDto();

                    dto.setWorkspaceId(workspaceMember.getWorkspace().getId());
                    dto.setWorkspaceName(workspaceMember.getWorkspace().getName());
                    dto.setWorkspaceAvatar(workspaceMember.getWorkspace().getAvatar());

                    dto.setMemberId(workspaceMember.getId());
                    dto.setJoinedAt(workspaceMember.getJoinedAt());
                    return dto;
                }).toList();
    }

    @Transactional
    public User setUserAvatar(User user, MultipartFile avatarImageFile) throws IOException {

        String extension = "." + FileUploadService.getFileExtension(avatarImageFile.getOriginalFilename());

        // random Id
        long randomId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        String imageUploadPath = "userAvatar-" + randomId + extension;

        // save image
        fileUploadService.saveMultipartFile(avatarImageFile, imageUploadPath);

        user.setAvatar(s3CdnPrefix + imageUploadPath);

        return userRepository.save(user);
    }
}
