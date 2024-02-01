package com.ChatTCP;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertData {
    private static final String URL = "jdbc:sqlite:myDatabase.db";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement agregar = connection.prepareStatement("INSERT INTO usuarios(username,password) VALUES(?,?)")) {

            String[] nombres = {"Juan", "Maria", "UnFrances", "que", "root", "Mario"};
            String[] contrasenas = {"Alberto", "DelMar", "Croissant", "so", "root", "Peach"};

            for (int i = 0; i < nombres.length; i++) {
                agregar.setString(1, nombres[i]);
                agregar.setString(2, contrasenas[i]);
                agregar.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
