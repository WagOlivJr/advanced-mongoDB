package com.wjrtech.mongo.dto;

import com.wjrtech.mongo.validation.bairro.Bairro;
import com.wjrtech.mongo.validation.especialidade.Especialidade;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class RequestDTO {
    @NotNull(message = "codigoRede é um parâmetro obrigatório")
    private Integer codigoRede;
    @NotNull(message = "codigoTipoEstabelecimento é um parâmetro obrigatório")
    private Integer codigoTipoEstabelecimento;
//    @NotNull(message = "listaEspecialidades deve conter de 1 a 5 códigos de especialidade")
    @Especialidade(message = "listaEspecialidades não pode conter item nulo.")
    @NotNull(message = "listaEspecialidades é um parâmetro obrigatório.")
    @Size(min = 1, max = 5, message = "listaEspecialidades deve conter de 1 a 5 códigos de especialidade")
    private List<Integer> listaEspecialidades;
    @NotNull(message = "codigoTipoReferenciado é um parâmetro obrigatório")
    private Integer codigoTipoReferenciado;
    @NotNull(message = "codigoAcomodacao é um parâmetro obrigatório")
    @Pattern(regexp = "[EQA]", message = "codigoAcomodação deve receber 'Q' - Quarto, 'E' - enfermaira ou 'A' - Ambos.")
    private String codigoAcomodacao;
    @NotNull(message = "codigoRegiao é um parâmetro obrigatório")
    private Integer codigoRegiao;
    @NotNull(message = "enderecoConsulta é um parâmetro obrigatório")
    @Valid
    private EnderecoConsulta enderecoConsulta;
    @NotNull(message = "instaAdapt é um parâmetro obrigatório")
    @Pattern(regexp = "[SN]", message = "o parâmetro 'instaAdapt' deve receber 'S' = endereços que tenham " +
            "instalações adaptadas ou 'N' = endereços com ou sem instalação adaptada)")
    private String instaAdapt;
    @Size(min = 1, max = 5, message = "listaBairros deve conter de 1 a 5 nomes de bairro.")
    @Bairro(message = "listaBairros não pode conter item vazio.")
    private List<String> listaBairros;

    @NotNull(message = "totalPaginas é um parâmetro obrigatório")
    @Min(value = 1, message = "O valor informado no parâmetro 'totalPaginas' não pode ser menor do que 1.")
    private Integer totalPaginas;
}
