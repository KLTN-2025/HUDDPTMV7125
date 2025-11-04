package com.example.KLTN.Service;

import com.example.KLTN.Entity.HotelEntity;
import com.example.KLTN.Repository.HotelRepository;
import com.example.KLTN.Service.Impl.HotelServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService implements HotelServiceImpl
{
    private final HotelRepository hotelRepository;
    @Override
    public void saveHotel(HotelEntity hotel) {
        hotelRepository.save(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    @Override
    public List<HotelEntity> findAllHotels() {
        return hotelRepository.findAll();
    }

    @Override
    public HotelEntity findHotelById(Long id) {
        return hotelRepository.findById(id).orElse(null);
    }
}
