package com.ChatTCP;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class VentanaChat extends JFrame {

    private JTextArea areaMensajes;
    private JTextField campoMensaje;
    private JButton botonEnviar;

    public VentanaChat(Socket cliente) {
        super("Chat");

        setSize(400,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaMensajes);

        campoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");

        JPanel panelSuperior = new JPanel();
        panelSuperior.add(campoMensaje);
        panelSuperior.add(botonEnviar);

        add(scrollPane, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.SOUTH);

        botonEnviar.addActionListener(e -> {
            String mensaje = campoMensaje.getText();
            areaMensajes.append(mensaje + "\n");
            campoMensaje.setText("");
        });
        new Thread(() -> {
            while (true) {
                try {
                    InputStream entrada = cliente.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesLeidos = entrada.read(buffer);

                    if (bytesLeidos == -1) {
                        break;
                    }

                    String mensaje = new String(buffer, 0, bytesLeidos);
                    areaMensajes.append(mensaje + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}