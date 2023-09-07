import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        Scanner sn = new Scanner(System.in);
        sn.useDelimiter("\n");

        try {
            Socket sc = new Socket("127.0.0.1",5000);

            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            DataInputStream in = new DataInputStream(sc.getInputStream());

            String mensaje = in.readUTF();
            System.out.print(mensaje);

            String instruccion = sn.next();
            out.writeUTF(instruccion);

            ClienteHilo hilo = new ClienteHilo(in, out);
            hilo.start();
            hilo.join();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
