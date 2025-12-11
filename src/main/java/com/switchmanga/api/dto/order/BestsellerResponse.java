package com.switchmanga.api.dto.order;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class BestsellerResponse {
    private Integer rank;
    private Long volumeId;
    private String volumeTitle;
    private Integer volumeNumber;
    private Long seriesId;
    private String seriesTitle;
    private String coverImage;
    private Long salesCount;
    private BigDecimal revenue;
}