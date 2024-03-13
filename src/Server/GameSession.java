package Server;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

class GameSession extends Thread {
    private Socket player1;
    private Socket player2;
    private boolean isAgainstAI;
    private int[][] tablero = new int[3][3]; //tablero para k siga el server la cuenta de las 3 en raya
    private Server s; 

    public GameSession(Socket player1, Socket player2, boolean isAgainstAI, Server s) {
        this.player1 = player1;
        this.player2 = player2;
        this.isAgainstAI = isAgainstAI;
        this.s = s; // Importante: esto almacena la referencia a Server
    }

    public void run() {
        try {
            DataOutputStream out1 = new DataOutputStream(player1.getOutputStream());
            DataInputStream in1 = new DataInputStream(player1.getInputStream());
            DataOutputStream out2 = isAgainstAI ? null : new DataOutputStream(player2.getOutputStream());
            DataInputStream in2 = isAgainstAI ? null : new DataInputStream(player2.getInputStream());
            
            int turnoJugador1 = 1;
            int turnoJugador2 = 2;

            out1.writeInt(turnoJugador1);
            if (!isAgainstAI) {
            	out2.writeInt(turnoJugador2);
            }
            // Asume que es el turno del jugador 1 inicialmente
            boolean esTurnoDelJugador1 = true;

            while (true) {
                if (esTurnoDelJugador1) {
                    // Espera por el movimiento del jugador 1
                    int indicador = in1.readInt();
                        System.out.println(indicador);
                        int fila = in1.readInt();
                        int columna = in1.readInt();
                        tablero[fila][columna] = 1;
                        if (!isAgainstAI) {
                            out2.writeInt(indicador); // Indica al jugador 2 que hay un movimiento
                            out2.writeInt(fila);
                            out2.writeInt(columna);
                        }
                    
                } else {
                    // Gestiona el movimiento del jugador 2 o de la IA
                    if (!isAgainstAI) {
                        int indicador = in2.readInt();
                        System.out.println(indicador);
                            int fila = in2.readInt();
                            int columna = in2.readInt();
                            tablero[fila][columna] = 2;
                            out1.writeInt(indicador); // Indica al jugador 1 que hay un movimiento
                            out1.writeInt(fila);
                            out1.writeInt(columna);
                    } else {
                        // Lógica para el movimiento de la IA
                        int[] movimiento = generarMovimientoAleatorioParaIA();
                        int fila = movimiento[0];
                        int columna = movimiento[1];
                        tablero[fila][columna] = 2;
                        out1.writeInt(1); // Indica al jugador 1 que hay un movimiento de la IA
                        out1.writeInt(fila);
                        out1.writeInt(columna);
                    }
                }
                esTurnoDelJugador1 = !esTurnoDelJugador1; // Cambia el turno
                
                // Verifica el fin del juego
                if (tableroLleno() || hayTresEnRaya()) {
                    cerrarConexiones();
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error en GameSession: " + e.getMessage());
        } finally {
            cerrarConexiones();
        }
    }

    private int[] generarMovimientoAleatorioParaIA() {
        Random random = new Random();
        int fila, columna;
        do {
            fila = random.nextInt(3); // Asumiendo un tablero de 3x3
            columna = random.nextInt(3);
        } while (!esCeldaVacia(fila, columna)); // Repite hasta encontrar una celda vacía
        // Realiza el movimiento en el tablero para la IA
        tablero[fila][columna] = 2; // Asume que '2' representa los movimientos de la IA
        return new int[]{fila, columna};
    }

    private boolean esCeldaVacia(int fila, int columna) {
        return tablero[fila][columna] == 0; // 0 indica celda vacía
    }
    
    private void cerrarConexiones() {
    	if(isAgainstAI) {
    		this.s.sumar(1);
    	}else {
    		this.s.sumar(2);
    	}
    	 
        try {
            player1.close();
            if (!isAgainstAI) {
                player2.close();
            }
            System.out.println("Conexiones con los clientes cerradas.");
        } catch (IOException e) {
            System.out.println("Error cerrando conexiones: " + e.getMessage());
        }
    }

    private boolean tableroLleno() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] == 0) {
                    return false; // Hay al menos una celda vacía, el tablero no está lleno.
                }
            }
        }
        return true; // No se encontró ninguna celda vacía, el tablero está lleno.
    }

    private boolean hayTresEnRaya() {
        // Verifica todas las posibles líneas de tres en el tablero.
        // Horizontal, vertical y diagonales.
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] != 0 && tablero[i][0] == tablero[i][1] && tablero[i][1] == tablero[i][2]) {
                return true; // Hay tres en raya horizontalmente.
            }
            if (tablero[0][i] != 0 && tablero[0][i] == tablero[1][i] && tablero[1][i] == tablero[2][i]) {
                return true; // Hay tres en raya verticalmente.
            }
        }
        if (tablero[0][0] != 0 && tablero[0][0] == tablero[1][1] && tablero[1][1] == tablero[2][2]) {
            return true; // Hay tres en raya en la diagonal principal.
        }
        if (tablero[0][2] != 0 && tablero[0][2] == tablero[1][1] && tablero[1][1] == tablero[2][0]) {
            return true; // Hay tres en raya en la diagonal inversa.
        }
        return false; // No se encontró ninguna línea de tres.
    }
}