import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorSimple {
    public static void main(String argv[]) throws Exception {

        ServerSocket socketdeEscucha = new ServerSocket(6789);
        Socket socketdeConexion = socketdeEscucha.accept();

        BufferedReader mensajeDesdeCliente = new BufferedReader(
                new InputStreamReader(socketdeConexion.getInputStream()));
        String lineaDeLaSolicitudHttp = mensajeDesdeCliente.readLine();

        StringTokenizer lineaSeparada = new StringTokenizer(lineaDeLaSolicitudHttp);
        if (lineaSeparada.nextToken().equals("GET")) {
            String nombreArchivo = lineaSeparada.nextToken();
            if (nombreArchivo.startsWith("/"))
                nombreArchivo = nombreArchivo.substring(1);

            File archivo = new File(nombreArchivo);
            FileInputStream archivoDeEntrada = new FileInputStream(nombreArchivo);
            int cantidadDeBytes = (int) archivo.length();
            byte[] archivoEnBytes = new byte[cantidadDeBytes];
            archivoDeEntrada.read(archivoEnBytes);

            DataOutputStream mensajeParaCliente = new DataOutputStream(socketdeConexion.getOutputStream());

            // Encabezado de respuesta HTTP
            mensajeParaCliente.writeBytes("HTTP/1.0 200 Document Follows\r\n");
            if (nombreArchivo.endsWith(".jpg"))
                mensajeParaCliente.writeBytes("Content-Type: image/jpeg\r\n");
            if (nombreArchivo.endsWith(".gif"))
                mensajeParaCliente.writeBytes("Content-Type: image/gif\r\n");
            mensajeParaCliente.writeBytes("Content-Length: " + cantidadDeBytes + "\r\n");

            // Línea vacía para indicar el fin de los encabezados
            mensajeParaCliente.writeBytes("\r\n");

            // Envío del contenido del archivo
            mensajeParaCliente.write(archivoEnBytes, 0, cantidadDeBytes);

            socketdeConexion.close();

        } else {
            System.out.println("Bad Request Message");
        }
    }
}