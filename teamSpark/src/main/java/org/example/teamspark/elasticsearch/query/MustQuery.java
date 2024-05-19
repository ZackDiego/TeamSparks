package org.example.teamspark.elasticsearch.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MustQuery {
    private MatchQuery match;
}
