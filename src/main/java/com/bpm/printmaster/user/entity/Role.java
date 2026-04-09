package com.bpm.printmaster.user.entity;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Administrador"),
    OPERADOR("Operador"),
    USER("Usuario");

    private final String descripcion;

    Role(String descripcion) {
        this.descripcion = descripcion;
    }
}