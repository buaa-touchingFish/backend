package com.touchfish.MiddleClass;

import com.touchfish.Po.Author;
import com.touchfish.Po.Paper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorHome {
    private Author author;
    private List<Paper> papers;
    private List<CoAuthor> co_authors;
//    private Integer subscriberCnt;
}
