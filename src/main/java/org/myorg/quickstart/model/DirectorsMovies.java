package org.myorg.quickstart.model;

import lombok.*;

// Required annotations to have:
// * Builder class
// * NoArgsConstructor required by Flink to be considered a POJO
@With
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE) // without this annotation, Builder + NoArgsConstructor doesn't work
@NoArgsConstructor
@Data
public class DirectorsMovies {
    private String id;
    private String director;
    private String movie;
    private Boolean nominatedToOscar;
}
