package com.partnerpro.crud.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;

@PageTitle("Produtos")
@Route(value="", layout=MainLayout.class)
public class ProdutoListView extends VerticalLayout{
    private final ProdutoClient produtoClient;
    private final Grid<ProdutoDTO> grid = new Grid<>(ProdutoDTO.class);
    private final Dialog dialogProduto = new Dialog();
    private final Dialog dialogExclusao = new Dialog();
    private final Binder<ProdutoDTO> binder = new Binder<>(ProdutoDTO.class);
    private final String CURSOR_POINTER = "pointer";

    private TextField nomeField;
    private BigDecimalField precoField;
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
        novoProdutoButton.getStyle().setCursor(CURSOR_POINTER);
        add(novoProdutoButton, grid);
    }
    
    private void configurarGrid() {
        var formatoCurrencyBR = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        var formatoDataBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        grid.setColumns("nome", "categoria");
        grid.getColumnByKey("nome").setSortable(false);
        grid.addColumn(produto -> formatoCurrencyBR.format(produto.getPreco()))
            .setHeader("Preço")
            .setComparator(ProdutoDTO::getPreco)
            .setAutoWidth(true)
            .setSortable(true)
            .setTextAlign(ColumnTextAlign.END);

        grid.getColumnByKey("categoria").setSortable(false);
        
        grid.addColumn(produto -> formatoDataBR.format(produto.getDataCriacao()))
            .setHeader("Data de Criação")
            .setAutoWidth(true)
            .setTextAlign(ColumnTextAlign.END);
        
        grid.addComponentColumn(produto -> {
            var editarButton = new Button("Editar", e -> abrirDialogProduto(produto));
            editarButton.getStyle().setCursor(CURSOR_POINTER);
            
            var excluirButton = new Button("Excluir", e -> abrirDialogExclusao(produto));
            excluirButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            excluirButton.getStyle().setCursor(CURSOR_POINTER);

            var acoesColumnLayout = new HorizontalLayout(editarButton, excluirButton);
            acoesColumnLayout.setSpacing(true);
            acoesColumnLayout.setWidthFull();
            acoesColumnLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            
            return acoesColumnLayout;
        }).setHeader("Ações").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        
        
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
        precoField = new BigDecimalField("Preço (R$)");
        categoriaField = new TextField("Categoria");
        dataCriacaoField = new DatePicker("Data de Criação");
        dataCriacaoField.setMax(LocalDate.now());
        
        binder.forField(nomeField).asRequired("Nome é obrigatório").bind(ProdutoDTO::getNome, ProdutoDTO::setNome);
        binder.forField(categoriaField).asRequired("Categoria é obrigatória").bind(ProdutoDTO::getCategoria, ProdutoDTO::setCategoria);
        
        binder.forField(precoField)
            .asRequired("O preço é obrigatório")
            .withValidator(preco -> Objects.nonNull(preco) && preco.compareTo(BigDecimal.ZERO) > 0,
                           "O preço deve ser maior que zero")
            .bind(ProdutoDTO::getPreco, ProdutoDTO::setPreco);
        
        binder.forField(dataCriacaoField).asRequired("Data é obrigatória")
            .withValidator(data -> Objects.nonNull(data) && !data.isAfter(LocalDate.now()), "A data não pode ser no futuro")
            .bind(ProdutoDTO::getDataCriacao, ProdutoDTO::setDataCriacao);

        
        var form = new FormLayout(nomeField, precoField, categoriaField, dataCriacaoField);
        
        var salvarProdutoButton = new Button("Salvar", e -> salvarProduto());
        salvarProdutoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        salvarProdutoButton.getStyle().setCursor(CURSOR_POINTER);
        
        var cancelarButton = new Button("Cancelar", e -> dialogProduto.close());
        cancelarButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        cancelarButton.getStyle().setCursor(CURSOR_POINTER);
        
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
        simButton.getStyle().setCursor(CURSOR_POINTER);
        
        var naoButton = new Button("Não", e -> dialogExclusao.close());
        naoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        naoButton.getStyle().setCursor(CURSOR_POINTER);
        
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
            binder.writeBean(produtoAtual);
            produtoAtual.setNome(nomeField.getValue());
            produtoAtual.setCategoria(categoriaField.getValue());
            produtoAtual.setDataCriacao(dataCriacaoField.getValue());
            produtoAtual.setPreco(Objects.nonNull(precoField.getValue()) ? precoField.getValue() : null);
            
            if(Objects.isNull(produtoAtual.getId())) {
                produtoClient.cadastrarProduto(produtoAtual);
                Notification.show("Produto criado!", 3000, Notification.Position.TOP_CENTER);
            }
            else {
                produtoClient.atualizarProduto(produtoAtual.getId(), produtoAtual);
                Notification.show("Produto atualizado!", 3000, Notification.Position.TOP_CENTER);
            }
            
            carregarProdutos();
            dialogProduto.close();
        }
        catch(ValidationException e) {
            Notification.show("Preencha todos os campos corretamente!", 3000, Notification.Position.TOP_CENTER);
        }
        catch(Exception e) {
            Notification.show("Erro não esperado ao cadastrar/atualizar produto", 3000, Notification.Position.TOP_CENTER);
        }
    }
    
    private void abrirDialogProduto(ProdutoDTO produtoDTO) {
        this.produtoAtual = produtoDTO;
        
        nomeField.setValue(Objects.nonNull(produtoDTO.getNome()) ? produtoDTO.getNome() : "" );
        precoField.setValue(Objects.nonNull(produtoDTO.getPreco()) ? produtoDTO.getPreco() :  null);
        categoriaField.setValue(Objects.nonNull(produtoDTO.getCategoria()) ? produtoDTO.getCategoria() : "" );
        dataCriacaoField.setValue(produtoDTO.getDataCriacao());
        
        binder.readBean(produtoDTO);
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

