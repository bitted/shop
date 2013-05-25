package com.shop.mvc.controllers;

public class StaticController extends MainController {

    public void doIndex() {
        render();
    }

    public void doAbout() {
        assign("main.title", "Apie mus");
        render();
    }

    public void doHelp() {
        assign("main.title", "Pagalba");
        render();
    }
}