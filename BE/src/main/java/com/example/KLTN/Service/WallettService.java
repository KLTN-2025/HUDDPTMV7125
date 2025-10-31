package com.example.KLTN.Service;

import com.example.KLTN.Entity.UsersEntity;
import com.example.KLTN.Entity.WalletsEntity;
import com.example.KLTN.Repository.WallersRepository;
import com.example.KLTN.Service.Impl.WalletsServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WallettService implements WalletsServiceImpl {
    @Override
    public WalletsEntity GetWallet(UsersEntity usersEntity) {
        return wallersRepository.findByUser(usersEntity);
    }

    private final WallersRepository wallersRepository;

    public WallettService(WallersRepository wallersRepository) {
        this.wallersRepository = wallersRepository;
    }

    @Override
    public void SaveWallet(WalletsEntity wallet) {
        wallersRepository.save(wallet);
    }
}
