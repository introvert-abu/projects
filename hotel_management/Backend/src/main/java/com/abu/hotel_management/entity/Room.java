package com.abu.hotel_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = "bookings")
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @SequenceGenerator(
            name = "room_seq",
            sequenceName = "room_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "room_seq"
    )
    private Long id;

    private String roomType;
    private BigDecimal roomPrice;
    private String roomDescription;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

    @OneToMany(
            mappedBy = "room",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<Booking> bookings = new ArrayList<>();

}
