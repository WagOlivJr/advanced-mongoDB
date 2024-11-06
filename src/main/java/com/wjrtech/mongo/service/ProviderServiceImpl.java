/*package com.wjrtech.mongo.service;

import com.wjrtech.mongo.dto.*;
import com.wjrtech.mongo.entity.Prestador;
import com.wjrtech.mongo.repository.ProviderRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
//@RequiredArgsConstructor
@Service
public class ProviderServiceImpl implements ProviderService {

//    @NonNull
    ProviderRepository providerRepository;
//    @NonNull
    MongoTemplate mongoTemplate;

    @Override
    public List<Prestador> getAllProviders() {
        return providerRepository.findAll();
    }
    @Override
    public List<Prestador> getAllProvidersWithSortingAndPagination(int page) {
        Sort sort = Sort.by("codigoPrestador");
        Pageable pageable = PageRequest.of(page - 1, 15, sort);
        return providerRepository.findAll(pageable).getContent();
    }

    @Override
    public ResponseDTO getByProviderCode(Long codigoPrestador) {
//        ResponseDTO response = new ResponseDTO(providerRepository.findByCodigoPrestador(codigoPrestador, "E"));
//        System.out.println(providerRepository.countByCodigoPrestador());
//        return response;
        return null;
    }

    @Override
    public ResponseDTO getProviders(RequestDTO requestDTO) {
        return null;
    }

    @Override
    public RequestDTO requestValidate(RequestDTO requestDTO) {
        //validações adicionais do request
        return requestDTO;
    }

    @Override
    public ResponseDTO teste(RequestDTO requestDTO) {
        int codigoTipoReferenciado = requestDTO.getCodigoTipoReferenciado();
        String instaAdapt = requestDTO.getInstaAdapt();
        int codigoRegiao = requestDTO.getCodigoRegiao();
        String codigoAcomodacao = requestDTO.getCodigoAcomodacao();
        float longitude = 1.1f; //= Float.parseFloat(requestDTO.getEndereçoConsulta().getLongitude());
        float latitude = 1.1f; //= Float.parseFloat(requestDTO.getEndereçoConsulta().getLatitude());
        List<ReferenciadoDTO> referenciados = providerRepository.teste(
                codigoTipoReferenciado,
                instaAdapt,
                codigoRegiao,
                codigoAcomodacao,
                longitude,
                latitude
            );
        ResponseDTO response = new ResponseDTO(referenciados);
//        ResponseDTO response = new ResponseDTO(providerRepository.teste(requestDTO.getCodigoTipoReferenciado()));
        System.out.println(providerRepository.countByCodigoPrestador());
        return response;

//        return providerRepository.teste(
//            requestDTO.getCodigoTipoReferenciado()
//        );
//        return null;
    }

//    @Override
//    public List<Prestador> getById(String id) {
//        return providerRepository.findById(id);
//    }

    @Override
    public ResponseDTO anotherTest(RequestDTO request) {

        List<ReferenciadoDTO> listaReferenciados;
        List<AggregationOperation> filtros = new ArrayList<>();
        double raio = 20;

        int codigoRede = request.getCodigoRede();
        int codigoTipoEstabelecimento = request.getCodigoTipoEstabelecimento();
        List<Integer> listaEspecialidades = request.getListaEspecialidades();
        int codigoTipoReferenciado = request.getCodigoTipoReferenciado();
        String codigoAcomodacao = request.getCodigoAcomodacao();
        int codigoRegiao = request.getCodigoRegiao();
        double latitude = Double.parseDouble(request.getEnderecoConsulta().getLatitude());
        double longitude = Double.parseDouble(request.getEnderecoConsulta().getLongitude());
        String instaAdapt = request.getInstaAdapt();
        List<String> listaBairros = request.getListaBairros();
        int totalPaginas = request.getTotalPaginas() -1;

        int referenciadosPorPagina = 5;
        Pageable pageable = PageRequest.of(totalPaginas, referenciadosPorPagina);

        filtros.add(Aggregation.match(Criteria.where("codigoTipoReferenciado")
                .is(codigoTipoReferenciado)));

        filtros.add(Aggregation.unwind("locaisAtendimento"));
        filtros.add(Aggregation.match(Criteria.where("locaisAtendimento.redesPrestador")
                .elemMatch(Criteria.where("codigoRede")
                        .is(codigoRede))));
        filtros.add(Aggregation.match(Criteria.where("locaisAtendimento.tipoAtendimento")
                .elemMatch(Criteria.where("codigoTipoAtendimento")
                        .is(codigoTipoEstabelecimento))));
        filtros.add(Aggregation.match(Criteria.where("locaisAtendimento.tipoAtendimento" +
                        ".especialidades.codigoEspecialidade")
                .all(listaEspecialidades)));
        filtros.add(Aggregation.match(Criteria.where("locaisAtendimento.codigoAcomodacao")
                .is(codigoAcomodacao)));
        filtros.add(Aggregation.match(Criteria.where("locaisAtendimento.codigoRegiao")
                .is(codigoRegiao)));
        filtros.add(Aggregation.match(Criteria.where("locaisAtendimento.local")
                .withinSphere(new Circle(longitude,latitude, raio/6378.1))));
        int indiceFiltroPorRaio = filtros.size() - 1;
        if("S".equals(instaAdapt)) {
            filtros.add(Aggregation.match(Criteria.where("locaisAtendimento.instaAdapt")
                    .is(instaAdapt)));
        }
        adicionarParametroOpcional(filtros, listaBairros, "locaisAtendimento.bairro");
        filtros.add(Aggregation.project()
                        .and("nomeDivulgacaoPrestador").as("nomeFantasia")
////                .andExpression("codigoEspecialidade").as("locaisAtendimento.tipoAtendimento.especialidades.codigoEspecialidade")
////                .andExpression("locaisAtendimento.tipoAtendimento.codigoTipoAtendimento").as("codigoEstabelecimento")
//                        .andExpression("locaisAtendimento.instaAdapt").as("instaAdapt")
//                        .andExpression("locaisAtendimento.instaAdapt").as("instaAdapt")
//                        .andExpression("cnpj").as("cnpj")
//                        .andExpression("cpf").as("cpf")
//                        .andExpression("locaisAtendimento.codigoAcomodacao").as("acomodacao")
//                        .andExpression("locaisAtendimento").as("locaisAtendimento")
//                        .andExpression("locaisAtendimento.latitude").as("locaisAtendimento.latitude")
//                        .andExpression("locaisAtendimento.longitude").as("locaisAtendimento.longitude")
//                        .andExpression("locaisAtendimento.local").as("local")
//                        .andExpression("{$map:{input: '$locaisAtendimento.redesPrestador', as: obj, in: '$$obj.codigoRede' } }").as("redesPrestador")
//
//
//                        .andInclude("codigoPrestador")// não funcionou
//                        .andExpression("nomeFantasia").as("codigoPrestador")// não funcionou
                        .andExclude("_id")
        );


////        TotalReferenciadosDTO totalReferenciadosDTO = contarReferenciados(filtros,raio,indiceFiltroPorRaio).get(0);
//         TotalReferenciadosDTO totalReferenciadosDTO =
//        if(totalReferenciadosDTO.obterTotalReferenciados() == 0) {
//            raio = 50.0;
//            contarReferenciados(filtros,raio,indiceFiltroPorRaio);
//        }

//        filtros.add(Aggregation.skip((long) pageable.getOffset()));
//        filtros.add(Aggregation.limit(pageable.getPageSize()));

//        FacetOperation facetOperation = Aggregation.facet(Aggregation.newAggregation(filtros).skip((long) pageable.getOffset()).limit(pageable.getPageSize())).as("resultadosPaginados").and(Aggregation.newAggregation(filtros)).count().as("totalReferenciados").as("contagemTotal");

        List<AggregationOperation> paginationPipeline = new ArrayList<>(filtros);
        paginationPipeline.add(Aggregation.skip((long) pageable.getOffset()));
        paginationPipeline.add(Aggregation.limit(pageable.getPageSize()));

        List<AggregationOperation> countPipeline = new ArrayList<>(filtros);
        countPipeline.add(Aggregation.count().as("totalReferenciados"));

        FacetOperation facetOperation = Aggregation.facet()
        .and(paginationPipeline.toArray(new AggregationOperation[0])).as("listaReferenciados")
        .and(countPipeline.toArray(new AggregationOperation[0])).as("total");

        List<AggregationOperation> finalPipeline = new ArrayList<>(filtros);
        finalPipeline.add(facetOperation);

        Aggregation aggregation = Aggregation.newAggregation(finalPipeline);

//        FacetOperation facetOperation = Aggregation.facet(
//
//                        Aggregation.skip((long) pageable.getOffset()),
//                        Aggregation.limit(pageable.getPageSize())
//                ).as("listaReferenciados")
//                .and(
//                        Aggregation.count().as("totalReferenciados")
//                ).as("total");
        filtros.add(facetOperation);

//        Aggregation aggregation = Aggregation.newAggregation(filtros);
//        AggregationResults<QueryResultDTO> results = mongoTemplate.aggregate(aggregation,"prestadores", QueryResultDTO.class);
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation,"prestadores", Map.class);
        Map<String, Object> resultData = results.getUniqueMappedResult();
        System.out.println(resultData);

        if(resultData != null) {
            listaReferenciados = (List<ReferenciadoDTO>) resultData.get("listaReferenciados");
            List<Map> totalList = (List<Map>) resultData.get("total");
            int totalReferenciados = totalList.isEmpty() ? 0 : (Integer) totalList.get(0).get("totalReferenciados");


//        int totalReferenciados = results.getUniqueMappedResult().getTotalReferenciados();

//        if(totalReferenciados == 0) {
//            raio = 50.0;
//            results = buscaPorSegundoRaio(latitude,longitude,raio,filtros,indiceFiltroPorRaio);
//            totalReferenciados = results.getUniqueMappedResult().getTotalReferenciados();
//        }
//        listaReferenciados = results.getUniqueMappedResult().getListaReferenciados();


           ResponseDTO response = new ResponseDTO(listaReferenciados);


//        System.out.println(results.);
//        response.setTotalReferenciados(totalReferenciadosDTO.obterTotalReferenciados());
            response.setTotalReferenciados(totalReferenciados);
//        response.setTotalPaginas(totalReferenciadosDTO.obterTotalPaginas(referenciadosPorPagina));
            response.setTotalPaginas(totalReferenciados / referenciadosPorPagina);
            response.setRaio((int) raio);
//        response
            return response;
        }
        return null;

    }

//    private List<QueryResultDTO> contarReferenciados(List<AggregationOperation> filtros, double raio, int indiceFiltroPorRaio) {
//        filtros.add(Aggregation.count().as("totalReferenciados"));
//
//        Aggregation aggregation = Aggregation.newAggregation(filtros);
//        AggregationResults<QueryResultDTO> results = mongoTemplate.aggregate(aggregation,"prestadores", QueryResultDTO.class);
//        filtros.remove(filtros.size()-1);
//        System.out.println(results.getMappedResults());
//        return results.getMappedResults();
//
//
//    }

    private  AggregationResults<QueryResultDTO> buscaPorSegundoRaio(double latitude,
                                                                    double longitude,
                                                                    double segundoRaio,
                                                                    List<AggregationOperation> filtros,
                                                                    int indiceFiltroPorRaio) {
        filtros.set(indiceFiltroPorRaio, Aggregation.match(Criteria.where("locaisAtendimento.local")
                .withinSphere(new Circle(longitude,latitude, segundoRaio/6378.1))));
        Aggregation aggregation = Aggregation.newAggregation(filtros);
        return mongoTemplate.aggregate(aggregation,"prestadores", QueryResultDTO.class);
    }

    private void adicionarParametroOpcional(List<AggregationOperation> filtros, String parametro, String campoMongo) {
        if(parametro != null && !parametro.isEmpty()) {
            filtros.add(Aggregation.match(Criteria.where(campoMongo).is(parametro)));
        }
    }

    private void adicionarParametroOpcional(List<AggregationOperation> filtros, Number parametro, String campoMongo) {
        if(parametro != null) {
            filtros.add(Aggregation.match(Criteria.where(campoMongo).is(parametro)));
        }
    }

    private void adicionarParametroOpcional(List<AggregationOperation> filtros, List<?> parametro, String campoMongo) {
        if(parametro != null && !parametro.isEmpty()) {
            filtros.add(Aggregation.match(Criteria.where(campoMongo).in(parametro)));
        }
    }

    private void adicionarParametroOpcional(List<AggregationOperation> filtros, Object parametro, String campoMongo) {
        if(parametro != null) {
            filtros.add(Aggregation.match(Criteria.where(campoMongo).is(parametro)));
        }
    }

//    private void adicionarParametro(Object parametro, String caminho){
//        filtros.add(Aggregation.match(Criteria.where(caminho).is(parametro)));
//    }
//
//    private void adicionarParametro(Object parametro, String caminho, boolean isList){
//        filtros.add(Aggregation.match(Criteria.where(caminho).in(parametro)));
//    }

//
//    private

}
*/