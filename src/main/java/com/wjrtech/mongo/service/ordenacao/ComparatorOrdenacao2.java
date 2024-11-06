package com.wjrtech.mongo.service.ordenacao;

import com.wjrtech.mongo.dto.DadosOrdenacaoDTO;

import java.util.Comparator;

public class ComparatorOrdenacao2<T extends Ordenavel> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        double ref1Distancia = o1.getDistancia();
        double ref2Distancia = o2.getDistancia();
        int ref1Ranking = o1.getRanking();
        int ref2Ranking = o2.getRanking();
        int ref1codigo = o1.getCodigoPrestador();
        int ref2codigo = o2.getCodigoPrestador();

//        if(ref1Distancia == null)
        if(ref1Distancia < ref2Distancia) return -1;
        if(ref1Distancia > ref2Distancia) return 1;
        if(ref1Ranking < ref2Ranking) return -1;
        if(ref1Ranking > ref2Ranking) return 1;
        if(ref1codigo > ref2codigo) return -1;
        if(ref1codigo < ref2codigo) return 1;

        return 0;
    }
}
