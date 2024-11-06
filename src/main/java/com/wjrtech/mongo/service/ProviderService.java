package com.wjrtech.mongo.service;

import com.wjrtech.mongo.dto.RequestDTO;
import com.wjrtech.mongo.dto.ResponseDTO;
import com.wjrtech.mongo.entity.Prestador;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProviderService {
    List<Prestador> getAllProviders();
    List<Prestador> getAllProvidersWithSortingAndPagination(int page);

    ResponseDTO getByProviderCode(Long codigoPrestador);

    ResponseDTO getProviders(RequestDTO requestDTO);

    RequestDTO requestValidate(RequestDTO requestDTO);

    ResponseDTO teste(RequestDTO requestDTO);

    ResponseDTO anotherTest(RequestDTO request);

//    List<Prestador> getById(String id);
}
