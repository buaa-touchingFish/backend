package com.touchfish.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
public class HotPaper {
    String id; //文献id
    String title;//文献题目
}
