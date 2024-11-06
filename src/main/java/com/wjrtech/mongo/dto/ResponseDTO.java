package com.wjrtech.mongo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wjrtech.mongo.entity.LocalAtendimento;
import com.wjrtech.mongo.entity.Prestador;
import com.wjrtech.mongo.entity.TotalReferenciadosPorBairro;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class ResponseDTO {
    private int totalReferenciados;
    private List<ReferenciadoDTO> listaReferenciados;
    private int totalPaginas;
    private int raio;
    private List<TotalReferenciadosPorBairro> listaTotalReferenciadosPorBairro;

//    public ResponseDTO(List<ReferenciadoDTO> listaReferenciados){ // Java apresenta erro pois não considera a classe dentro de List, então é como se fossem constructors que recebessem soment List como parametro
//        this.totalReferenciados = listaReferenciados.size();
//        this.listaReferenciados = listaReferenciados;
//    }

    public ResponseDTO(List<TotalReferenciadosPorBairro> listaTotalReferenciadosPorBairro) {
        this.listaTotalReferenciadosPorBairro = listaTotalReferenciadosPorBairro;
        this.totalReferenciados = listaTotalReferenciadosPorBairro.stream().mapToInt(TotalReferenciadosPorBairro::getQtd).sum();
    };

}
