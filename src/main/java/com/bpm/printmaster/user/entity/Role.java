package com.bpm.printmaster.user.entity;

import lombok.Getter;

@Getter
public enum Role {

    SUPER_ADMIN("Modo dios"),
    ADMIN("Administrador"),
    OPERADOR("Operador"),
    USER("Usuario"),
    COBRADOR("Cobrador"),
    DISENADOR("Diseñador"),
    CONTADOR("Contador");



    private final String descripcion;

    Role(String descripcion) {
        this.descripcion = descripcion;
    }
}