package com.service.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DtoSearchResponse {

    private Long offset;
    private Integer limit, total;
    private String sort;
    private Iterable<? extends Dto> data;
}
