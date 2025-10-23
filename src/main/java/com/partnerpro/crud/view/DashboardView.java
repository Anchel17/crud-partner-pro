package com.partnerpro.crud.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.partnerpro.crud.client.ProdutoClient;
import com.partnerpro.crud.dto.ProdutoDTO;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.TextAlign;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Dashboard")
@Route(value="/dashboard", layout=MainLayout.class)
public class DashboardView extends VerticalLayout{
    private final ProdutoClient produtoClient;
    private static final String CARD_WIDTH = "200px";
    private static final String CARD_HEIGHT = "150px";
    private static final String CARD_FONT_SIZE = "24px";
    
    @Autowired
    public DashboardView(ProdutoClient produtoClient) {
        this.produtoClient = produtoClient;
        
        setSpacing(true);
        setPadding(true);

        add(new Text("Dashboard"));
        
        var produtos = produtoClient.listarProdutos();
        
        var totalProdutos = produtos.size();
        var soma = produtos.stream()
                .map(ProdutoDTO::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        var precoMedio = totalProdutos > 0 ? soma.divide(BigDecimal.valueOf(totalProdutos), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        var formatoBR = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        var precoMedioFormatado = formatoBR.format(precoMedio);
        
        var cardTotalProdutos = criarBaseDoCard("Total de produtos");
        cardTotalProdutos.add(String.valueOf(totalProdutos));
        
        var cardPrecoMedio = criarBaseDoCard("Preço médio");
        cardPrecoMedio.add(precoMedioFormatado);
        
        var cardsLayout = new HorizontalLayout(cardTotalProdutos, cardPrecoMedio);
        cardsLayout.setSpacing(true);
        
        add(cardsLayout);
    }
    
    private Card criarBaseDoCard(String titulo) {
        var card = new Card();
        
        card.setTitle(titulo);
        card.addThemeVariants(CardVariant.LUMO_ELEVATED);
        card.setWidth(CARD_WIDTH);
        card.setHeight(CARD_HEIGHT);
        card.getStyle().setTextAlign(TextAlign.CENTER);
        card.getStyle().setFontSize(CARD_FONT_SIZE);
        
        return card;
    }
    
}
