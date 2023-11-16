package com.touchfish.MiddleClass;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
public class AuthorShip {
    private List<DisplayInfo> institutions ;
    private DisplayInfo author;
}
