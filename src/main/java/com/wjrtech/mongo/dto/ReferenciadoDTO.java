package com.wjrtech.mongo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wjrtech.mongo.entity.Contato;
import com.wjrtech.mongo.entity.LocalAtendimento;
import com.wjrtech.mongo.entity.Qualificacao;
import com.wjrtech.mongo.entity.RedePrestador;
import com.wjrtech.mongo.service.ordenacao.Ordenavel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class ReferenciadoDTO implements Ordenavel {
    @JsonIgnore
    private String id;
    private double distancia;
    private int ranking;
    private int codigo;
    private String cnpj;
    private String nomeFantasia;
//    @Field("conselhosMedicos.numeroInscricaoConselho")
    private List<Integer> conselhosMedicos;
//    private LocalAtendimento locaisAtendimento;
//    @Field("codigoLocalAtendimento")
    private int codigoLocalAtendimento;
    @Field("razaoSocial")
    private String nome;
    private String refNovo;
    @Field("especialidades.codigoEspecialidade")
    private List<Integer> listaEspecialidades;
    @Field("tipoAtendimento.codigoTipoAtendimento")
    private int codigoTipoAtendimento;
//    @Field("listaQualificacoes.codigo")
    private List<Qualificacao> listaQualificacoes;
    private int codigoTipoReferenciado;
    private String codigoAcomodacao;
//    @Field("locaisAtendimento.instaAdapt")
    private String instaAdapt;
    @Field("locaisAtendimento.codigoRegiao")
    private int codigoRegiao;
    @Field("redesPrestador.codigoRede")
    private List<RedePrestador> redesPrestador;
    private String bairro;
    private String longitude;
    private String latitude;
    private List<String> listaTelefones;

    @JsonIgnore
    private int codigoPrestador;

    @Override
    public double getDistancia() {
        return distancia;
    }

    @Override
    public int getCodigoPrestador() {
        return codigo;
    }

    public void setCodigoPrestador(int codigoPrestador) {
        this.codigo = codigoPrestador;
    }

    @Override
    public int getRanking() {
        return ranking;
    }

    @JsonIgnore
    public GeoJsonPoint getPosicao() {
        return new GeoJsonPoint(Double.parseDouble(longitude), Double.parseDouble(latitude)) ;
    }


}
