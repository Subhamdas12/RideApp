package com.rideApp.RideApp.DTO;

import java.util.Set;

import com.rideApp.RideApp.entities.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String name;

    private String email;

    private Set<Role> roles;
}
