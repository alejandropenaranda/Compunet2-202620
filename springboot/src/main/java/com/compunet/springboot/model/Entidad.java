package com.compunet.springboot.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Entidad {

    private final String prop1;
    private String prop2;
    private final String prop3;

    @NonNull
    private Integer prop4;
    
}
