package com.docmala.plugins.document;

import java.lang.annotation.*;

@Repeatable(Plugins.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {
    String value(); // name
    String defaultParameters() default "";
}
