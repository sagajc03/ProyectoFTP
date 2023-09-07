import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            Socket sc;

            System.out.println("Servidor iniciado");

            while (true){
                sc = server.accept();
                DataInputStream in = new DataInputStream(sc.getInputStream());
                DataOutputStream out = new DataOutputStream(sc.getOutputStream());

                out.writeUTF("Hola (devuelva el saludo)>");
                String instruccion = in.readUTF();
                ServidorHilo hilo = new ServidorHilo(in, out, instruccion);
                hilo.start();

                System.out.println("Conexion con cliente: " + sc + "\nInstruccion> " + instruccion) ;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}