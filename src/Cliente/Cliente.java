package Cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
	private String serverIP;//la IP
	private int serverPort;//puerto del servidor
	private Socket socket;// puente de conexión
	private DataInputStream is;//Flujo lectura
	private DataOutputStream out;//flujo escritura
	public Cliente(String serverIP, int serverPort) {//Guarda los valores de IP y puerto para poder conectarse
		super();
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		
	}
	
	
	public void start() throws UnknownHostException, IOException {
		System.out.println("(Cliente) Estableciendo conexión...");
		socket=new Socket(serverIP, serverPort);//Creamos el puente
		is=new DataInputStream(socket.getInputStream());//Creamos el flujo de entrada y salida
		out=new DataOutputStream(socket.getOutputStream());
		System.out.println("(Cliente) Conexión establecida...");
		
		
		//Iniciamos el hilo para el cliente
		GestionHilosCliente hilo= new GestionHilosCliente(is, out);
		hilo.start();
		
	}

	
	
	public static void main(String[] args) {
		try {
            Cliente cliente = new Cliente("localhost", 5000);
            cliente.start();
            
        } catch (IOException e) {
            System.out.println("(Cliente) Error: " + e.getMessage());
        }
	}
//Aquí vamos a conectar con el servidor, crear los flujos e iniciar el hilo
}

	

