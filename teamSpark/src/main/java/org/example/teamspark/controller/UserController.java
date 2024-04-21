package org.example.teamspark.controller;

import jakarta.validation.Valid;
import org.example.teamspark.data.DataResponse;
import org.example.teamspark.data.dto.SignInAndUpDto;
import org.example.teamspark.data.dto.UserWorkspaceMemberDto;
import org.example.teamspark.data.form.SignInForm;
import org.example.teamspark.data.form.SignUpForm;
import org.example.teamspark.exception.BadAuthenticationRequestException;
import org.example.teamspark.exception.BadRequestParamException;
import org.example.teamspark.exception.EmailAlreadyExistsException;
import org.example.teamspark.exception.UserAuthenticationException;
import org.example.teamspark.model.user.User;
import org.example.teamspark.service.UserService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/signup", consumes = {"application/json"})
    public ResponseEntity<Object> handleUserSignUp(@RequestBody @Valid SignUpForm request,
                                                   BindingResult bindingResult) throws EmailAlreadyExistsException, BadAuthenticationRequestException, BadRequestParamException {
        // validate request
        if (bindingResult.hasErrors()) {
            // If there are validation errors, construct a response entity with the error details
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            throw new BadAuthenticationRequestException(errorMessage);
        }

        SignInAndUpDto dto = userService.signUpUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new DataResponse<>(dto));
    }

    @PostMapping(value = "/signin", consumes = {"application/json"})
    public ResponseEntity<Object> handleUserSignIn(@RequestBody @Valid SignInForm request,
                                                   BindingResult bindingResult) throws UserAuthenticationException, BadAuthenticationRequestException, BadRequestParamException, EmailAlreadyExistsException {

        // validate request
        if (bindingResult.hasErrors()) {
            // If there are validation errors, construct a response entity with the error details
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            throw new BadAuthenticationRequestException(errorMessage);
        }

        SignInAndUpDto dto = userService.signInUser(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new DataResponse<>(dto));
    }

    @GetMapping(value = "/workspaceMembers")
    public ResponseEntity<Object> getUserWorkspaceMembers() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserWorkspaceMemberDto> dtos = userService.getUserWorkspaceMembers(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new DataResponse<>(dtos));
    }
}
