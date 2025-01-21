package com.abu.hotel_management.service.design;

import com.abu.hotel_management.dto.LoginRequest;
import com.abu.hotel_management.dto.Response;
import com.abu.hotel_management.entity.User;

public interface UserService {

    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUserById(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);

}
