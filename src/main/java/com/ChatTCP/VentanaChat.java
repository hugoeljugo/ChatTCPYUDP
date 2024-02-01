package com.ChatTCP;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class VentanaChat extends JFrame {

    private JButton BotonEnviar;
    private JScrollPane PanelMensajes;
    private JPanel PanelPrincipal;
    private JTextField textField1;
    private JPanel Mensajes;
    private static MulticastSocket socket;
    private static String nombre;

    private static final int PUERTO = 12345;

    public VentanaChat(String nombre) {
        VentanaChat.nombre = nombre;

        setTitle("Chat");
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        BotonEnviar.addActionListener(e -> {
            String mensaje = String.format("%S: %s", nombre, textField1.getText());
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
                    String mensaje = String.format("%S: %s", nombre, textField1.getText());
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
                    String mensaje = String.format("%S ACABA DE ABANDONAR EL CHAT", nombre);
                    DatagramPacket dp = new DatagramPacket(mensaje.getBytes(), mensaje.length(),
                            InetAddress.getByName("225.0.0.1"), PUERTO);
                    socket.send(dp);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        try {
            socket = new MulticastSocket(PUERTO);
            InetAddress grupo = InetAddress.getByName("225.0.0.1");
            socket.joinGroup(grupo);
            String mensaje = String.format("%S ACABA DE UNIRSE AL CHAT", nombre);
            DatagramPacket dp = new DatagramPacket(mensaje.getBytes(), mensaje.length(),
                    InetAddress.getByName("225.0.0.1"), PUERTO);
            socket.send(dp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket entrada = new DatagramPacket(buffer, buffer.length);
                    socket.receive(entrada);

                    String texto = new String(entrada.getData());
                    anadirMensaje(texto);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
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

    public static void enviarMensaje(@NotNull String mensaje, @NotNull MulticastSocket socket) throws IOException {
        DatagramPacket dp = new DatagramPacket(mensaje.getBytes(), mensaje.length(),
                InetAddress.getByName("225.0.0.1"), PUERTO);
        socket.send(dp);
    }

}