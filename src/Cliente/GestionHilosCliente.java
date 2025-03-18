package Cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
//Esta clase tiene que crear el menú y comunicarse con el servidor
public class GestionHilosCliente extends Thread{
	private DataInputStream in;
	private DataOutputStream out;
	private Socket socket;
//Va a recibir los flujos para guardarlos y usarlos en el menú
	public GestionHilosCliente(DataInputStream in, DataOutputStream out) {
		//super();
		this.in = in;
		this.out = out;
	}

	public void run() {
		Scanner sc =new Scanner(System.in);
		String opcion;
		do {
			System.out.println("\n--- Menú ---");
            System.out.println("1. Insertar nota");
            System.out.println("2. Modificar nota");
            System.out.println("3. Consultar nota");
            System.out.println("4. Eliminar nota");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            opcion=sc.nextLine();
            try {
            switch(opcion) {
           
            case "1":
            	insertarNota(sc);
            	break;
            case "2":
            	modificarNota(sc);
            	break;
            case "3":
            	consultarNota(sc);
            	break;
            case "4":
            	eliminarNota(sc);
            	break;
            case "5":
            	break;
            default:System.out.println("Opción no válida");
            	
            }
            }catch(IOException e) {
            	System.out.println("(Cliente) Error: "+e.getMessage());
            }
			
			
			
		}while(!opcion.equals("5"));
	
	}
	private void insertarNota(Scanner sc) throws IOException {
		//Método para solicitar la info al usuario
		System.out.println("Inserte el nombre del alumno");
		String nombre= sc.nextLine();
		System.out.println("Inserte la nota");
		String nota=sc.nextLine();
		out.writeUTF("INSERTAR,"+nombre +","+ nota);//Envía el mensaje al servidor
		String respuesta=in.readUTF();//Lee la respuesta del servidor
		System.out.println("(Servidor) "+ respuesta);
	}
	private void modificarNota(Scanner sc) throws IOException {
		System.out.println("Inserte el nombre del alumno");
		String nombre =sc.nextLine();
		System.out.println("Inserte la nueva nota");
		String nuevaNota=sc.nextLine();
		out.writeUTF("MODIFICAR,"+ nombre+","+nuevaNota);
	}
	
	private void consultarNota(Scanner sc) throws IOException{
		/*Este método tiene que pedir el nombre, mandarlo al servidor, esperar la respuesta y mostrarla por pantalla*/
		System.out.println("Inserte el nombre del alumno");
		String nombre=sc.nextLine();
		out.writeUTF("CONSULTAR,"+nombre);
		String respuesta=in.readUTF();
		System.out.println("(Servidor)"+ respuesta);
	}
	
	private void eliminarNota(Scanner sc) throws IOException {
		System.out.println("Inserte el nombre del alumno que desea eliminar");
		String nombre= sc.nextLine();
		out.writeUTF("ELIMINAR,"+nombre);
		String respuesta=in.readUTF();
		System.out.println("(Servidor) "+respuesta);
	}
	
	
	

}
