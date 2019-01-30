package Annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface Constraints {
	
	boolean primaryKey() default false;

	boolean allowNull() default true;

	boolean unique() default false;
}
