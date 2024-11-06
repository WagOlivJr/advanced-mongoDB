package com.wjrtech.mongo.dto;

import lombok.Data;

import java.util.List;

@Data
public class QueryResultDTO {
    List<ReferenciadoDTO> listaReferenciados;
    int totalReferenciados;
}
