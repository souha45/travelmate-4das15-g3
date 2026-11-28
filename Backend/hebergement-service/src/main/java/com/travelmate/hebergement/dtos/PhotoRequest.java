package com.travelmate.hebergement.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PhotoRequest {

    @NotBlank(message = "L'URL est obligatoire")
    private String url;

    @NotNull(message = "L'ordre est obligatoire")
    private Integer ordre;

    private Long hebergementId;
}
