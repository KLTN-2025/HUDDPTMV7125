package com.example.KLTN.Service.Impl;

import com.example.KLTN.Entity.RoomsEntity;

import java.util.List;

public interface RoomsServiceImpl {
    void saveRooms(RoomsEntity rooms);

    List<RoomsEntity> findAllRooms();

    RoomsEntity findRoomById(Long id);
}
