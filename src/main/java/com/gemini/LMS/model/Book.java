package com.gemini.LMS.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private Integer bookId;
    private String bookName;
    private String authorName;
    private String publishedBy;
}
