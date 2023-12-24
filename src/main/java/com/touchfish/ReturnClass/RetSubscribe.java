package com.touchfish.ReturnClass;

import com.touchfish.MiddleClass.LastKnownInstitution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetSubscribe {
    private String id;
    private String display_name;
    private LastKnownInstitution last_known_institution;
    private String avatar;
}
