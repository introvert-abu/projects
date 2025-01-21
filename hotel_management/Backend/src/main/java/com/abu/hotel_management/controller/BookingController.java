package com.abu.hotel_management.controller;

import com.abu.hotel_management.dto.Response;
import com.abu.hotel_management.entity.Booking;
import com.abu.hotel_management.service.design.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book-room/{roomId}/{userId}")
    public ResponseEntity<Response> saveBookings(
            @PathVariable("roomId") Long roomId, @PathVariable("userId") Long userId,
            @RequestBody Booking bookingRequest) {
        Response response = bookingService.saveBooking(roomId, userId, bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllBookings() {
        Response response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-confirmation-code/{confirmationCode}")
    public ResponseEntity<Response> getBookingByConfirmationCode(@PathVariable("confirmationCode") String confirmationCode) {
        Response response = bookingService.findBookingByConfirmationCode(confirmationCode);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<Response> cancelBooking(@PathVariable("bookingId") Long bookingId) {
        Response response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
