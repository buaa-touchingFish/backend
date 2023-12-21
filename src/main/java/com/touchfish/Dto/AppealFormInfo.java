package com.touchfish.Dto;

import com.touchfish.Po.Paper;
import com.touchfish.Po.PaperAppeal;
import com.touchfish.Po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Data
public class AppealFormInfo {
    private PaperAppeal paperAppeal;
    private User user;
    private Paper paper;
}
