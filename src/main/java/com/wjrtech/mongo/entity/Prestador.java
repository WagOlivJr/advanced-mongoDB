package com.wjrtech.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "prestadores")
public class Prestador {
    @Id
    private String id;
    @Field
    private Long codigoPrestador;
    @Field
    private String cpf;
    @Field
    private String cnpj;
    @Field
    private String nomeDivulgacaoPrestador;
    @Field(name = "locaisAtendimento")
    private LocalAtendimento localAtendimento;
}
