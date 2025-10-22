package com.partnerpro.crud.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID", nullable=false)
    private Long id;
    
    @Column(name="NOME", nullable=false)
    private String nome;
    
    @Column(name="PRECO", nullable=false)
    private BigDecimal preco;
    
    @Column(name="CATEGORIA", nullable=false)
    private String categoria;
    
    @Column(name="DATA_CRIACAO", nullable=false)
    private LocalDate dataCriacao;
}
