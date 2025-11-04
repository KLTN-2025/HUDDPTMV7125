package com.example.KLTN.Service;

import com.example.KLTN.Entity.RoomsEntity;
import com.example.KLTN.Repository.RoomsRepository;
import com.example.KLTN.Service.Impl.RoomsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomsService implements RoomsServiceImpl {
    private final RoomsRepository roomsRepository;
    @Override
    public List<RoomsEntity> findAllRooms() {
        return roomsRepository.findAll();
    }

    @Override
    public RoomsEntity findRoomById(Long id) {
    return roomsRepository.findById(id).orElse(null);
    }


    @Override
    public void saveRooms(RoomsEntity rooms) {
        roomsRepository.save(rooms);
    }
}
