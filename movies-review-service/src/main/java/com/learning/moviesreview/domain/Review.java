package com.learning.moviesreview.domain;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document
public class Review {
    @Id
    private String reviewId;
    @NotNull
    private Long movieInfoId;

    private String comment;
    @Min(value = 0L, message = "review.rating: please pass non negative value")
    private Double rating;

}
