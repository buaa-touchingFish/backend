package com.touchfish.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchfish.Dao.ClaimRequestMapper;
import com.touchfish.Po.ClaimRequest;
import com.touchfish.Service.IClaimRequest;
import org.springframework.stereotype.Service;

@Service
public class ClaimRequestImpl extends ServiceImpl<ClaimRequestMapper, ClaimRequest> implements IClaimRequest {
}
