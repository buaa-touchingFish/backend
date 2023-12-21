package com.touchfish.MiddleClass;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AuthorShip {
    private DisplayInfo author;
    private List<DisplayInfo> institutions;
}
