package Cliente;

public class Inicio{
	 Posicion p;
	 Tablero t;
	 Logica juego;
	public static void main(String[] args) {
	nuevoJuego();
	 
	   
	  
	}
	 public static void nuevoJuego() {
		  Posicion p = new Posicion(); //crea posicion
		   Tablero t = new Tablero(p); //crea Tablero
		   Logica juego = new Logica(t, p); // crea una logica metiendo la poscion y tablero
		   juego.Conecto(); //invoca la funcion de logica de conectar con el servidor
		   int turno = juego.turno();//obtiene el turno
		   p.cargaLetra((char)(turno == 1 ? 'X' : 'O')); // establecee el símbolo k le toca a cada uno.
		   //System.out.println("eeeeeeeee"+turno);
		   juego.inicio(turno);
	   }
	 
}