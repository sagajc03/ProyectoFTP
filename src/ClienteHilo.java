import java.io.*;
import java.util.Scanner;

public class ClienteHilo extends Thread{
    private DataInputStream in;
    private DataOutputStream out;
    private String path = "C:\\Users\\sagaj\\ClienteFTP";

    public ClienteHilo(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }
    @Override
    public void run(){
        String mensaje;
        Scanner sn = new Scanner(System.in);
        String instruccion;
        String archivo;
        byte[] contenidoFichero;
        File f = new File(path+"\\");

        boolean salir = false;
        try {
            while (!salir) {
                System.out.print("LS, PUT, GET, SAlIR\nFTP> ");
                instruccion = sn.next();
                out.writeUTF(instruccion);
                switch (instruccion.toLowerCase()) {
                    case "ls":
                        mensaje = in.readUTF();
                        System.out.println(mensaje);
                        break;
                    case "get":
                        //recibe un archivo del servido
                        manejarGET(sn);
                        break;
                    case "put":
                        //envia un archivo al servidor
                        manejarPUT(sn);
                        break;
                    case "salir":
                        System.out.println("SAliendo");
                        salir=true;
                    case "cd":
                        mensaje = in.readUTF();
                        System.out.println(mensaje);
                        String directorio = sn.next();
                        out.writeUTF(directorio);
                        System.out.println(in.readUTF());

                        break;
                    default:
                        mensaje = in.readUTF();
                        System.out.println(mensaje);
                        break;

                }
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }
    private byte[] convertirFileABytes(File f) throws FileNotFoundException {
        // Crear un InputStream para leer el archivo
        InputStream inputStream = new FileInputStream(f);

        // Crear un arreglo de bytes para almacenar el contenido del archivo
        byte[] byteArray = new byte[(int) f.length()];

        // Usar un BufferedInputStream para leer el archivo
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            int bytesRead = bufferedInputStream.read(byteArray, 0, byteArray.length);
            if (bytesRead != byteArray.length) {
                System.out.println("No se pudo leer todo el archivo.");
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArray;
    }

    private void manejarPUT(Scanner sn) throws IOException {
        String mensaje = in.readUTF();
        System.out.println(mensaje);
        String archivo = sn.next();
        out.writeUTF(archivo);
        File f = new File(path+"\\"+archivo);
        boolean existe;
        if (f.exists()) {
            out.writeBoolean(true);
            byte[] contenidoFichero = convertirFileABytes(f);
            out.writeInt(contenidoFichero.length);
            for (int i = 0; i < contenidoFichero.length; i++) {
                out.writeByte(contenidoFichero[i]);
            }
        }else{
            out.writeBoolean(false);
            System.out.println("No existe el archivo");
        }
    }

    private void manejarGET(Scanner sn) throws IOException {
        String mensaje = in.readUTF();
        System.out.println(mensaje);
        String archivo = sn.next();
        out.writeUTF(archivo);
        boolean existe = in.readBoolean();
        if (existe) {
            int limiteFichero = in.readInt();
            byte[] contenidoFichero = new byte[limiteFichero];
            File f = new File(path + "\\" + archivo);
            for (int i = 0; i < limiteFichero; i++) {
                contenidoFichero[i] = in.readByte();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(f.getPath());

            // Usar un BufferedOutputStream para escribir los bytes en el archivo
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                bufferedOutputStream.write(contenidoFichero);
                bufferedOutputStream.flush();
            }
        } else{
            System.out.println("El archivo no existe");
        }
    }
}


