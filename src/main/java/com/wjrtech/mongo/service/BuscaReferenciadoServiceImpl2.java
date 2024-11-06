package com.wjrtech.mongo.service;

import com.wjrtech.mongo.dto.DadosOrdenacaoDTO;
import com.wjrtech.mongo.dto.ReferenciadoDTO;
import com.wjrtech.mongo.dto.RequestDTO;
import com.wjrtech.mongo.dto.ResponseDTO;
import com.wjrtech.mongo.entity.Prestador;
import com.wjrtech.mongo.entity.TotalReferenciadosPorBairro;
import com.wjrtech.mongo.exception.GenericException;
import com.wjrtech.mongo.service.ordenacao.ComparatorOrdenacao;
import com.wjrtech.mongo.service.ordenacao.ComparatorOrdenacao2;
import com.wjrtech.mongo.service.ordenacao.OrdenacaoService;
import com.wjrtech.mongo.service.ordenacao.Ordenavel;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
@AllArgsConstructor
public class BuscaReferenciadoServiceImpl2 implements ProviderService{

    MongoTemplate mongoTemplate;

    private static final String LOCAIS_ATENDIMENTO = "locaisAtendimento";
    private static final String TIPO_ATENDIMENTO = "locaisAtendimento.tipoAtendimento";
    private static final String ESPECIALIDADES = "locaisAtendimento.tipoAtendimento.especialidades";

    private static final double RAIO_TERRA_KM = 6371;

    @Override
    public ResponseDTO anotherTest(RequestDTO request) {


        List<ReferenciadoDTO> listaReferenciados;
        List<AggregationOperation> operacoesAgregacao = new ArrayList<>();
        final double primeiroRaio = 20.0;
        final double segundoRaio = 50.0;
        final int referenciadosPorPagina = 15;


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
        double raio = primeiroRaio;

//        Pageable pageable = PageRequest.of(totalPaginas, referenciadosPorPagina);

        //Adicionando operações de agregação
        operacoesAgregacao.add(Aggregation.match(Criteria.where("codigoTipoReferenciado")
                .is(codigoTipoReferenciado)));

        operacoesAgregacao.add(Aggregation.unwind(LOCAIS_ATENDIMENTO));
        operacoesAgregacao.add(Aggregation.unwind(TIPO_ATENDIMENTO));

        operacoesAgregacao.add(Aggregation.match(Criteria.where(LOCAIS_ATENDIMENTO + ".redesPrestador")
                .elemMatch(Criteria.where("codigoRede")
                        .is(codigoRede))));

//        operacoesAgregacao.add(Aggregation.match(Criteria.where( TIPO_ATENDIMENTO)
//                .elemMatch(Criteria.where("codigoTipoAtendimento")
//                        .is(codigoTipoEstabelecimento))));
        operacoesAgregacao.add(Aggregation.match(Criteria.where(TIPO_ATENDIMENTO + ".codigoTipoAtendimento")
                        .is(codigoTipoEstabelecimento)));

//        operacoesAgregacao.add(Aggregation.match(Criteria.where(LOCAIS_ATENDIMENTO + ".tipoAtendimento" +
//                        ".especialidades.codigoEspecialidade")
//                .all(listaEspecialidades)));

operacoesAgregacao.add(Aggregation.match(Criteria.where(ESPECIALIDADES +
                        ".codigoEspecialidade")
                .all(listaEspecialidades)));

        operacoesAgregacao.add(Aggregation.match(Criteria.where(LOCAIS_ATENDIMENTO + ".codigoAcomodacao")
                .is(codigoAcomodacao)));

        operacoesAgregacao.add(Aggregation.match(Criteria.where(LOCAIS_ATENDIMENTO + ".codigoRegiao")
                .is(codigoRegiao)));

        operacoesAgregacao.add(Aggregation.match(Criteria.where(LOCAIS_ATENDIMENTO + ".local")
                .withinSphere(new Circle(longitude,latitude, raio/RAIO_TERRA_KM))));
        int indiceFiltroPorRaio = operacoesAgregacao.size() - 1;

        if("S".equals(instaAdapt)) {
            operacoesAgregacao.add(Aggregation.match(Criteria.where(LOCAIS_ATENDIMENTO + ".instaAdapt")
                    .is(instaAdapt)));
        }
        adicionarParametroOpcional(operacoesAgregacao, listaBairros, LOCAIS_ATENDIMENTO + ".bairro");

        operacoesAgregacao.add(Aggregation.group(LOCAIS_ATENDIMENTO + ".bairro")
                .count().as("qtd")
        );


        Aggregation aggregation = Aggregation.newAggregation(operacoesAgregacao);

        AggregationResults<TotalReferenciadosPorBairro> countResults = mongoTemplate.aggregate(aggregation,"prestadores", TotalReferenciadosPorBairro.class);

        ResponseDTO response = new ResponseDTO(countResults.getMappedResults());

        int totalReferenciados = response.getTotalReferenciados();

        if(totalReferenciados == 0) {
            raio = segundoRaio;
            operacoesAgregacao.set(
                    indiceFiltroPorRaio, Aggregation.match(Criteria.where(LOCAIS_ATENDIMENTO + ".local")
                            .withinSphere(new Circle(longitude,latitude, raio/RAIO_TERRA_KM)))
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

        ProjectionOperation projectionOperation;
        projectionOperation = Aggregation.project()
                .and("_id").as("id")
                .and("codigoPrestador").as("codigoPrestador")
                .and(LOCAIS_ATENDIMENTO + ".codigoLocalAtendimento").as("codigoLocalAtendimento")
//                .and(TIPO_ATENDIMENTO + ".codigoTipoAtendimento").as("codigoTipoAtendimento")
                .and(LOCAIS_ATENDIMENTO + ".ranking").as("ranking")
                .and(LOCAIS_ATENDIMENTO + ".local").as("posicao");

        operacoesAgregacao.add(projectionOperation);


        aggregation = Aggregation.newAggregation(operacoesAgregacao);

        AggregationResults<DadosOrdenacaoDTO> providersSortingResults = mongoTemplate.aggregate(aggregation,"prestadores", DadosOrdenacaoDTO.class);

        List<DadosOrdenacaoDTO> dadosOrdenacao = new ArrayList<>(providersSortingResults.getMappedResults());

        response.setRaio((int) raio);
        response.setTotalPaginas( (int) Math.ceil( (double) totalReferenciados / referenciadosPorPagina));


        if(totalPaginas >= response.getTotalPaginas()){
            throw new GenericException("Há prestadores somente até a página de número " + (response.getTotalPaginas()) + ".", HttpStatus.NOT_FOUND);
        }

        calcularDistanciaPrestadores(dadosOrdenacao, latitude, longitude);

        dadosOrdenacao.sort(new ComparatorOrdenacao());

        List<DadosOrdenacaoDTO> dadosPaginados = OrdenacaoService.paginarLista(dadosOrdenacao, totalPaginas, referenciadosPorPagina);

        operacoesAgregacao.clear();


//        List<Criteria> listaCriterios = Collections.synchronizedList(new ArrayList<>());
////        List<Criteria> listaCriterios = new CopyOnWriteArrayList<>();
//
//        dadosPaginados.parallelStream().forEach(referenciado -> {
//            Criteria criterio = new Criteria().andOperator(
//                Criteria.where("_id").is(referenciado.getId()),
//                Criteria.where(LOCAIS_ATENDIMENTO + ".codigoLocalAtendimento").is(referenciado.getCodigoLocalAtendimento())
//            );
//            synchronized (listaCriterios) {
//                listaCriterios.add(criterio);
//            }
//        });

//        List<Criteria> listaCriterios = new ArrayList<>();
        List<String> listaIds = new ArrayList<>();
        dadosPaginados.forEach(ref -> listaIds.add(ref.getId()));

        List<Integer> listaCodigosLocalAtendimento = new ArrayList<>();
        dadosPaginados.forEach(ref -> listaCodigosLocalAtendimento.add(ref.getCodigoLocalAtendimento()));

//        dadosPaginados.forEach(referenciado -> {
//            Criteria criterio = new Criteria().andOperator(
//                    Criteria.where("_id").is(referenciado.getId()),
//                    Criteria.where(LOCAIS_ATENDIMENTO + ".codigoLocalAtendimento").is(referenciado.getCodigoLocalAtendimento())
//            );
//            listaCriterios.add(criterio);
//        });

        operacoesAgregacao.add(Aggregation.match(Criteria.where("_id").in(listaIds)));
        operacoesAgregacao.add(Aggregation.unwind(LOCAIS_ATENDIMENTO));
        operacoesAgregacao.add(Aggregation.match(Criteria.where(LOCAIS_ATENDIMENTO + ".codigoLocalAtendimento").in(listaCodigosLocalAtendimento)));
        operacoesAgregacao.add(Aggregation.unwind(TIPO_ATENDIMENTO));
        operacoesAgregacao.add(Aggregation.match(Criteria.where(TIPO_ATENDIMENTO + ".codigoTipoAtendimento").is(codigoTipoEstabelecimento)));



//        operacoesAgregacao.add(Aggregation.match(new Criteria().orOperator(listaCriterios.toArray(new Criteria[0]))));

//        operacoesAgregacao.add(Aggregation.unwind(LOCAIS_ATENDIMENTO));

////        listaCriterios.clear();
//        dadosPaginados.forEach(referenciado -> {
//            Criteria criterio = Criteria.where(LOCAIS_ATENDIMENTO + "codigoLocalAtendimento").is(referenciado
//                    .getCodigoLocalAtendimento());
//            listaCriterios.add(criterio);
//        });

//        operacoesAgregacao.add(Aggregation.match(new Criteria().orOperator(listaCriterios.toArray(new Criteria[0]))));
//
//        operacoesAgregacao.add(Aggregation.unwind(TIPO_ATENDIMENTO));
//
//        listaCriterios.clear();
//        dadosPaginados.forEach(referenciado -> {
//            Criteria criterio = Criteria.where(TIPO_ATENDIMENTO + "codigoTipoAtendimento").is(referenciado
//                    .getCodigoTipoAtendimento());
//            listaCriterios.add(criterio);
//        });
//
//        operacoesAgregacao.add(Aggregation.match(new Criteria().orOperator(listaCriterios.toArray(new Criteria[0]))));


//        operacoesAgregacao.add(Aggregation.sort(Sort.Direction.ASC,  "locaisAtendimento.ranking", "codigoPrestador"));
//        operacoesAgregacao.add(Aggregation.skip((long) pageable.getOffset()));
//        operacoesAgregacao.add(Aggregation.limit(pageable.getPageSize()));

        projectionOperation = Aggregation.project()
                .and("_id").as("id")
                .and(LOCAIS_ATENDIMENTO + ".nomeDivulgado").as("nomeFantasia")
                .and(ESPECIALIDADES + ".codigoEspecialidade").as("especialidades.codigoEspecialidade")
                .and(LOCAIS_ATENDIMENTO + ".qualificacoes").as("listaQualificacoes")
//                .and(LOCAIS_ATENDIMENTO + ".qualificacoes.nomeQualificacao").as("listaQualificacoes.descricao")
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
                .and(LOCAIS_ATENDIMENTO + ".redesPrestador").as("redesPrestador.codigoRede")
                .and(LOCAIS_ATENDIMENTO + ".ranking").as("ranking")
                .andExpression("map(conselhosMedicos, 'conselho', '$$conselho.numeroInscricaoConselho')").as("conselhosMedicos");
//                .andInclude("conselhosMedicos.numeroInscricaoConselho");

        operacoesAgregacao.add(projectionOperation);

        aggregation = Aggregation.newAggregation(operacoesAgregacao);

        AggregationResults<ReferenciadoDTO> providersResults = mongoTemplate.aggregate(aggregation,"prestadores", ReferenciadoDTO.class);

        List<ReferenciadoDTO> listaDesordenada = providersResults.getMappedResults();

//        List<ReferenciadoDTO> listaFinalOrdenada = new ArrayList<>(ordenarListaFinal(dadosPaginados, listaDesordenada));
        calcularDistanciaPrestadores(listaDesordenada, latitude, longitude);
        List<ReferenciadoDTO> listaModificavel = new ArrayList<>(listaDesordenada);
        listaModificavel.sort(new ComparatorOrdenacao2<>());

//        response.setListaReferenciados(listaFinalOrdenada);
        response.setListaReferenciados(listaDesordenada);

        response.setRaio((int) raio);
        response.setTotalPaginas((int) Math.ceil( (double) totalReferenciados / referenciadosPorPagina));

        return response;

    }

    private void adicionarParametroOpcional(List<AggregationOperation> filtros, List<?> parametro, String campoMongo) {
        if(parametro != null && !parametro.isEmpty()) {
            filtros.add(Aggregation.match(Criteria.where(campoMongo).in(parametro)));
        }
    }

//    private void calcularDistanciaPrestadores(List<DadosOrdenacaoDTO> listaPrestadores, double latitudeRequest, double longitudeRequest){
//        listaPrestadores.parallelStream().forEach(prestador -> {
//            double latitudePrestador = prestador.getPosicao().getY();
//            double longitudePrestador = prestador.getPosicao().getX();
//            double distancia = CalculoHaversine.CalcularDistancia(latitudeRequest, longitudeRequest, latitudePrestador, longitudePrestador);
//            prestador.setDistancia(distancia);
//        });
//    }

    private void calcularDistanciaPrestadores(List<? extends Ordenavel> listaPrestadores, double latitudeRequest, double longitudeRequest){
        double fator = Math.pow(10, 3); // Para 3 casas decimais
        listaPrestadores.parallelStream().forEach(prestador -> {
            double latitudePrestador = prestador.getPosicao().getY();
            double longitudePrestador = prestador.getPosicao().getX();
            double distancia = CalculoHaversine.calcularDistancia(latitudeRequest, longitudeRequest, latitudePrestador, longitudePrestador);
            distancia = Math.round(distancia * fator) / fator;
            prestador.setDistancia(distancia);
        });
    }

//    private List<ReferenciadoDTO> ordenarListaFinal2(List<DadosOrdenacaoDTO> listaOrdenacaoPrevia, List<ReferenciadoDTO> listaDesordenada) {
//        double fator = Math.pow(10, 3); // Para 3 casas decimais
//        List<ReferenciadoDTO> listaFinalOrdenada = new ArrayList<>();
//        listaOrdenacaoPrevia.forEach(prestador -> {
//            String idRefOrdenado = prestador.getId();
//            int codLocRefOrdenado = prestador.getCodigoLocalAtendimento();
//            for(ReferenciadoDTO referenciadoDTO : listaDesordenada) {
//                String idRefDesordenado = referenciadoDTO.getId();
//                int coodLocRefDesordenado = referenciadoDTO.getCodigoLocalAtendimento();
//                if (idRefDesordenado.equals(idRefOrdenado) && coodLocRefDesordenado == codLocRefOrdenado) {
//                    referenciadoDTO.setDistancia(Math.round(prestador.getDistancia() * fator) / fator);
//                    listaFinalOrdenada.add(referenciadoDTO);
//                }
//            }
//        });
//        return listaFinalOrdenada;
//    }

    private List<ReferenciadoDTO> ordenarListaFinal(List<DadosOrdenacaoDTO> listaOrdenacaoPrevia, List<ReferenciadoDTO> listaDesordenada) {
        double fator = Math.pow(10, 3);
        Map<String, ReferenciadoDTO> mapaDesordenado = new HashMap<>();
        listaDesordenada.forEach(ref -> mapaDesordenado.put(ref.getId() + "_" + ref.getCodigoLocalAtendimento(), ref));

        List<ReferenciadoDTO> listaFinalOrdenada = listaOrdenacaoPrevia.stream().map(ref-> {
          String chave = ref.getId() + "_" + ref.getCodigoLocalAtendimento();
          ReferenciadoDTO correspondente = mapaDesordenado.get(chave);
          if (correspondente != null){
//              String stringDistanciaArredondada = ((double) Math.round((ref.getDistancia() * fator) / fator)) + " Km";
              correspondente.setDistancia(Math.round(ref.getDistancia() * fator) / fator);
//              correspondente.setDistancia(stringDistanciaArredondada);
          }
          return correspondente;
        }).collect(Collectors.toList());

        return listaFinalOrdenada;
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
