package com.ChatTCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorChatTCP {
    private static final int PUERTO = 54321;
    private static final List<Socket> clientes = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor TCP iniciado. Esperando conexiones");

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                clientes.add(clienteSocket);
                System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress().getHostAddress());

                new Thread(() -> manejarCliente(clienteSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manejarCliente(Socket clienteSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));

            String nombreCliente = reader.readLine();
            String bienvenida = String.format("%S ACABA DE UNIRSE AL CHAT", nombreCliente);
            enviarMensajeTodos(bienvenida);

            String mensajeCliente;
            while ((mensajeCliente = reader.readLine()) != null) {
                enviarMensajeTodos(nombreCliente + ": " + mensajeCliente);
            }

            clientes.remove(clienteSocket);
            clienteSocket.close();
            String despedida = String.format("%S HA ABANDONADO AL CHAT", nombreCliente);
            enviarMensajeTodos(despedida);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enviarMensajeTodos(String mensaje) {
        synchronized (clientes) {
            for (Socket cliente : clientes) {
                try {
                    PrintWriter writer = new PrintWriter(cliente.getOutputStream(), true);
                    writer.println(mensaje);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
