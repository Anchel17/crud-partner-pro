package com.partnerpro.crud.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.partnerpro.crud.dto.ProdutoDTO;

@Component
public class ProdutoClient {
    private final RestTemplate restTemplate;
    private static final String API_URL = "http://localhost:8080/api/produto";
    
    public ProdutoClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public List<ProdutoDTO> listarProdutos(){
        var produtos = restTemplate.getForObject(API_URL, ProdutoDTO[].class);
        
        return Arrays.asList(produtos);
    }
    
    public ProdutoDTO cadastrarProduto(ProdutoDTO produtoDTO) {
        return restTemplate.postForObject(API_URL, produtoDTO, ProdutoDTO.class);
    }
    
    public ProdutoDTO atualizarProduto(Long idProduto, ProdutoDTO produtoDTO) {
        restTemplate.put(API_URL + "/" + idProduto, produtoDTO);
        
        return produtoDTO;
    }
    
    public void deletar(Long idProduto) {
        restTemplate.delete(API_URL + "/" + idProduto);
    }
}
