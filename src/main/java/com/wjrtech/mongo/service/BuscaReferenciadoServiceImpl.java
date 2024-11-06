package com.wjrtech.mongo.service;

import com.mongodb.internal.operation.AggregateOperation;
import com.wjrtech.mongo.dto.ReferenciadoDTO;
import com.wjrtech.mongo.dto.RequestDTO;
import com.wjrtech.mongo.dto.ResponseDTO;
import com.wjrtech.mongo.entity.Prestador;
import com.wjrtech.mongo.entity.TotalReferenciadosPorBairro;
import com.wjrtech.mongo.exception.GenericException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sun.util.resources.is.LocaleNames_is;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
//@Primary
@AllArgsConstructor
public class BuscaReferenciadoServiceImpl implements ProviderService{

    MongoTemplate mongoTemplate;

    private static final String LOCAIS_ATENDIMENTO = "locaisAtendimento";
    private static final String TIPO_ATENDIMENTO = "locaisAtendimento.tipoAtendimento";
    private static final String ESPECIALIDADES = "locaisAtendimento.tipoAtendimento.especialidades";

    @Override
    public ResponseDTO anotherTest(RequestDTO request) {


        List<ReferenciadoDTO> listaReferenciados;
        List<AggregationOperation> operacoesAgregacao = new ArrayList<>();
        double raio = 20.0;
        double segundoRaio = 50.0;
        int referenciadosPorPagina = 15;


        //Parametros da requisição:
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

        Pageable pageable = PageRequest.of(totalPaginas, referenciadosPorPagina);

        //Adicionando operações de agregação
        operacoesAgregacao.add(Aggregation.match(Criteria.where("codigoTipoReferenciado")
                .is(codigoTipoReferenciado)));

        operacoesAgregacao.add(Aggregation.unwind("locaisAtendimento"));

        operacoesAgregacao.add(Aggregation.match(Criteria.where("locaisAtendimento.redesPrestador")
                .elemMatch(Criteria.where("codigoRede")
                        .is(codigoRede))));

        operacoesAgregacao.add(Aggregation.match(Criteria.where("locaisAtendimento.tipoAtendimento")
                .elemMatch(Criteria.where("codigoTipoAtendimento")
                        .is(codigoTipoEstabelecimento))));

        operacoesAgregacao.add(Aggregation.match(Criteria.where("locaisAtendimento.tipoAtendimento" +
                        ".especialidades.codigoEspecialidade")
                .all(listaEspecialidades)));

        operacoesAgregacao.add(Aggregation.match(Criteria.where("locaisAtendimento.codigoAcomodacao")
                .is(codigoAcomodacao)));

        operacoesAgregacao.add(Aggregation.match(Criteria.where("locaisAtendimento.codigoRegiao")
                .is(codigoRegiao)));

        operacoesAgregacao.add(Aggregation.match(Criteria.where("locaisAtendimento.local")
                .withinSphere(new Circle(longitude,latitude, raio/6371.0))));
        int indiceFiltroPorRaio = operacoesAgregacao.size() - 1;

        if("S".equals(instaAdapt)) {
            operacoesAgregacao.add(Aggregation.match(Criteria.where("locaisAtendimento.instaAdapt")
                    .is(instaAdapt)));
        }
        adicionarParametroOpcional(operacoesAgregacao, listaBairros, "locaisAtendimento.bairro");

        operacoesAgregacao.add(Aggregation.group("locaisAtendimento.bairro")
                .count().as("qtd")
        );


        Aggregation aggregation = Aggregation.newAggregation(operacoesAgregacao);

        AggregationResults<TotalReferenciadosPorBairro> countResults = mongoTemplate.aggregate(aggregation,"prestadores", TotalReferenciadosPorBairro.class);

        ResponseDTO response = new ResponseDTO(countResults.getMappedResults());

        int totalReferenciados = response.getTotalReferenciados();

        if(totalReferenciados == 0) {
            raio = segundoRaio;
            operacoesAgregacao.set(
                    indiceFiltroPorRaio, Aggregation.match(Criteria.where("locaisAtendimento.local")
                            .withinSphere(new Circle(longitude,latitude, raio/6378.1)))
            );

            aggregation = Aggregation.newAggregation(operacoesAgregacao);

            countResults = mongoTemplate.aggregate(aggregation,"prestadores", TotalReferenciadosPorBairro.class);

            response = new ResponseDTO(countResults.getMappedResults());

            totalReferenciados = response.getTotalReferenciados();
        }

        if(totalReferenciados == 0){
            throw new GenericException("Prestadores não encontrados.", HttpStatus.NOT_FOUND);
        };

        operacoesAgregacao.remove(operacoesAgregacao.size() - 1);

        operacoesAgregacao.add(Aggregation.sort(Sort.Direction.ASC,  "locaisAtendimento.ranking", "codigoPrestador"));

        operacoesAgregacao.add(Aggregation.skip((long) pageable.getOffset()));
        operacoesAgregacao.add(Aggregation.limit(pageable.getPageSize()));

//        operacoesAgregacao.add(Aggregation.project().andExpression("locaisAtendimento.instaAdapt").as("instaAdapt"));
        ProjectionOperation projectionOperation = Aggregation.project()
                .and(LOCAIS_ATENDIMENTO + ".nomeDivulgado").as("nomeFantasia")
//                .and(ESPECIALIDADES + ".codigoEspecialidade").as("especialidades.codigoEspecialidade")
                .and(LOCAIS_ATENDIMENTO + ".qualificacoes.codigoQualificacao").as("listaQualificacoes.codigo")
                .and(TIPO_ATENDIMENTO + ".codigoTipoAtendimento").as("tipoAtendimento.codigoTipoAtendimento")
                .and(LOCAIS_ATENDIMENTO + ".instaAdapt").as("instaAdapt")
                .and(LOCAIS_ATENDIMENTO + ".refNovo").as("refNovo")
                .and(LOCAIS_ATENDIMENTO + ".codigoLocalAtendimento").as("codigoLocalAtendimento")
                .and(LOCAIS_ATENDIMENTO + ".razaoSocial").as("razaoSocial")
                .and("cnpj").as("cnpj")
                .and(LOCAIS_ATENDIMENTO +".codigoAcomodacao").as("codigoAcomodacao")
//                .and("conselhosMedicos.numeroInscricaoConselho").as("conselhosMedicos.numeroInscricao")
//                .and(LOCAIS_ATENDIMENTO + ".contatoLocalAtendimento.ddi").as("contato.ddi")
//                .and(LOCAIS_ATENDIMENTO + ".contatoLocalAtendimento.ddd").as("contato.ddd")
//                .and(LOCAIS_ATENDIMENTO + ".contatoLocalAtendimento.telefone").as("contato.telefone")
//                .andExpression("$concat(locaisAtendimento.contatoLocalAtendimento.ddd, '-', locaisAtendimento.contatoLocalAtendimento.telefone)").as("listaTelefones.telefone")
                .andExpression("map(locaisAtendimento.contatoLocalAtendimento, 'contato', concat('$$contato.ddd', '-', '$$contato.telefone'))").as("listaTelefones")
                .and(LOCAIS_ATENDIMENTO + ".bairro").as("bairro")
                .and(LOCAIS_ATENDIMENTO + ".latitude").as("latitude")
                .and(LOCAIS_ATENDIMENTO + ".longitude").as("longitude")
                .and("codigoTipoReferenciado").as("codigoTipoReferenciado")
                .and("codigoPrestador").as("codigo")
                .and(LOCAIS_ATENDIMENTO + ".codigoRegiao").as("codigoRegiao")
                .and(LOCAIS_ATENDIMENTO + ".local").as("local")
                .and(LOCAIS_ATENDIMENTO + ".redesPrestador").as("redesPrestador.codigoRede")
                .and(LOCAIS_ATENDIMENTO + ".ranking").as("ranking")
                .andInclude("conselhosMedicos.numeroInscricaoConselho");

        operacoesAgregacao.add(projectionOperation);


        aggregation = Aggregation.newAggregation(operacoesAgregacao);

        AggregationResults<ReferenciadoDTO> providersResults = mongoTemplate.aggregate(aggregation,"prestadores", ReferenciadoDTO.class);


        response.setListaReferenciados(providersResults.getMappedResults());

        response.setRaio((int) raio);
        response.setTotalPaginas( (int) Math.ceil( (double) totalReferenciados / referenciadosPorPagina));

        if(totalPaginas >= response.getTotalPaginas()){
            throw new GenericException("Há prestadores somente até a página de número " + (response.getTotalPaginas()) + ".", HttpStatus.NOT_FOUND);
        }

        return response;

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


    @Override
    public List<Prestador> getAllProviders() {
        return null;
    }

    @Override
    public List<Prestador> getAllProvidersWithSortingAndPagination(int page) {
        return null;
    }

    @Override
    public ResponseDTO getByProviderCode(Long codigoPrestador) {
        return null;
    }

    @Override
    public ResponseDTO getProviders(RequestDTO requestDTO) {
        return null;
    }

    @Override
    public RequestDTO requestValidate(RequestDTO requestDTO) {
        return requestDTO;
    }

    @Override
    public ResponseDTO teste(RequestDTO requestDTO) {
        return null;

    }
}
