package com.wjrtech.mongo.entity;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class Qualificacao {
    @Field("codigoQualificacao")
    private int codigo;
    @Field("nomeQualificacao")
    private String descricao;

}
