package com.ChatTCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class HiloCliente extends Thread {

    private Socket cliente;
    private ArrayList<String> mensajes;

    public HiloCliente(Socket cliente) {
        this.cliente = cliente;
        this.mensajes = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            InputStream entrada = cliente.getInputStream();
            OutputStream salida = cliente.getOutputStream();

            while (true) {
                byte[] buffer = new byte[1024];
                int bytesLeidos = entrada.read(buffer);

                if (bytesLeidos == -1) {
                    break;
                }

                String mensaje = new String(buffer, 0, bytesLeidos);
                mensajes.add(mensaje);

                for (String msj : mensajes) {
                    salida.write(msj.getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
