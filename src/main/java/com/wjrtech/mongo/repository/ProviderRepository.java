package com.wjrtech.mongo.repository;

import com.wjrtech.mongo.dto.ReferenciadoDTO;
import com.wjrtech.mongo.dto.ResponseDTO;
import com.wjrtech.mongo.entity.LocalAtendimento;
import com.wjrtech.mongo.entity.Prestador;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProviderRepository extends MongoRepository<Prestador, String> {

    @Aggregation(pipeline = {
            "{ $unwind: { path: '$locaisAtendimento' }}"
    })
    List<Prestador> findAll();

    @Aggregation(pipeline = {
            "{ $match: { codigoPrestador: ?0 }}",
            "{ $unwind: { path: '$locaisAtendimento' }}",
            "{ $match: { 'locaisAtendimento.codigoAcomodacao': ?1 }}"
//            ,
//            "{ $addFields: { localAtendimento: '$locaisAtendimento' }}",
//            "{ $project: { locaisAtendimento: 0 }}"
    })
    List<ReferenciadoDTO> findByCodigoPrestador(Long codigoPrestador, String codigoAcomodacao);

    @Aggregation(pipeline = {
//            "{ $match: { codigoPrestador: ?0 }}",
            "{ $unwind: { path: '$locaisAtendimento' }}",
            "{ $count: 'count' }"
//            ,
//            "{ $addFields: { localAtendimento: '$locaisAtendimento' }}",
//            "{ $project: { locaisAtendimento: 0 }}"
    })
    int countByCodigoPrestador();

    @Aggregation(pipeline = {
            "{ $match: { codigoTipoReferenciado: ?0 }}",
            "{ $unwind: { path: '$locaisAtendimento' }}",
            "{ $match: {" +
//                " 'locaisAtendimento.codigoRede' : ?0 ",
//                " 'locaisAtendimento.tipoAtendimento.codigoTipoAtendimento' : ?1",
//                " 'locaisAtendimento.tipoAtendimento.especialidades.codigoEspecialidade' : ?2 ",
                " 'locaisAtendimento.instaAdapt': ?1 ," +
                " 'locaisAtendimento.codigoRegiao': ?2 ," +
                " 'locaisAtendimento.codigoAcomodacao': ?3 ," +
                " 'locaisAtendimento.local': { $geoWithin: { $centerSphere: [[ ?4 , ?5 ], 5.0/40075 ] }} " +
//                " 'locaisAtendimento.bairro': ?9" +
            "}}"
//            ,
//            "{ $count: 'count' }"
    })
    List<ReferenciadoDTO> teste(
        int codigoTipoReferenciado, //0
        String instaAdapt, //1
        int codigoRegiao, //2
        String codigoAcomodacao, //3
        float longitude, //4
        float latitude //5
//        int codigoRede, //0
//        int codigoTipoEstabelecimento, //1
//        int listaEspecialidades, //2
//        List<String> listaBairros //9
    );

}
