package com.touchfish.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterSuccess {
    String jwt;
    Integer uid;
}
