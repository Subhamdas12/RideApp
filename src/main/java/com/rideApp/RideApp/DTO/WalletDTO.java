package com.rideApp.RideApp.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private Long Id;
    private UserDTO user;
    private Double balance = 0.0;
    private List<WalletTransactionDTO> transactions;
}
