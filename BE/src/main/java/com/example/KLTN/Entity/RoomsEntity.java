package com.example.KLTN.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Rooms")
@Data
public class RoomsEntity {
    public enum RoomType {
        STANDARD,
        DELUXE,
        SUITE,
        SUPERIOR,
        EXECUTIVE,
        FAMILY,
        STUDIO
    }

    public enum Status {
        AVAILABLE,  // còn phòng
        BOOKED      // đã đặt
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String Number;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Double price;
    private String image;
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;
}
