package com.partnerpro.crud.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout{
    public MainLayout() {
        createHeader();
        
        var homeLink = new RouterLink("Produtos", ProdutoListView.class);
        
        var dashboardLink = new RouterLink("Dashboard", DashboardView.class);
        
        var menuLayout = new VerticalLayout(homeLink, dashboardLink);
        menuLayout.setPadding(true);
        menuLayout.setSpacing(true);
        menuLayout.setHeightFull();
        menuLayout.setAlignItems(Alignment.CENTER);
        menuLayout.getStyle().setBackgroundColor("#F3F5F7");
        
        addToDrawer(menuLayout);
    }
    
    private void createHeader() {
        var titulo = new H1("Vaadin CRUD");
        titulo.getStyle().set("font-size", "1.5em").set("margin", "0.5em");
        
        var header = new HorizontalLayout(titulo);
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        
        addToNavbar(new DrawerToggle(), header);
    }
}
