package com.ChatTCP;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class VentanaChat extends JFrame {

    private static final int PUERTO = 54321;
    private static Socket socket;
    private JButton BotonEnviar;
    private JScrollPane PanelMensajes;
    private JPanel PanelPrincipal;
    private JTextField textField1;
    private JPanel Mensajes;

    public VentanaChat(String nombre) {

        setTitle("Chat");
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        BotonEnviar.addActionListener(e -> {
            String mensaje = textField1.getText();
            if (!mensaje.isBlank()) {
                try {
                    enviarMensaje(mensaje, socket);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                textField1.setText("");
            }
        });
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String mensaje = textField1.getText();
                    if (!mensaje.isBlank()) {
                        try {
                            enviarMensaje(mensaje, socket);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        textField1.setText("");
                    }
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        try {
            socket = new Socket("localhost", PUERTO);
            enviarMensaje(nombre, socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            while (true) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String texto;
                    while ((texto = in.readLine()) != null) {
                        anadirMensaje(texto);
                    }
                } catch (IOException ignored) {}
            }
        }).start();
    }

    public static void enviarMensaje(@NotNull String mensaje, @NotNull Socket socket) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(mensaje);
    }

    public void anadirMensaje(String mensaje) {
        JLabel label = new JLabel(mensaje.trim());

        try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/JetBrainsMono-Regular.ttf")) {
            if (fontStream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                label.setFont(font.deriveFont(Font.PLAIN, 16));
            } else {
                System.err.println("No se pudo cargar la fuente.");
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        Mensajes.setLayout(new BoxLayout(Mensajes, BoxLayout.Y_AXIS));
        Mensajes.add(label);
        PanelMensajes.setViewportView(Mensajes);
        PanelMensajes.getVerticalScrollBar().setValue(PanelMensajes.getVerticalScrollBar().getMaximum());
    }

}