package com.partnerpro.crud.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partnerpro.crud.dto.ProdutoDTO;
import com.partnerpro.crud.entity.Produto;
import com.partnerpro.crud.repository.ProdutoRepository;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;
    
    public List<ProdutoDTO>buscarTodosProdutos() {
        var produtos = this.produtoRepository.findAllByOrderByIdDesc(); 
        
        return transformarProdutosEntityEmProdutosDTO(produtos);
    }
    
    public ProdutoDTO cadastrarProduto(ProdutoDTO produtoDTO) {
        var produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setCategoria(produtoDTO.getCategoria());
        produto.setDataCriacao(produtoDTO.getDataCriacao());
        
        this.produtoRepository.save(produto);
        
        return produtoDTO;
    }
    
    public ProdutoDTO atualizarProduto(Long idProduto, ProdutoDTO produtoDTO) {
        var produto = this.produtoRepository.findById(idProduto)
                .orElseThrow(() -> new IllegalStateException("Produto com id {" + idProduto + "} não encontrado na base de dados"));
        
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setCategoria(produtoDTO.getCategoria());
        produto.setDataCriacao(produtoDTO.getDataCriacao());
        
        this.produtoRepository.save(produto);
        
        return produtoDTO;
    }
    
    public void deletarProduto(Long idProduto) {
        if(this.produtoRepository.findById(idProduto).isEmpty()) {
            throw new IllegalStateException("Produto com id {" + idProduto + "} não encontrado na base de dados");
        }
        
        this.produtoRepository.deleteById(idProduto);
    }
    
    public List<ProdutoDTO> transformarProdutosEntityEmProdutosDTO(List<Produto> produtos){
        List<ProdutoDTO> produtosDTO = new ArrayList<>();
        
        produtos.forEach(p -> {
            var produtoDTO = new ProdutoDTO();
            produtoDTO.setId(p.getId());
            produtoDTO.setNome(p.getNome());
            produtoDTO.setPreco(p.getPreco());
            produtoDTO.setCategoria(p.getCategoria());
            produtoDTO.setDataCriacao(p.getDataCriacao());
            
            produtosDTO.add(produtoDTO);
        });
        
        return produtosDTO;
    }
}
