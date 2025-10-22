package com.partnerpro.crud.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProdutoDTO {
    Long id;
    
    @NotBlank
    String nome;
    
    @NotNull
    @DecimalMin(value="0.0", inclusive=false)
    BigDecimal preco;
    
    @NotBlank
    String categoria;
    
    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dataCriacao;
}
