package Annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.*;
import static java.lang.annotation.ElementType.*;
@Target(TYPE)
@Retention(SOURCE)
public @interface ExtractInterface {
public String value();
}
