package com.crezyman.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(value=RetentionPolicy.RUNTIME)
public @interface Table {
	public String name() default "";
	public String domain() default "";
}
