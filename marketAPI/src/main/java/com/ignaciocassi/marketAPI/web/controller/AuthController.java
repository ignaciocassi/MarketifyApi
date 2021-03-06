package com.ignaciocassi.marketAPI.web.controller;

import com.ignaciocassi.marketAPI.domain.dto.AuthenticationRequest;
import com.ignaciocassi.marketAPI.domain.dto.AuthenticationResponse;
import com.ignaciocassi.marketAPI.domain.service.UserRegistratorService;
import com.ignaciocassi.marketAPI.domain.service.UserSigninService;
import com.ignaciocassi.marketAPI.web.exceptions.PasswordNotValidException;
import com.ignaciocassi.marketAPI.web.exceptions.UsernameExistsException;
import com.ignaciocassi.marketAPI.web.exceptions.UsernameNotValidException;
import com.ignaciocassi.marketAPI.web.messages.ResponseStrings;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserSigninService userSigninService;

    @Autowired
    private UserRegistratorService userRegistratorService;

    @PostMapping("/login")
    @ApiOperation("Login to account providing username and password, and get JWT when authenticated.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully logged in."),
            @ApiResponse(code = 403, message = "User login failed.")
    })
    public ResponseEntity<AuthenticationResponse> login(@ApiParam(value = "Body containing username and password in JSON format.") @RequestBody AuthenticationRequest request) {
        try {
            String jwt = userSigninService.loginUsuario(request);
            return new ResponseEntity<>(new AuthenticationResponse(jwt), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(ResponseStrings.USERNAME_PASSWORD_INCORRECT);
        }
    }

    @PostMapping("/register")
    @ApiOperation("Register new account providing desired username and password.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "User successfully registered."),
            @ApiResponse(code = 409, message = "Username is taken."),
            @ApiResponse(code = 406, message = "Username not valid.")
    })
    public ResponseEntity<String> register(@ApiParam(value = "Body containing username and password in JSON format.")@RequestBody AuthenticationRequest request) {
        try {
            userRegistratorService.registerNewUsuario(request);
            return new ResponseEntity<>(ResponseStrings.SUCCESSFULL_REGISTRATION, HttpStatus.CREATED);
        } catch (UsernameExistsException e) {
            throw new UsernameExistsException(ResponseStrings.USERNAME_TAKEN);
        } catch (UsernameNotValidException e) {
            throw new UsernameNotValidException(ResponseStrings.USERNAME_UNACCEPTABLE);
        } catch (PasswordNotValidException e) {
            throw new PasswordNotValidException(ResponseStrings.PASSWORD_UNACCEPTABLE);
        }
    }

}
