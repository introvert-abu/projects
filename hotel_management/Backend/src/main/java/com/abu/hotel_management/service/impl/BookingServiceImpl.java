package com.abu.hotel_management.service.impl;

import com.abu.hotel_management.dto.BookingDTO;
import com.abu.hotel_management.dto.Response;
import com.abu.hotel_management.entity.Booking;
import com.abu.hotel_management.entity.Room;
import com.abu.hotel_management.entity.User;
import com.abu.hotel_management.exception.CustomException;
import com.abu.hotel_management.repository.BookingRepository;
import com.abu.hotel_management.repository.RoomRepository;
import com.abu.hotel_management.repository.UserRepository;
import com.abu.hotel_management.service.design.BookingService;
import com.abu.hotel_management.utils.Utils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();

        try {

            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check in date must be before check out date ");
            }

            Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException("Room not found "));
            User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found "));

            List<Booking> existingBookings = room.getBookings();

            if (!isRoomAvailable(bookingRequest, existingBookings)) {
                throw new CustomException("Selected room is not available for the selected date range ");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            bookingRequest.setTotalNumOfGuest();
            String confirmationCode = Utils.generateConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(confirmationCode);
            Booking savedBooking = bookingRepository.save(bookingRequest);
            response.setMessage("Successful");
            response.setStatusCode(200);
            response.setBookingConfirmationCode(confirmationCode);
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRoom(savedBooking, true);
            response.setBooking(bookingDTO);

        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error booking room " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() ->
                    new CustomException("Booking not found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRoom(booking, true);
            response.setMessage("Successful");
            response.setStatusCode(200);
            response.setBooking(bookingDTO);
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting booking " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();

        try {
            List<Booking> bookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOS = Utils.mapBookingListEntityToBookingListDTO(bookings);
            response.setMessage("Successful");
            response.setStatusCode(200);
            response.setBookingList(bookingDTOS);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting bookings " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();

        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new CustomException("Booking does not exist "));
            bookingRepository.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("Successful");
        } catch (CustomException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting bookings " + e.getMessage());
        }

        return response;
    }

    private boolean isRoomAvailable(Booking bookingRequest, List<Booking> existingBookings) {

        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}
