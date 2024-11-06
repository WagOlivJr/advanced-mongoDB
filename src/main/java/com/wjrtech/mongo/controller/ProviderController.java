package com.wjrtech.mongo.controller;


import com.wjrtech.mongo.dto.RequestDTO;
import com.wjrtech.mongo.dto.ResponseDTO;
import com.wjrtech.mongo.entity.Prestador;
import com.wjrtech.mongo.exception.ErrorMessageListResponse;
import com.wjrtech.mongo.exception.GenericException;
import com.wjrtech.mongo.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/prestador")
@AllArgsConstructor
public class ProviderController {

    ProviderService providerService;

//    @GetMapping("/all")
//    public ResponseEntity<List<Prestador>> getAllProviders() {
//        return ResponseEntity.ok(providerService.getAllProviders());
//    }

//    @GetMapping("/allWithSortingAndPagination/{page}")
//    public ResponseEntity<List<Prestador>> getAllProvidersWithSortingAndPagination(@PathVariable int page) {
//        return ResponseEntity.ok(providerService.getAllProvidersWithSortingAndPagination(page));
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<List<Prestador>> getById(@PathVariable String id) {
//        return ResponseEntity.ok(providerService.getById(id));
//    }

//    @GetMapping("/{codigoPrestador}")
//    public ResponseEntity<ResponseDTO> getByProviderCode(@PathVariable Long codigoPrestador) {
//        return ResponseEntity.ok(providerService.getByProviderCode(codigoPrestador));
//    }

//    @PostMapping
//    public ResponseEntity<ResponseDTO> getProviders(@RequestBody RequestDTO requestDTO) {
//        return ResponseEntity.ok(providerService.getProviders(requestDTO));
//    }
//
//    @PostMapping("/validacaoRequest")
//    public ResponseEntity<RequestDTO> requestValidate(@Valid @RequestBody RequestDTO requestDTO) {
//        return new ResponseEntity<>(providerService.requestValidate(requestDTO), HttpStatus.OK);
//    }

//    @PostMapping("/teste")
//    public ResponseEntity<ResponseDTO> teste(@RequestBody RequestDTO requestDTO) {
//        return ResponseEntity.ok(providerService.teste(requestDTO));
//    }

    @GetMapping("/cl/healthcheck")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("OK" ,HttpStatus.OK);
    }


    @PostMapping("/buscarPrestadoresPorEspecialidade")
    @Operation(
            summary = "Busca referenciados por rede",
            description = "Endpoint para listar os referenciados do ramo saúde"
    )
    @ApiResponse(responseCode = "200", description = "Sucesso",
            content = @Content(schema = @Schema(implementation = ResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Requisição mal formatada.",
            content = @Content(schema = @Schema(implementation = ErrorMessageListResponse.class)))
    @ApiResponse(responseCode = "404", description = "Prestadores não encontrados.",
            content = @Content(schema = @Schema(implementation = GenericException.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno.")
    public ResponseEntity<ResponseDTO> anotherTest(@Valid @RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok(providerService.anotherTest(requestDTO));
    }
}
