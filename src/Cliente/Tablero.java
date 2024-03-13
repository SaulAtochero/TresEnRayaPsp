package Cliente;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Tablero extends JFrame implements ActionListener {
	   final int n = 3; //para tablero 3 x 3
	   JButton[][] boton;
	   Font f;
	   boolean activo; //vaiable k indica si el jugador puede tocar o no el tablero
	   Posicion p; //referecia para las posciones
	   Posicion p2;
	   boolean empate =false;
	   public Tablero(Posicion pp) {// Constructor para crear tablero
		
	      super("tres en raya");
	      this.p2=pp;
	      this.p = pp;
	      this.setSize(500, 500);
	      this.setResizable(false);
	      this.activo = false;
	      this.f = new Font("Monospaced", 0, 100);
	      this.boton = new JButton[3][3];
	      this.setLayout(new GridLayout(3, 3));

	      for(int i = 0; i < 3; ++i) { // esto para crear los botoness
	         for(int j = 0; j < 3; ++j) {
	            this.boton[i][j] = new JButton();
	            this.boton[i][j].setActionCommand(i + "-" + j);
	            this.boton[i][j].addActionListener(this);
	            this.boton[i][j].setFont(this.f);
	            this.add(this.boton[i][j]);
	         }
	      }

	      this.repaint();// refresca la interfaz gráfica
	      this.setVisible(true);// hace visible la ventana
	   }

	   public void Poner(int i, int j, char letra) {// esto es para colocar X o O en una poscion i= fila j = columna
	      this.boton[i][j].setText(String.valueOf(letra)); //pinta la X o O
	      this.boton[i][j].setEnabled(false);// desactiva ese boton
	      this.repaint(); //refresca la interfaz
	   }

	   public void Poner(JButton j, char letra) {// lo mismo pero si le pasar un boton en vez de coordenadas
	      j.setText(String.valueOf(letra));
	      j.setEnabled(false);
	      j.repaint();
	   }

	   public void actionPerformed(ActionEvent e) { //cuando pulso un boton
	      if (this.activo) {// si esta activado 
	         String[] aux = e.getActionCommand().split("-"); // obtiene las coordenadas del boton
	         int fila = Integer.parseInt(aux[0]);//obtine el primer caracter del string y lo define como fila
	         int columna = Integer.parseInt(aux[1]);// lo mismo pero columna
	         this.Poner((JButton)e.getSource(), this.p.letra()); // coloca la letra X o O del jugador  en boton
	         this.p.cargaPosicion(fila, columna);  // la pone la posicion
	         System.out.println("fila: "+fila+"Columna: "+columna);
	         this.activo = false; //desactivo tablero 
	         this.p.despierto();//notifia k ya a pulsado al hilo para k despierte y siga el juegooo
	      }

	   }

	   public void Activo() {// activa el tablero
	      this.setTitle("Es tu turno");
	      this.activo = true;
	   }

	   public void Desactivo() {// desactiva el tablero
	      this.setTitle("Espera a que el otro juegue");
	      this.activo = false;
	   }

	   public void gano() { //imprime 3 en rayaaaa
	      this.setTitle(" HAY TRES EN RAYA ");
	   }
	   
	   public boolean empate() { //imprime empate
		      this.setTitle(" EMPATE ");
		      empate=true;
		      return empate;
		   }

	   public boolean hueco() {// comprueva si hay hueco en el tabler
	      for(int i = 0; i < 3; ++i) {
	         for(int j = 0; j < 3; ++j) {
	            if (this.boton[i][j].getText().equals("")) {
	               return true;//si hay algo vacio tryue
	            }
	         }
	      }

	      return false;//retorna false si esta lleno
	   }

	   public boolean linea(int x0, int y0, int x1, int y1, int x2, int y2) {// yo no enteder jajajajaj como k compara k no esten vacios los botones en 1 con el 2 y el 2 con el 3 no se pa k quiere esto el julio chalao
	      return !this.boton[x0][y0].getText().equals("") && this.boton[x0][y0].getText().equals(this.boton[x1][y1].getText()) && this.boton[x1][y1].getText().equals(this.boton[x2][y2].getText());
	   }

	   public boolean enraya() {// convinaciones ganadoras para el 3 en raya
	      return this.linea(0, 0, 0, 1, 0, 2) || this.linea(1, 0, 1, 1, 1, 2) || this.linea(2, 0, 2, 1, 2, 2) || this.linea(0, 0, 1, 0, 2, 0) || this.linea(0, 1, 1, 1, 2, 1) || this.linea(0, 2, 1, 2, 2, 2) || this.linea(0, 0, 1, 1, 2, 2) || this.linea(0, 2, 1, 1, 2, 0);
	   }

	   public boolean PopUpSiNo() {
		   JFrame popupYN = new JFrame();
		   int resultado=-1;
		   if (empate) {
			    resultado =  JOptionPane.showConfirmDialog(popupYN, "¿Deseas volver a jugar?","EMPATE",JOptionPane.YES_NO_OPTION);
		}else {
			 resultado =  JOptionPane.showConfirmDialog(popupYN, "¿Deseas volver a jugar?","HAY TRES EN RAYA",JOptionPane.YES_NO_OPTION);
		}
		   
		   boolean r=false;
		
		switch (resultado) {
		case JOptionPane.YES_OPTION:
			System.out.println("Me meto en la opcion de si");
			r=true;
			break;
		case JOptionPane.NO_OPTION:
			System.out.println("Me meto en la opcion de No");
			r=false;
			break;


		default:
			r=false;
			break;
		}
	    return r;
	   }

	}