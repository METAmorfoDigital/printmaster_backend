package com.bpm.printmaster.cliente;

import jakarta.persistence.*;
import lombok.*;
 
@Entity
@Table(name = "clientes")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Cliente {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 150)
    private String nombre;
 
    @Column(length = 20)
    private String celular;
 
    @Column(length = 150)
    private String correo;
 
    @Column(length = 250)
    private String direccion;
}
 