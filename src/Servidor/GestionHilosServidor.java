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
			String mensaje="Esta respuesta indica que no está entrando al bucle";
			
			while(true) {
				try{mensaje= in.readUTF();
				}catch(IOException e) {
					 System.out.println("(Servidor) Cliente desconectado inesperadamente.");
		                break;//Si el cliente se desconecta debería salir por aquí
				}
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
					respuesta=procesarModificacionNota(mensaje);
					break;
				case"CONSULTAR":
					respuesta= procesarConsultaNota(mensaje);
					break;
				case"ELIMINAR":
					respuesta=procesarEliminacionNota(mensaje);
					break;
				case"SALIR":
					System.out.println("(Servidor ) Cliente ha salido");
					in.close();
					out.close();
					socket.close();
					return;//Uso esto para que no rompa al salir
					
				}out.writeUTF(respuesta);
				
			
			}
			
		
		
		} catch (IOException e) {
			
			
			e.printStackTrace();
			 System.out.println("(Servidor) Cliente desconectado.");
             return; //
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
				if(parte[0].equalsIgnoreCase(nombre)&& parte.length>1) {
					return "Error, el alumno ya tiene nota";
				}
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			BufferedWriter writer =new BufferedWriter(new FileWriter(archivo,true));//No sobre escribe, añade
			writer.write(nombre+","+ nota);
			writer.newLine();//Tengo que hacer saltar la línea
			writer.flush();//fuerza la escritura inmediata
			writer.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
		return "Alumno " + nombre+ " tiene la nota: "+ nota;
	}
	public String procesarConsultaNota(String mensaje) {
		String parte[]=mensaje.split(",");
		if(parte.length<2)return "Error: formato no válido";
		String nombre =parte[1];
		String resultado= buscarNota(nombre);
		System.out.println("(BÓRRAME LUEGO) Resultado de buscarNota(): " + resultado);
		if(resultado==null || resultado.equals("El alumno no existe")) {
			return "El alumno no existe";
		}
		return resultado;
		
	
	}
	public String procesarModificacionNota(String mensaje) {
		String[]parte=mensaje.split(",");
		if(parte.length<3) return "Formato no válido";
		String nombre=parte[1];
		String nuevaNota=parte[2];
		return modificarNota(nombre, nuevaNota);
	}
	private String modificarNota(String nombre, String nuevaNota) {
		boolean encontrado= false;
		StringBuilder textoNuevo=new StringBuilder();//Almacena los cambios
		try {
			BufferedReader reader=new BufferedReader(new FileReader(archivo));
			String linea;
			while((linea=reader.readLine())!=null){
				String[]parte=linea.split(",");
				if (parte.length<2) continue;
				if (parte[0].equalsIgnoreCase(nombre) ) {
					textoNuevo.append(nombre).append(",").append(nuevaNota);//Modificamos
					encontrado=true;//
				}else {
					textoNuevo.append(linea);//Si no encuentra el alumno mantiene la línea
				}
				textoNuevo.append("\n");//Necesito este salto de línea
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!encontrado) {
			
			return "El alumno no existe o ya tiene nota asignada";
		}
		
		//Necesito escribirlo en el archivo. Ahora está almacenado en el StringBuilder
		try {
			BufferedWriter writer= new BufferedWriter(new FileWriter(archivo, false));//A false sobreescribe
			writer.write(textoNuevo.toString());//Escribe todo el contenido nuevo
			writer.flush(); //Obligo a escribir los datos porque si no sale en blanco
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return "Nota modificada correctamente";
	}
	public String buscarNota(String nombre) {
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(archivo));
			String linea;
			while((linea=reader.readLine())!=null) {
				 System.out.println("BÓRRAME) Línea leída: " + linea);
				if(linea.trim().isEmpty()) continue;
				String[]parte= linea.split(",");
				if(parte.length<1)continue;
				if(parte[0].equals(nombre)) {
					if(parte.length==1) {
						return"El alumno "+nombre+" no tiene nota asignada.";
					}else {
						return "La nota del alumno "+ nombre+ " es "+parte[1];
					}
					
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
	private String procesarEliminacionNota(String mensaje) {
		String[]parte=mensaje.split(",");
		if(parte.length<2) return "Formato no válido";
		String nombre=parte[1];
		return eliminarNota(nombre);
	}
	private String eliminarNota(String nombre) {
		boolean encontrado=false;
		StringBuilder nuevoTexto=new StringBuilder();
		
		try {
			BufferedReader reader= new BufferedReader(new FileReader(archivo));
			String linea;
			while((linea =reader.readLine())!=null) {
				String parte[]= linea.split(",");
				if(parte.length<2) {continue;}
				//Estoy leyendo el archivo, que sólo tiene 2 partes en el txt
				if(parte[0].equalsIgnoreCase(nombre)) {
					nuevoTexto.append(nombre).append(",");//Mantenemos el nombre
					encontrado=true;
				}else {
					nuevoTexto.append(linea).append("\n");
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!encontrado) {
			return "El alumno no existe";
			
			
		}
		//Ahora hay que reescribir el archivo
		try {
			BufferedWriter writer= new BufferedWriter(new FileWriter(archivo, false));
			writer.write(nuevoTexto.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Nota eliminada correctamente";
	}
	

}
