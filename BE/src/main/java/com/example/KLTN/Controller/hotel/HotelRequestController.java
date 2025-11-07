package com.example.KLTN.Controller.hotel;

import com.example.KLTN.Config.HTTPstatus.HttpResponseUtil;

import com.example.KLTN.Entity.HotelEntity;
import com.example.KLTN.Entity.RoomsEntity;
import com.example.KLTN.Entity.UsersEntity;
import com.example.KLTN.Service.*;
import com.example.KLTN.dto.Apireponsi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import com.example.KLTN.dto.hotelDto;
import org.springframework.web.multipart.MultipartFile;
import com.example.KLTN.dto.roomsDto;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelRequestController {
    private final HotelGroupService hotelGroupService;
    private final HotelService hotelService;
    private final RoomsService roomsService;
    private final HttpResponseUtil httpResponseUtil;
    private final UserService userService;
    private final Image image;
    @PostMapping(value = "/create")
    public ResponseEntity<Apireponsi<HotelEntity>> createHotel(@RequestPart("hotel") hotelDto dto,
                                                               @RequestPart("hotelImage") MultipartFile hotelImage,
                                                               @RequestPart("roomsImage") List<MultipartFile> roomsImage
    ) {
        return hotelService.createHotel(dto, hotelImage, roomsImage);
    }
    @GetMapping("/list")
    public ResponseEntity<Apireponsi<List<HotelEntity>>> findAllHotel() {
        return hotelService.findAllHotel();
    }
}

