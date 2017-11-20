package module;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag("long")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LongTest {
}
