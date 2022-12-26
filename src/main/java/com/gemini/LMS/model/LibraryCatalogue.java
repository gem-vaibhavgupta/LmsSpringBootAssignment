package com.gemini.LMS.model;

import io.swagger.models.auth.In;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class LibraryCatalogue extends Book {
    private Integer catalogueId;
    private String catalogueName;
}
