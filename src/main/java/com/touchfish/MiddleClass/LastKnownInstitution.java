package com.touchfish.MiddleClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LastKnownInstitution {
    private String id;
    private String type;
    private String country_code;
    private String display_name;
}

