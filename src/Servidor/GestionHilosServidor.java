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
 
 File archivo =new File(Servidor.RUTA_ARCHIVO );
 
	public GestionHilosServidor(Socket socket) {//Guarda el socket de conexión con el cliente
	//super();
	this.socket = socket;
}

	@Override
	public void run() {//Obligatorio porque aquí quise usar Runnable
		try {
			//Creamos el flujo de datos, con el socket le permitimos recibir los mensajes del cliente
			DataInputStream in= new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			
			while(true) {
				String mensaje= in.readUTF();
				System.out.println("(Servidor )"+ mensaje);
				
				//Necesito trocear el mensaje para identificar sus partes
				String[] parte= mensaje.split(",");
				String orden= parte[0];
				String respuesta = "Error, no se ha podido identificar la orden.";
				switch(orden) {
				case"INSERTAR":
					respuesta=procesarInsercionNota(mensaje);
					break;
				case"MODIFICAR":
					break;
				case"CONSULTAR":
					respuesta= procesarConsultaNota(mensaje);
					break;
				case"ELIMINAR":
					break;
				case"SALIR":
					break;
					
				}out.writeUTF(respuesta);
				
			
			}
			
		
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	private String procesarInsercionNota(String mensaje) {
		//Con este método lo que logro es que separe el texto en trocitos, busque coincidencia con la 
		//instrucción INSERTAR y llame al método insertarNota que es el que hace la chicha de la inserción
		String[] parte= mensaje.split(",");
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
			BufferedWriter writer =new BufferedWriter(new FileWriter(archivo,true));//No sobre escribe, añade
			writer.write(nombre+","+ nota);
			writer.newLine();//Tengo que hacer saltar la línea
			writer.flush();//fuerza la escritura inmediata
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		return nota;//PROVISIONAL
	}
	public String procesarConsultaNota(String mensaje) {
		String parte[]=mensaje.split(",");
		if(parte.length<2)return "Error: formato no válido";
		String nombre =parte[1];
		String resultado= buscarNota(nombre);
		if(resultado==null) {
			return "El alumno no existe";
		}
		return resultado;
		
	
	}
	public String buscarNota(String nombre) {
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(archivo));
			String linea;
			while((linea=reader.readLine())!=null) {
				if(linea.trim().isEmpty()) continue;
				String[]parte= linea.split(",");
				if(parte.length<2)continue;
				if(parte[0].equals(nombre)) {
					String encontrado= "La nota del alumno "+ nombre+ " es "+parte[1];
					return encontrado;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "El alumno no existe";
		
		
	
	}
	

}
