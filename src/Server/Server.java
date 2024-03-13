package Server;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    private static int MAX_CONNECTIONS = 4; // Máximo de conexiones permitidas
    private static final long WAIT_TIME_FOR_SECOND_PLAYER = 10000; // Tiempo de espera para el segundo jugador en milisegundos
    boolean nosalir=true;
    public static void main(String[] args) {
        new Server().startServer(); // Crea una instancia de Server y comienza a escuchar conexiones.
    }

    public void startServer() {
        List<Socket> clients = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(3030)) {
            System.out.println("Servidor esperando conexiones...");

            Timer timer = new Timer();
            while (nosalir) {
            	if(clients.size() < MAX_CONNECTIONS) {
                Socket client = serverSocket.accept();
                clients.add(client);

                if (clients.size() % 2 != 0) {
                    System.out.println("Jugador 1 conectado desde: " + client.getInetAddress().getHostAddress());

                    TimerTask task = new TimerTask() {
                        
                        public void run() {
                            if (clients.size() % 2 != 0) { // Si sigue siendo impar, no se ha conectado el segundo jugador
                                System.out.println("Iniciando juego contra la IA.");
                                Socket iaPlayer = new Socket(); // Socket falso para representar a la IA
                                clients.add(iaPlayer);
                                // Nota el cambio: Server.this se usa para referirse a la instancia de Server desde la clase anónima
                                GameSession gameSession = new GameSession(client, iaPlayer, true, Server.this); 
                                gameSession.start();
                            }
                        }
                    };
                    timer.schedule(task, WAIT_TIME_FOR_SECOND_PLAYER);
                } else {
                    System.out.println("Jugador 2 conectado desde: " + client.getInetAddress().getHostAddress());
                    timer.cancel(); // Cancela el temporizador si el segundo jugador se conecta
                    timer = new Timer(); // Crea un nuevo Timer para futuras esperas
                    Socket player1 = clients.get(clients.size() - 2);
                    Socket player2 = client;
                    System.out.println("Iniciando una nueva sesión de juego.");
                    // Aquí también usamos Server.this para referirse a la instancia actual de Server
                    GameSession gameSession = new GameSession(player1, player2, false, Server.this); 
                    gameSession.start();
                }
            	}else {
            		 System.out.println("Se ha alcanzado el máximo número de conexiones.");
            	}
            }
           
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void sumar(int cantidad) {
    	MAX_CONNECTIONS=MAX_CONNECTIONS+cantidad;
    }
}
