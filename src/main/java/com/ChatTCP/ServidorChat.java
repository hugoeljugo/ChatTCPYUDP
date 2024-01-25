package com.ChatTCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorChat {
    private static final int PUERTO = 9999;

    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(PUERTO);

        System.out.println("Servidor iniciado en el puerto " + PUERTO);

        while (true) {
            Socket cliente = servidor.accept();

            System.out.println("Cliente conectado");

            HiloCliente hilo = new HiloCliente(cliente);
            hilo.start();
        }
    }
}
