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

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return httpResponseUtil.badRequest("User not authenticated");
            }
            String username = auth.getName();
            UsersEntity owner = userService.FindByUsername(username);
            if (hotelImage == null) {
                return httpResponseUtil.badRequest("hoteimage is null");
            }
            HotelEntity hotel = new HotelEntity();
            hotel.setAddress(dto.getAddress());
            hotel.setName(dto.getName());
            hotel.setDescription(dto.getDescription());
            hotel.setOwner(owner);
            hotel.setImage(image.saveFile(hotelImage));
            hotel.setPhone(dto.getPhone());
            hotel.setStatus(HotelEntity.Status.pending);
            List<RoomsEntity> room = new ArrayList<>();
            for (int i = 0; i < roomsImage.size(); i++) {
                if (dto.getRooms().size() != roomsImage.size()) {
                    System.out.println("roomsImage:" + roomsImage.size() + " " + "dto" + dto.getRooms().size());
                    return httpResponseUtil.badRequest("Số lượng rooms và ảnh rooms không khớp");}
                RoomsEntity roomentity = new RoomsEntity();
                roomsDto roomdto = dto.getRooms().get(i);
                roomentity.setHotel(hotel);
                roomentity.setType(RoomsEntity.RoomType.STANDARD);
                roomentity.setPrice(roomdto.getPrice());
                roomentity.setStatus(RoomsEntity.Status.AVAILABLE);
                roomentity.setNumber(roomdto.getNumber());
                roomentity.setImage(image.saveFile(roomsImage.get(i)));
                room.add(roomentity);
            }
            hotel.setRooms(room);
            hotelService.saveHotel(hotel);
            return httpResponseUtil.created("created hotel successfully", hotel);
        } catch (Exception e) {
            return httpResponseUtil.error("Lỗi create ", e);
        }
    }
    @GetMapping("/list")
    public ResponseEntity<Apireponsi<List<HotelEntity>>> findAllHotel() {
        try {
            List<HotelEntity> hotels = hotelService.findAllHotels();
            return httpResponseUtil.ok("findAll Hotel",hotels);

        } catch (Exception e) {
            return httpResponseUtil.error("Lỗi findAllHotel ", e);
        }
    }
}
