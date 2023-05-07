module client {
    requires MaterialFX;
    requires VirtualizedFX;
    requires com.google.gson;
    requires jdk.localedata;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires commons.collections;
    requires org.apache.commons.lang;
    requires fr.brouillard.oss.cssfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.scenicview.scenicview;
    requires javafx.base;
    requires java.rmi;
    requires org.junit.jupiter.api;
    requires jakarta.persistence;
    exports chess.client;
    exports chess.client.sharedCode to java.rmi;
    opens chess.client.controllers;
    opens chess.client.board;
    opens chess.client.controllers.pages;
    exports chess.client.controllers;
    exports chess.client.sharedCode.communication to java.rmi;
    exports chess.client.sharedCode.gamerelated to java.rmi;
}