package com.touchfish.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClaimResultInfo {
    int claimRequestId;
    boolean result;
}
