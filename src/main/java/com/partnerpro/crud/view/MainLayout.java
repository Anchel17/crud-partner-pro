package com.partnerpro.crud.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MainLayout extends AppLayout{
    public MainLayout() {
        createHeader();
    }
    
    private void createHeader() {
        var titulo = new H1("Vaadin Crud");
        titulo.getStyle().set("font-size", "1.5em").set("margin", "0.5em");
        
        var header = new HorizontalLayout(titulo);
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        
        addToNavbar(header);
    }
}
