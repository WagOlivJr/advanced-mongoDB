package com.wjrtech.mongo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

@Data
public class LocalAtendimento {
    private Long codigoPrestador;
    private Long codigoLocalAtendimento;
    private String nome;
    private List<RedePrestador> redesPrestador;
    private String longitude;
    private String latitude;
    @JsonIgnore
    private GeoJsonPoint local;
}
