package com.example.KLTN.Service.Impl;

import com.example.KLTN.Entity.HotelEntity;

import java.util.List;

public interface HotelServiceImpl {
    void saveHotel(HotelEntity hotel);
    void deleteHotel(Long id);
    List<HotelEntity> findAllHotels();
    HotelEntity findHotelById(Long id);
}
