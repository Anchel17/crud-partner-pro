package com.partnerpro.crud.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.partnerpro.crud.client.ProdutoClient;
import com.partnerpro.crud.dto.ProdutoDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Produtos")
@Route(value="", layout=MainLayout.class)
public class ProdutoListView extends VerticalLayout{
    private final ProdutoClient produtoClient;
    private final Grid<ProdutoDTO> grid = new Grid<>(ProdutoDTO.class);
    private final Dialog dialogProduto = new Dialog();
    private final Dialog dialogExclusao = new Dialog();

    private TextField nomeField;
    private NumberField precoField;
    private TextField categoriaField;
    private DatePicker dataCriacaoField;

    private List<ProdutoDTO>produtos;
    private ProdutoDTO produtoAtual;
    
    @Autowired
    public ProdutoListView(ProdutoClient produtoClient) {
        this.produtoClient = produtoClient;
        
        configurarGrid();
        configurarDialogProduto();
        configurarDialogExclusao();
        carregarProdutos();
        
        var novoProdutoButton = new Button("Novo Produto", e -> abrirDialogProduto(new ProdutoDTO()));
        novoProdutoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        novoProdutoButton.getStyle().setCursor("pointer");
        add(novoProdutoButton, grid);
    }
    
    private void configurarGrid() {
        grid.setColumns("nome", "preco", "categoria", "dataCriacao");
        grid.getColumnByKey("nome").setSortable(false);
        grid.getColumnByKey("preco").setHeader("Preço") ;
        grid.getColumnByKey("categoria").setSortable(false);
        grid.getColumnByKey("dataCriacao").setHeader("Data de Criação").setSortable(false);
        
        grid.addComponentColumn(produto -> new Button("Editar", e -> abrirDialogProduto(produto)));
        grid.addComponentColumn(produto -> {
             var excluirButton = new Button("Excluir", e -> abrirDialogExclusao(produto));
             excluirButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
             excluirButton.getStyle().setCursor("pointer");
             
             return excluirButton;
            });
        
        grid.setEmptyStateText("Sem produtos cadastrados.");
        
        var filtro = grid.appendHeaderRow();
        
        var filtroCategoriaField = new TextField();
        filtroCategoriaField.setPlaceholder("Digite a categoria...");
        filtroCategoriaField.setValueChangeMode(ValueChangeMode.LAZY);
        filtroCategoriaField.addValueChangeListener(e -> aplicarFiltroCategoria(filtroCategoriaField.getValue()));
        
        filtro.getCell(grid.getColumnByKey("categoria")).setComponent(filtroCategoriaField);
    }
    
    private void configurarDialogProduto() {
        dialogProduto.setHeaderTitle("Produto");
        
        nomeField = new TextField("Nome");
        precoField = new NumberField("Preço");
        categoriaField = new TextField("Categoria");
        dataCriacaoField = new DatePicker("Data de Criação");
        
        var form = new FormLayout(nomeField, precoField, categoriaField, dataCriacaoField);
        
        var salvarProdutoButton = new Button("Salvar", e -> salvarProduto());
        salvarProdutoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        salvarProdutoButton.getStyle().setCursor("pointer");
        
        var cancelarButton = new Button("Cancelar", e -> dialogProduto.close());
        cancelarButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancelarButton.getStyle().setCursor("pointer");
        
        var botoesModal = new HorizontalLayout(cancelarButton, salvarProdutoButton);
        botoesModal.setWidthFull();
        botoesModal.setJustifyContentMode(JustifyContentMode.END);
        
        var layout = new VerticalLayout(form, botoesModal);

        dialogProduto.add(layout);
        dialogProduto.setWidth("480px");
        
        add(dialogProduto);
    }
    
    private void configurarDialogExclusao() {
        dialogExclusao.setHeaderTitle("Deseja excluir o produto?");
        
        var simButton = new Button("Sim", e -> {
            produtoClient.deletar(produtoAtual.getId());
            carregarProdutos();
            Notification.show("Produto excluído!");
            dialogExclusao.close();
        });
        
        simButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        simButton.getStyle().setCursor("pointer");
        
        var naoButton = new Button("Não", e -> dialogExclusao.close());
        naoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        naoButton.getStyle().setCursor("pointer");
        
        var layout = new HorizontalLayout(naoButton, simButton);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.END);
        
        dialogExclusao.add(layout);
        dialogExclusao.setWidth("480px");
        add(dialogExclusao);
    }
    
    private void carregarProdutos() {
        produtos = produtoClient.listarProdutos();
        grid.setItems(produtos);
    }
    
    private void salvarProduto() {
        try {
            produtoAtual.setNome(nomeField.getValue());
            produtoAtual.setCategoria(categoriaField.getValue());
            produtoAtual.setDataCriacao(dataCriacaoField.getValue());
            produtoAtual.setPreco(Objects.nonNull(precoField.getValue()) ? BigDecimal.valueOf(precoField.getValue()) : null);
            
            if(Objects.isNull(produtoAtual.getId())) {
                produtoClient.cadastrarProduto(produtoAtual);
                Notification.show("Produto criado!");
            }
            else {
                produtoClient.atualizarProduto(produtoAtual.getId(), produtoAtual);
                Notification.show("Produto atualizado!");
            }
            
            carregarProdutos();
            dialogProduto.close();
        }
        catch(Exception e) {
            Notification.show(e.getMessage());
        }
    }
    
    private void abrirDialogProduto(ProdutoDTO produtoDTO) {
        this.produtoAtual = produtoDTO;
        
        nomeField.setValue(Objects.nonNull(produtoDTO.getNome()) ? produtoDTO.getNome() : "" );
        precoField.setValue(Objects.nonNull(produtoDTO.getPreco()) ? produtoDTO.getPreco().doubleValue() :  null);
        categoriaField.setValue(Objects.nonNull(produtoDTO.getCategoria()) ? produtoDTO.getCategoria() : "" );
        dataCriacaoField.setValue(produtoDTO.getDataCriacao());
        
        dialogProduto.open();
    }
    
    private void abrirDialogExclusao(ProdutoDTO produtoDTO) {
        this.produtoAtual = produtoDTO;
        
        dialogExclusao.open();
    }

    private void aplicarFiltroCategoria(String categoria) {
        if(Strings.isBlank(categoria)) {
            grid.setItems(produtos);
        }
        else {
            var termo = categoria.trim().toLowerCase();
            grid.setItems(produtos.stream().
                    filter(p -> Objects.nonNull(p.getCategoria()) && p.getCategoria().toLowerCase().contains(termo)).toList());
        }
    }
}

