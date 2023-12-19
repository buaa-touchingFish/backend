package com.touchfish.Po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstitutionRelation {
    private String id;
    private String ror;
    private String display_name;
    private String country_code;
    private String type;
    private String relationship;
}
