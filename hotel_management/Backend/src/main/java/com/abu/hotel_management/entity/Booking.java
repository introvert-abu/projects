package com.abu.hotel_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString(exclude = {"room", "user"})
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @SequenceGenerator(
            name = "booking_seq",
            sequenceName = "booking_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "booking_seq"
    )
    @Column(name = "booking_id")
    private Long id;

    @NotNull(message = "Check in date is required")
    private LocalDate checkInDate;

    @Future(message = "check out date must be greater than the check in date")
    private LocalDate checkOutDate;

    private int numOfAdults;

    private int numOfChildren;

    private int totalNumOfGuest;

    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void setTotalNumOfGuest() {
        this.totalNumOfGuest = numOfAdults + numOfChildren;
    }
}
