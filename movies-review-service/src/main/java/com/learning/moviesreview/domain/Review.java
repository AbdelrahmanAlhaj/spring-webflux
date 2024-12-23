package com.learning.moviesreview.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class Review {
    @Id
    private String reviewId;
    private Long movieInfoId;

    private String comment;
    private Double rating;

}
