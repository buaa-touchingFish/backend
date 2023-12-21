package com.touchfish.MiddleClass;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AuthorShip {
    private List<DisplayInfo> institutions;
    private DisplayInfo author;
}
