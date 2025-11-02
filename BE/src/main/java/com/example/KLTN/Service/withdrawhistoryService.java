package com.example.KLTN.Service;


import com.example.KLTN.Entity.withDrawHistoryEntity;
import com.example.KLTN.Repository.withdrawhistoryRepository;
import com.example.KLTN.Service.Impl.withdrawhistoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class withdrawhistoryService implements withdrawhistoryServiceImpl {
    @Override
    public withDrawHistoryEntity findByid(Long id) {
        return withdrawRepository.findById(id).orElse(null);
    }

    @Autowired
    private withdrawhistoryRepository withdrawRepository;
    @Override
    public void saveWithdraw(withDrawHistoryEntity withdraw) {
        withdrawRepository.save(withdraw);
    }

    @Override
    public List<withDrawHistoryEntity> findAllWithdrawHistory() {
        return withdrawRepository.findAll();
    }
}
