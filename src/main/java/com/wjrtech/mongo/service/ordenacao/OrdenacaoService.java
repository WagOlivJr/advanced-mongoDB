package com.wjrtech.mongo.service.ordenacao;

import com.wjrtech.mongo.dto.DadosOrdenacaoDTO;
import com.wjrtech.mongo.exception.GenericException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class OrdenacaoService {

    public static <T> List<T> paginarLista(List<T> listaOrdenacao, int numeroDaPagina, int referenciadosPorPagina) {
        int indiceInicial = numeroDaPagina * referenciadosPorPagina;
        int indiceFinal = Math.min(indiceInicial + referenciadosPorPagina, listaOrdenacao.size());
        if(indiceInicial>=listaOrdenacao.size()) throw new GenericException("Prestadores não encontrados.", HttpStatus.NOT_FOUND);
        return listaOrdenacao.subList(indiceInicial, indiceFinal);
    }

}

//Entendimento importante:
// Nesta classe, o Java exige que eu especifique o tipo de objecto que compõe a lista;
// Como quero que a classe aceite vários tipos de lista, quero indicar que o objeto que a compõe é genérico.
// (isso é possível somente porque não vou acessar nenhum atributo/metodo do objeto que compoe a lista)
// para indicar a classe genérico tenho as seguintes possibilidades:
    // Se o metodo que estou criando não for estático:
        // posso indicar a classe generica que compoe a lista no nome da classe: public class OrdenacaoService<T>{};
        // posso indicar a classe generica depois do 'public' do metodo: public <T> List<t> methodName(){};
    // Se o metodo for estatico, devo indicar a classe generica depois do 'public' do metodo: public <T> List<t> methodName(){};