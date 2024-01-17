package com.service.payment.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class FieldViolation {

    private String field;

    private String objectName;

    private Object rejectedValue;

    private String message;
}
