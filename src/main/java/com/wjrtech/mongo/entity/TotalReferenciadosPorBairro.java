package com.wjrtech.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class TotalReferenciadosPorBairro {
    private int qtd;
    @Field("_id")
    private String bairro;
}
