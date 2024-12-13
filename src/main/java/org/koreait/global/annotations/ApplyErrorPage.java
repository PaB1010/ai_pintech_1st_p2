package org.koreait.global.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  범위는 거의 Rest 제외 @Controller 와 같도록 했음
 * 
 * 공통 Error 출력 처리
 * - ErrorPage 유입용 Annotation
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplyErrorPage {

}