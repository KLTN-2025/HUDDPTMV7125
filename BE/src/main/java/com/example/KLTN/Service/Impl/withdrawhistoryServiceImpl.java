package com.example.KLTN.Service.Impl;
import com.example.KLTN.Entity.withDrawHistoryEntity;

import java.util.List;

public interface withdrawhistoryServiceImpl {
 void   saveWithdraw(withDrawHistoryEntity withdraw);
 List<withDrawHistoryEntity> findAllWithdrawHistory();
 withDrawHistoryEntity findByid(Long id);
}
