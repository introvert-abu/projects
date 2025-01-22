package com.abu.hotel_management.utils;

import com.abu.hotel_management.dto.BookingDTO;
import com.abu.hotel_management.dto.RoomDTO;
import com.abu.hotel_management.dto.UserDTO;
import com.abu.hotel_management.entity.Booking;
import com.abu.hotel_management.entity.Room;
import com.abu.hotel_management.entity.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Utils {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateConfirmationCode(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            stringBuilder.append(ALPHANUMERIC_STRING.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }

    public static UserDTO mapUserEntityToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public static RoomDTO mapRoomEntityToRoomDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .roomDescription(room.getRoomDescription())
                .roomType(room.getRoomType())
                .roomPrice(room.getRoomPrice())
                .imageType(room.getImageType())
                .imageData(room.getImageData())
                .imageName(room.getImageName())
                .build();
    }

    public static RoomDTO mapRoomEntityToRoomDTOPlusBookings(Room room) {
        RoomDTO roomDTO = RoomDTO.builder()
                .id(room.getId())
                .roomDescription(room.getRoomDescription())
                .roomType(room.getRoomType())
                .roomPrice(room.getRoomPrice())
                .imageType(room.getImageType())
                .imageData(room.getImageData())
                .imageName(room.getImageName())
                .build();

        if (room.getBookings() != null) {
            roomDTO.setBookings(room.getBookings().stream().map(Utils::mapBookingEntityToBookingDTO).collect(Collectors.toList()));
        }

        return roomDTO;

    }

    public static BookingDTO mapBookingEntityToBookingDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numOfAdults(booking.getNumOfAdults())
                .numOfChildren(booking.getNumOfChildren())
                .totalNumOfGuest(booking.getTotalNumOfGuest())
                .bookingConfirmationCode(booking.getBookingConfirmationCode())
                .build();
    }

    public static UserDTO mapUserEntityToUserDTOPlusBookingAndRoom(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());

        if (!user.getBookings().isEmpty()) {
            userDTO.setBookings(user.getBookings().stream().map(booking -> mapBookingEntityToBookingDTOPlusBookedRoom(booking, false)).collect(Collectors.toList()));
        }

        return userDTO;
    }

    public static BookingDTO mapBookingEntityToBookingDTOPlusBookedRoom(Booking booking, boolean mapUser) {
        BookingDTO bookingDTO = BookingDTO.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numOfAdults(booking.getNumOfAdults())
                .numOfChildren(booking.getNumOfChildren())
                .totalNumOfGuest(booking.getTotalNumOfGuest())
                .bookingConfirmationCode(booking.getBookingConfirmationCode())
                .build();

        if (mapUser) {
            bookingDTO.setUser(mapUserEntityToUserDTO(booking.getUser()));
        }

        if (booking.getRoom() != null) {
            bookingDTO.setRoom(mapRoomEntityToRoomDTO(booking.getRoom()));
        }

        return bookingDTO;
    }

    public static List<UserDTO> mapUserListEntityToUserListDTO(List<User> userList) {
        return userList.stream().map(Utils::mapUserEntityToUserDTO).collect(Collectors.toList());
    }

    public static List<RoomDTO> mapRoomListEntityToRoomListDTO(List<Room> roomList) {
        return roomList.stream().map(Utils::mapRoomEntityToRoomDTO).collect(Collectors.toList());
    }

    public static List<BookingDTO> mapBookingListEntityToBookingListDTO(List<Booking> bookingList) {
        return bookingList.stream().map(Utils::mapBookingEntityToBookingDTO).collect(Collectors.toList());
    }

}
