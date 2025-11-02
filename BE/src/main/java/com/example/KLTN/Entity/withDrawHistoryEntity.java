package com.example.KLTN.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import com.example.KLTN.Entity.WalletsEntity;
import java.time.LocalDateTime;

@Entity
@Table(name = "WithDrawHistory")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class withDrawHistoryEntity {
 public enum Status {
        pending,resolved,refuse

    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private String accountNumber;
    private String bankName;
    private LocalDateTime create_AT;
    private LocalDateTime update_AT;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String accountHolderName;

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @ManyToOne
    @JoinColumn(name = "wallet_id",nullable = false)
    private WalletsEntity walletsEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }



    public LocalDateTime getCreate_AT() {
        return create_AT;
    }

    public void setCreate_AT(LocalDateTime create_AT) {
        this.create_AT = create_AT;
    }

    public LocalDateTime getUpdate_AT() {
        return update_AT;
    }

    public void setUpdate_AT(LocalDateTime update_AT) {
        this.update_AT = update_AT;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public WalletsEntity getWalletsEntity() {
        return walletsEntity;
    }

    public void setWalletsEntity(WalletsEntity walletsEntity) {
        this.walletsEntity = walletsEntity;
    }
}
