package com.abu.hotel_management.service.impl;

import com.abu.hotel_management.dto.LoginRequest;
import com.abu.hotel_management.dto.Response;
import com.abu.hotel_management.dto.UserDTO;
import com.abu.hotel_management.entity.User;
import com.abu.hotel_management.exception.CustomException;
import com.abu.hotel_management.repository.UserRepository;
import com.abu.hotel_management.service.design.UserService;
import com.abu.hotel_management.utils.JWTUtils;
import com.abu.hotel_management.utils.Utils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Response register(User user) {
        Response response = new Response();

        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new CustomException(user.getEmail() + " already exists");
            }
            user.setRole("USER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(200);
            response.setMessage("registration successful");
            response.setUser(userDTO);
        } catch (CustomException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while registering user " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new CustomException("User not found"));
            String token = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 days");
            response.setMessage("Login successful");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while trying to log in " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();

        try {
            List<User> users = userRepository.findAll();
            List<UserDTO> userDTOS = Utils.mapUserListEntityToUserListDTO(users);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUserList(userDTOS);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();

        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new CustomException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusBookingAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting history " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response deleteUserById(String userId) {
        Response response = new Response();

        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new CustomException("User not found"));
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("Successful");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting user " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();

        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new CustomException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting user " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDTO);
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting user " + e.getMessage());
        }

        return response;
    }

}
