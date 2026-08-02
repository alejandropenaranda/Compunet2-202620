package MultiThreadServer.Parte1;

import java.io.*;
import java.net.*;
import java.util.*;

public final class ServidorWebMultihilosParte1 {

    public static void main(String[] args) throws Exception {

        int puerto = 6789;
        ServerSocket socketdeEscucha = new ServerSocket(puerto);

        System.out.println("Servidor escuchando en el puerto " + puerto);

        while (true) {

            // Espera una nueva conexión
            Socket socketdeConexion = socketdeEscucha.accept();

            // Crea un hilo para atenderla
            SolicitudHttp solicitud = new SolicitudHttp(socketdeConexion);
            Thread hilo = new Thread(solicitud);

            hilo.start();
        }
    }
}

final class SolicitudHttp implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public SolicitudHttp(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implementa el método run() de la interface Runnable.
    public void run() {
        try {
            proceseSolicitud();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void proceseSolicitud() throws Exception {
        // Referencia al stream de salida del socket.
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // Referencia y filtros (InputStreamReader y BufferedReader)para el stream de
        // entrada.
        BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // Recoge la línea de solicitud HTTP del mensaje.
        String lineaDeSolicitud = br.readLine();

        // Muestra la línea de solicitud en la pantalla.
        System.out.println();
        System.out.println(lineaDeSolicitud);

        // recoge y muestra las líneas de header.
        String lineaDelHeader = null;
        while ((lineaDelHeader = br.readLine()).length() != 0) {
            System.out.println(lineaDelHeader);
        }

        // Cierra los streams y el socket.
        os.close();
        br.close();
        socket.close();

    }
}