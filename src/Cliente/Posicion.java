package Cliente;

public class Posicion {
	   int fila; // donde se guarda la fila del ultimo movimiento
	   int columna;// lo mismo pero coluimna
	   char letra; //simbolo k se pone X o O
	   private boolean notificado = false;
	   public synchronized boolean espera() {
		    try {
		        this.wait(10000); // Espera hasta ser notificado o hasta que pasen 10 segundos
		        return notificado; // Retorna el estado de 'notificado'
		    } catch (InterruptedException e) {
		        return false; // Retorna false si el hilo fue interrumpido
		    } finally {
		        notificado = false; // Restablece el estado para el próximo uso
		    }
		}

	   public synchronized void despierto() { //esto para despertar el hilo y siga el programa
		   this.notificado = true;
	      this.notify();
	   }

	   public synchronized void cargaPosicion(int pfila, int pcolumna) { // Carga la posición del último movimiento
	      this.fila = pfila;
	      this.columna = pcolumna;
	   }

	   public synchronized void cargaLetra(char pletra) { // Carga el símbolo del jugador
	      this.letra = pletra;
	   }

	   public synchronized int fila() {// Devuelve la fila del último movimiento
	      return this.fila;
	   }

	   public synchronized int columna() {//o mismo pero columna
	      return this.columna;
	   }

	   public synchronized char letra() {//lo mismo pero con simbolo
	      return this.letra;
	   }

	   public synchronized char otraletra() {//devuelve lo opuesto del k te dal la funcion de arriab 
	      return (char)(this.letra == 'X' ? 'O' : 'X');
	   }
	}