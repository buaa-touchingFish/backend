package com.touchfish.Dto;

import com.touchfish.Po.Author;
import com.touchfish.Po.ClaimRequest;
import com.touchfish.Po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@AllArgsConstructor
@Data
public class ClaimFormInfo {
    @Setter
    private ClaimRequest claimRequest;

    @Setter
    private User user;

    @Setter
    private Author author;
}
