package Servidor;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	private ServerSocket serverSocket;//Guarda el serverSocket para aceptar conexiones
	protected static final String RUTA_ARCHIVO="notas.txt";
	//Le doy la ruta donde voy a crear el archivo sino existe
	
	public Servidor (int puerto) throws IOException {
		serverSocket =new ServerSocket(puerto);//Esto deja el servidor a la escucha al crearlo
		//Ahora hago que el servidor cree el archivo en caso de que no exista
		File archivo= new File(RUTA_ARCHIVO);
		if(!archivo.exists()) {
			archivo.createNewFile();
			System.out.println("Se ha creado el archivo");
		}else {
			System.out.println("El archivo ya existe");
		}
		
		
		
		
		while(true) {
			Socket socket=serverSocket.accept();//Acepta las conexiones
			
			System.out.println("(Servidor) Conexión establecida...");
			new Thread(new GestionHilosServidor(socket)).start();//Crea un hilo por cada cliente

			
			
		}
		
		
		
	}
	public static void main(String[] args) {
        try {
            new Servidor(5000);
        } catch (IOException e) {
            System.out.println("(Servidor) Error al iniciar: " + e.getMessage());
        }
    }

}
