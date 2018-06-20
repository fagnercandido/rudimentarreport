package br.com.rudimentarreport.reports;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TableExport {

	String titleField();

	Class<?> preProccessField() default String.class;

	String nameField();
}
