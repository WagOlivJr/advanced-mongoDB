package com.wjrtech.mongo.dto;

import com.wjrtech.mongo.service.ordenacao.Ordenavel;
import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Data
public class DadosOrdenacaoDTO implements Ordenavel {
    private String id;
    private int codigoPrestador;
    private int codigoLocalAtendimento;
//    private int codigoTipoAtendimento;
    private int ranking;
    private GeoJsonPoint posicao;
    private double distancia;

    @Override
    public double getDistancia() {
        return distancia;
    }

    @Override
    public int getCodigoPrestador() {
        return codigoPrestador;
    }

    @Override
    public int getRanking() {
        return ranking;
    }
}
