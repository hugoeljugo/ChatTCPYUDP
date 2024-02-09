package com.ChatTCP;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Login extends JFrame {
    private final String host = "localhost";
    private final int puerto = 12345;
    private JTextField textField1;
    private JPanel panel1;
    private JPasswordField passwordField1;
    private JButton enviarButton;

    public Login() {
        setTitle("Chat");
        setContentPane(panel1);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setVisible(true);

        enviarButton.addActionListener(e -> {
            try (Socket socket = new Socket(host, puerto);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                String username = textField1.getText();

                String password = passwordField1.getText();

                writer.println(username);
                writer.println(password);

                String respuesta = reader.readLine();

                if (respuesta.equals("OK")) {
                    new VentanaChat(username);
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        new Login();
    }
}
