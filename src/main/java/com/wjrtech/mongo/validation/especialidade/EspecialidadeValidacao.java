package com.wjrtech.mongo.validation.especialidade;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Max;
import java.util.List;

public class EspecialidadeValidacao implements ConstraintValidator<Especialidade, List<Integer>>  {
    @Override
    public void initialize(Especialidade constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<Integer> listaEspecialidades, ConstraintValidatorContext context) {
        if(listaEspecialidades == null) return true;
        return !listaEspecialidades.contains(null);
    }
}
