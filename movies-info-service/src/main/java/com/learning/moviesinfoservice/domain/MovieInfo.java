package com.learning.moviesinfoservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class MovieInfo {

    @Id
    private String movieInfoId;

    @NotBlank(message = "must be present")
    private String name;
    @NotNull
    @Positive(message = "must be a Positive number")
    private Integer year;

    private List<@NotBlank(message = "must be present") String> cast;
    private LocalDate releaseDate;
}
