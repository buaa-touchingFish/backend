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

    public CoAuthor(String id, String display_name) {
        this.id = id;
        this.display_name = display_name;
    }

    public CoAuthor(String id) {
        this.id = id;
    }
}
