package com.partnerpro.crud.service;

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
    
    public List<Produto>buscarTodosProdutos() {
        return this.produtoRepository.findAllByOrderByIdDesc(); 
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
}
