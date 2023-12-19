package com.touchfish.MiddleClass;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoAuthor {
    private String id;
    private String display_name;
    private String last_known_institution_display_name;
}
