package com.wjrtech.mongo.validation.especialidade;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EspecialidadeValidacao.class)
public @interface Especialidade {
    String message() default "The speciality is not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
