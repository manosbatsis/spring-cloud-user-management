package com.github.manosbatsis.lib.test.error;

public record FieldError(
    String code,
    String property,
    String rejectedValue,
    String path
){

}
