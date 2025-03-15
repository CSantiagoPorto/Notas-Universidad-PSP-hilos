package Servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GestionHilosServidor implements Runnable{
 private Socket socket;
 
	public GestionHilosServidor(Socket socket) {//Guarda el socket de conexión con el cliente
	//super();
	this.socket = socket;
}

	@Override
	public void run() {//Obligatorio porque aquí quise usar Runnable
		try {
			
			DataInputStream in= new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String mensaje;
	            while (true) {
	               mensaje =in.readUTF();
	               System.out.println("(Servidor: )"+ mensaje);
	               String respuesta =procesarInsercionNota(mensaje);
	               out.writeUTF(respuesta);//Envía la respuesta al cliente         
	            }
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	private String procesarInsercionNota(String mensaje) {
		//Con este método lo que logro es que separe el texto en trocitos, busque coincidencia con la 
		//instrucción INSERTAR y llame al método insertarNota que es el que hace la chicha de la inserción
		String[] parte= mensaje.split(";");
		if(parte.length<3) {
			return "No es un formato váido";
			}
		String orden=parte[0];
		String nombre =parte[1];
		String nota= parte[2];
		
		if(orden.equals("INSERTAR")) {
			return insertarNota(nombre, nota);
		}else return "Error: No se insertó la nota";
	}
	
	private String insertarNota(String nombre, String nota) {
		File archivo =new File(Servidor.RUTA_ARCHIVO );
		
		try {
			BufferedReader reader=new BufferedReader(new FileReader(archivo));
			String linea;
			while ((linea=reader.readLine())!=null) {
				String[] parte = linea.split(";");
				if(parte[0].equals(nombre)) {
					return "Error, el alumno ya tiene nota";
				}
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			BufferedWriter writer =new BufferedWriter(new FileWriter(archivo,true));
			writer.write(nombre+";"+ nota);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		return nota;//PROVISIONAL
	}
	

}
