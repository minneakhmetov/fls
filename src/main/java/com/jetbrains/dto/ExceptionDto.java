package com.jetbrains.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDto {
    String timestamp;
    Integer status;
    String error;
    String message;
    String path;
}
