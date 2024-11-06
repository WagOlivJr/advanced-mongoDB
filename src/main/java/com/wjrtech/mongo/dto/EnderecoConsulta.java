package com.wjrtech.mongo.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class EnderecoConsulta {
    @NotNull(message = "o preenchimento do parâmetro 'latitude' é obrigatório.")
    @NotBlank
    @Pattern(regexp = "^[-+]?([1-8]?\\d(\\.\\d{1,10})?|90(\\.0{1,10})?)$",
            message = "o parâmetro 'latitude' deve ser um inteiro ou decimal de até 10 caracteres entre -90 e 90.")
    private String latitude;
    @NotNull(message = "o preenchimento do parâmetro 'longitude' é obrigatório.")
    @NotBlank
    @Pattern(regexp = "^[-+]?(180(\\.0{1,10})?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d{1,10})?)$",
            message = "o parâmetro 'longitude' deve ser um inteiro ou decimal de até 10 caracteres entre -180 e 180.")
    private String longitude;
//    private GeoJsonPoint posicao;
}
