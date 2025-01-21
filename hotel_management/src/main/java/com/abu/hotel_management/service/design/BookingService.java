package com.abu.hotel_management.service.design;

import com.abu.hotel_management.dto.Response;
import com.abu.hotel_management.entity.Booking;

public interface BookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);

}
