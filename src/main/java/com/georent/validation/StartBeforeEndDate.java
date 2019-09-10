package com.georent.validation;

import com.georent.dto.RentOrderDTO;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { StartBeforeEndDateValidator.class})
@Documented
public @interface StartBeforeEndDate {
    String message() default "Start date should be before end date";

    /**
     * The name of start DateTime field in the target class.
     * @return
     */
    String start() default "startTime";

    /**
     * The name of end DateTime field in the target class.
     * @return
     */
    String end() default "endTime";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
