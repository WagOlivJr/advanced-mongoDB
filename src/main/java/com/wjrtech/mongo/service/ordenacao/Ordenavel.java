package com.wjrtech.mongo.service.ordenacao;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public interface Ordenavel {
    double getDistancia();

    int getCodigoPrestador();

    int getRanking();

    //Comentar depois
    GeoJsonPoint getPosicao();

    void setDistancia(double distancia);

}
