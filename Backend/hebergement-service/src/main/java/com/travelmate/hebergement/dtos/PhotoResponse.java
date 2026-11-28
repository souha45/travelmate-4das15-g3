package com.travelmate.hebergement.dtos;

import lombok.Data;

@Data
public class PhotoResponse {
    private Long id;
    private String url;
    private Integer ordre;
    private Long hebergementId;
}
