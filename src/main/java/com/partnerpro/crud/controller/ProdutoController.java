package com.partnerpro.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partnerpro.crud.dto.ProdutoDTO;
import com.partnerpro.crud.entity.Produto;
import com.partnerpro.crud.service.ProdutoService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;
    
    @GetMapping(produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Produto>> buscarProdutos(){
        return ResponseEntity.ok(this.produtoService.buscarTodosProdutos());
    }
    
    @PostMapping(produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProdutoDTO> cadastrarProduto(@Valid @RequestBody ProdutoDTO produtoDTO) {
        return ResponseEntity.ok(this.produtoService.cadastrarProduto(produtoDTO));
    }
    
    @PutMapping(value="/{idProduto}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProdutoDTO> atualizarProduto(@PathVariable Long idProduto,  @Valid @RequestBody ProdutoDTO produtoDTO){
        try {
            var produto = this.produtoService.atualizarProduto(idProduto, produtoDTO);
            return ResponseEntity.ok(produto);
        }
        catch(IllegalStateException e) {
            log.error("[ATUALIZAR_PRODUTO][ERRO] - {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping(value="/{idProduto}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletarProduto(@PathVariable Long idProduto){
        try {
            this.produtoService.deletarProduto(idProduto);
            return ResponseEntity.ok().build();            
        }
        catch(IllegalStateException e) {
            log.error("[DELETAR_PRODUTO][ERRO] - {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
