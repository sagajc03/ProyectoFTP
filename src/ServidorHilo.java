import java.io.*;

public class ServidorHilo extends Thread{

    private DataInputStream in;
    private DataOutputStream out;
    private String instruccion;

    private final String PATH = "C:\\Users\\sagaj\\ServerFTP";
    private String path = "C:\\Users\\sagaj\\ServerFTP";

    public ServidorHilo(DataInputStream in, DataOutputStream out, String instruccion) {
        this.in = in;
        this.out = out;
        this.instruccion = instruccion;
    }

    @Override
    public void run(){
        String mensaje;
        String instruccion;
        String archivo;
        File f = new File(path+"\\");


        while (true){
            try {
                instruccion = in.readUTF();

                switch (instruccion.toLowerCase()){
                    case "ls":
                        mensaje = respuestaLS();
                        out.writeUTF(mensaje);
                        break;
                    case  "get":
                        manejarGET();
                        break;
                    case "put":
                        manejarPUT();
                        break;
                    case "cd" :
                        manejarCD();
                        break;
                    default:
                        out.writeUTF("Escriba un comando valido");
                        break;

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private String respuestaLS() {
        // PATH de los archivos a contar
        File archivo = new File(this.path);
        File[] elementos = archivo.listFiles();
        StringBuilder res = new StringBuilder();

        if (elementos != null) {
            // Iteramos a través de los elementos (archivos y carpetas)
            for (File elemento : elementos) {
                if (elemento.isDirectory()) {
                    // Es una carpeta
                    System.out.println("carpeta: " + elemento.getName());
                    // Agregamos nombres de carpetas a cadena res
                    res.append("Carpeta: ").append(elemento.getName()).append("\n");
                } else {
                    // Es un archivo
                    System.out.println("archivo: " + elemento.getName());
                    // Agregamos nombres de archivos a cadena res
                    res.append("Archivo: ").append(elemento.getName()).append("\n");
                }
            }
        } else {
            res.append("Archivero vacío");
        }

        return res.toString();
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

    private void manejarPUT() throws IOException {
        //Pregunta por un archivo
        out.writeUTF("Escribe el numbre del fichero> ");
        String archivo = in.readUTF();
        //Compureba que existe
        boolean existe = in.readBoolean();
        if (existe) {
            //Comprueba la cantidad de bytes
            int limiteFichero = in.readInt();
            byte[] contenidoFichero = new byte[limiteFichero];
            File f = new File(path + "\\" + archivo);

            for (int i = 0; i < limiteFichero; i++) {
                contenidoFichero[i] = in.readByte();
            }
            //Configura para mandar
            FileOutputStream fileOutputStream = new FileOutputStream(f.getPath());

            // Usar un BufferedOutputStream para escribir los bytes en el archivo
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                bufferedOutputStream.write(contenidoFichero);
                bufferedOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        else {
            System.out.println("No existe el archivo");
        }
    }

    private void manejarGET() throws IOException {
        out.writeUTF("Escribe el numbre del fichero> ");
        String archivo = in.readUTF();
        File f = new File(path+"\\"+archivo);
        if(f.exists() && !f.isDirectory()) {
            out.writeBoolean(true);
            byte[] contenidoFichero = convertirFileABytes(f);
            out.writeInt(contenidoFichero.length);
            for (int i = 0; i < contenidoFichero.length; i++) {
                out.writeByte(contenidoFichero[i]);
            }
        } else {
            out.writeBoolean(false);
            System.out.println("No existe el archivo o es una carpeta");
        }
    }

    private void manejarCD() throws IOException {
        //Pregunta por un archivo
        out.writeUTF("Directorio a entrar (\"..\" a raiz)> ");
        String directorio = in.readUTF();
        File f = new File(this.path + "\\" + directorio);
        if(f.exists() && f.isDirectory()) {
            if (directorio.equals("..")) {
                this.path = PATH;
            } else {
                this.path = path + "\\" + directorio;
            }
            out.writeUTF("Cambio de directorio realizado");
        } else{
            out.writeUTF("No se encontro el directorio");
        }
    }
}


