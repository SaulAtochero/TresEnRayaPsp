package Cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Logica extends Thread {
    int jugador;// Identificador del jugador
    Socket estelado = null;// Socket para la conexión TCP
    DataOutputStream salida = null;// Stream para enviar datos
    DataInputStream entrada = null;// Stream para recibir datos
    Tablero t;
    Posicion p;
    boolean dichoSI=false;

    public Logica(Tablero pt, Posicion pp) {//constuctor para crear logica a partir de tablero y 
        this.t = pt;
        this.p = pp;
    }

    public void Conecto() {//conexion hacia el server
        boolean termino = false;

        while (!termino) {// Loop infinito hasta que conecte
            termino = true;

            try {
                this.estelado = new Socket("pspsaul.s2dam.com", 3030);//direccion del servidor y puerto
            } catch (IOException e) {
                termino = false;
                System.out.println("No me puedo conectar");
            }
        }
        //cuando se sale del whiled inicia los streams de envio y recibo de datoss
        try {
            this.salida = new DataOutputStream(this.estelado.getOutputStream());
            this.entrada = new DataInputStream(this.estelado.getInputStream());
        } catch (IOException e) {
            // Manejar excepción adecuadamente
        }
    }

    public int turno() { // Obtiene el turno del jugador desde el servidor
        int aux = -1;

        try {
            aux = this.entrada.readInt();
        } catch (IOException e) {
            
        }

        return aux;
    }

    public void inicio(int pj) {// Inicia el juego y el hilo de ejecución invocada desde inicio
        this.jugador = pj;
        this.start();
    }

    public void run() {// Método principal que maneja la logica del juego
        boolean miturno = this.jugador == 1;// Determina si es el turno del jugador

        while (this.t.hueco() && !this.t.enraya()) { // Mientras haya huecos y nadie haya ganado
            if (miturno) {//si es mi turno
                this.t.Activo();// Activa el tablero 
                boolean accionRealizada = this.p.espera();// Espera a que se le pulse un boton o pase un tiempo que pierda el turno
                if (accionRealizada) {//si me devuelve true por que a pulsado boton 
                    try {
                        this.salida.writeInt(1); // Indica que se ha realizado un movimiento
                        this.salida.writeInt(this.p.fila());
                        this.salida.writeInt(this.p.columna());
                    } catch (IOException e) {
                        System.out.println("Error al enviar movimiento: " + e.getMessage());
                    }
                } else {
                    try {
                    	System.out.println("Paso de turno");
                        this.salida.writeInt(0); // Indica ausencia de movimiento
                        this.salida.writeInt(0);
                        this.salida.writeInt(0);
                    } catch (IOException e) {
                        //System.out.println("Error al indicar ausencia de movimiento: " + e.getMessage());
                    }
                }
            } else {//si no es mi turno
                this.t.Desactivo();

                try {
                    int indicador = this.entrada.readInt();// Lee el indicador de movimiento
                    //System.out.println(indicador);
                    int fila = this.entrada.readInt();
                    int columna = this.entrada.readInt();
                    if (indicador == 1) {
                        this.t.Poner(fila, columna, this.p.otraletra());
                    } else{
                        System.out.println("No se recibió movimiento.");
                    }
                } catch (IOException e) {
                    //System.out.println("Error al recibir movimiento: " + e.getMessage());
                }
            }

            miturno = !miturno; // Cambia de turno
            try {
                Thread.sleep(500); // Espera medio segundo para el siguiente turno
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }   

        if (this.t.enraya()) {//Ejecuta todo el resto si hay enraya o empate
	        this.t.gano();
	        	if(this.t.PopUpSiNo()) {//Se ejecuta una ventana emergente para decir si quiere continuar jugando
	        		dichoSI=true;//En caso de si, pone a true la variable
	        		confirmarjuego();

	        		 cerrarConexiones();
	        	}else {
	        		dichoSI=false;
	        		this.t.dispose();//En caso de No cierra la pantalla y no hace nada mas
	        		 cerrarConexiones();
	        	}
				
			} else {
				this.t.empate();
				if(this.t.PopUpSiNo()) {//Se ejecuta una ventana emergente para decir si quiere continuar jugando
	        		dichoSI=true;//En caso de si, pone a true la variable
	        		confirmarjuego();

	        		 cerrarConexiones();
	        	}else {
	        		dichoSI=false;
	        		this.t.dispose();//En caso de No cierra la pantalla y no hace nada mas
	        		 cerrarConexiones();
	        	}
			}

        try {
            Thread.sleep(1000); // Espera 1 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (dichoSI) {//Si anteriormente han dicho Si, quita la pantalla y pone una nueva

        	this.t.dispose();
        	Inicio i = null;
        	i.nuevoJuego();
		}
       // this.t.dispose(); // Cierra la ventana

       
    }
    public boolean confirmarjuego() {
    	if (dichoSI) {
    		return true;
		}else {
			return false;
		}
		
    	
    }
    public void reiniciarSi() {
    	dichoSI=false;
    }
    private void cerrarConexiones() {//cierra las coneciones del servidor
        try {
            if (this.entrada != null) this.entrada.close();
            if (this.salida != null) this.salida.close();
            if (this.estelado != null) this.estelado.close();
        } catch (IOException e) {
            System.out.println("Error cerrando conexiones: " + e.getMessage());
        }
    }
}
