package com.touchfish.MiddleClass;

import lombok.Data;

import java.util.List;

@Data
public class AuthorShip {
    private List<DisplayInfo> institutions ;
    private DisplayInfo author;
}
