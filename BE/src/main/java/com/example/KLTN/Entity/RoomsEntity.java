package com.example.KLTN.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    // Ngày kết thúc booking

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 0, message = "Khuyến mãi không được âm")
    @Max(value = 10, message = "Khuyến mãi không được vượt quá 10%")
    private Double discountPercent;
    private Long id;
    private String Number;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Double price;
    private String image;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;
}
