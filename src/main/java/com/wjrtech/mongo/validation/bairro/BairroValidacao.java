package com.wjrtech.mongo.validation.bairro;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class BairroValidacao implements ConstraintValidator <Bairro, List<String>>{
    @Override
    public void initialize(Bairro constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> listaBairros, ConstraintValidatorContext context) {
        if (listaBairros == null) return true;
        return !listaBairros.contains("");

//        if(listaBairros.size() == 1 && "".equals(listaBairros.get(0))) return false;
//        if(listaBairros.contains(""))
//        boolean StringVazia = false;
//        if(listaBairros.stream().forEach(bairro -> {
//            if("".equals(bairro)) vazio = true
//        })) return false;
    }
}
