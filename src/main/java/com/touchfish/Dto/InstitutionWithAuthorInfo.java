package com.touchfish.Dto;

import com.touchfish.Po.Author;
import com.touchfish.Po.Institution;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class InstitutionWithAuthorInfo {
    @Setter
    private Institution institution;
    @Setter
    private List<Author> authorList;
}
