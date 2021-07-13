package jcb.com.snake;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import static jcb.com.snake.VistaSnake.*;

/**
 *
 * @author Juan
 */
public class MostrarSnake extends JPanel {

    private ImageIcon imagenSerpiente;
    private ImageIcon imagenComida;

    private int dimensionPanel, dimensionCuadricula, cantidad, residuoBorde;
    private List<int[]> snake = new ArrayList<>();
    private int[] comida = new int[2];
    private int puntoTotales = 0;
    private int aumentoVelocidad;

    private String direccion = "R";
    private String direccionProxima = "R";

    private String direccionImagenCabeza = "/jcb.com.recursos/cabeza-derecha.png";
    private String direccionImagenCentro = "/jcb.com.recursos/centro-izquierda-derecha.png";
    private String direccionImagenCola = "/jcb.com.recursos/cola-derecha.png";

    private Thread hilo;
    private Caminante camino;

    //El constructor define los parametros iniciales como lo son la posicion inicial de la serpiente y la primera posicion de la comida;
    public MostrarSnake(int dimensionPanel, int cantidad) {
        this.dimensionPanel = dimensionPanel;
        this.cantidad = cantidad;
        this.dimensionCuadricula = dimensionPanel / cantidad;
        this.residuoBorde = dimensionPanel % cantidad;
        int[] a = {cantidad / 2 - 1, cantidad / 2 - 1};
        int[] b = {cantidad / 2, cantidad / 2 - 1};
        snake.add(a);
        snake.add(b);
        generarComida();

        camino = new Caminante(this);
        hilo = new Thread(camino);
        hilo.start();

    }

    public int getPuntoTotales() {
        return puntoTotales;
    }

    public void setPuntoTotales(int puntoTotales) {
        this.puntoTotales = puntoTotales;
    }

    @Override
    public void paint(Graphics pintor) {
        super.paint(pintor);

        //Renderizar la serpiente con imagenes
        int contador = 0;
        //Este for recorre una y otra ves el array de snake
        int[] ultimo = snake.get(snake.size() - 1);
        for (int[] snk : snake) {

            //Si el contador es igual a 0(ultima posicion de el snake) entinces se pinta esa posicion con la imagen de la cola
            if (contador == 0) {

                imagenSerpiente = new ImageIcon(getClass().getResource(direccionImagenCola));
                pintor.drawImage(imagenSerpiente.getImage(), residuoBorde / 2 + snk[0] * dimensionCuadricula, residuoBorde / 2 + snk[1] * dimensionCuadricula,
                        dimensionCuadricula - 1, dimensionCuadricula - 1, null);

            }
            //si el contador es >= a el tamaño de snake-2 entinces se pinta todo lo que este entre ese intervalo 
            //con la imagen de centro
            if (contador >= 1 && contador <= (snake.size() - 2)) {

                imagenSerpiente = new ImageIcon(getClass().getResource(direccionImagenCentro));
                pintor.drawImage(imagenSerpiente.getImage(), residuoBorde / 2 + snk[0] * dimensionCuadricula, residuoBorde / 2 + snk[1] * dimensionCuadricula,
                        dimensionCuadricula - 1, dimensionCuadricula - 1, null);

            }
            //si el contador es = al tamaño de snake-1 entonces se pinta esa posicion con la imagen de la cabeza
            if (contador == (snake.size() - 1)) {

                imagenSerpiente = new ImageIcon(getClass().getResource(direccionImagenCabeza));
                pintor.drawImage(imagenSerpiente.getImage(), residuoBorde / 2 + snk[0] * dimensionCuadricula, residuoBorde / 2 + snk[1] * dimensionCuadricula,
                        dimensionCuadricula - 1, dimensionCuadricula - 1, null);

            }

            contador++;
        }

        //renderizando la comida
        imagenComida = new ImageIcon(getClass().getResource("/jcb.com.recursos/manzana.png"));
        pintor.drawImage(imagenComida.getImage(), residuoBorde / 2 + comida[0] * dimensionCuadricula,
                residuoBorde / 2 + comida[1] * dimensionCuadricula, dimensionCuadricula - 1, dimensionCuadricula - 1, null);

    }

    public void avanzar() {

        igualarDireccion();
        //D = Derecha, L = Izquierda, U = Arriba, D = Abajo (son las iniciales pero en ingles)
        int[] ultimo = snake.get(snake.size() - 1);
        int agregarEjeX = 0;
        int agregarEjeY = 0;

        //Este swich suma o resta una unidad a las coordenadas de la serpiente dependiendo de la direccion elejida
        //ademas se asigna una ruta a las variables de direccion...´para llamar la imagen que corresponde
        switch (direccion) {
            case "R":
                agregarEjeX = 1;
                direccionImagenCabeza = "/jcb.com.recursos/cabeza-derecha.png";
                direccionImagenCentro = "/jcb.com.recursos/centro-izquierda-derecha.png";
                direccionImagenCola = "/jcb.com.recursos/cola-derecha.png";

                break;
            case "L":
                agregarEjeX = -1;
                direccionImagenCabeza = "/jcb.com.recursos/cabeza-izquierda.png";
                direccionImagenCentro = "/jcb.com.recursos/centro-izquierda-derecha.png";
                direccionImagenCola = "/jcb.com.recursos/cola-izquierda.png";

                break;
            case "U":
                agregarEjeY = -1;
                direccionImagenCabeza = "/jcb.com.recursos/cabeza-arriba.png";
                direccionImagenCentro = "/jcb.com.recursos/centro-arriba-abajo.png";
                direccionImagenCola = "/jcb.com.recursos/cola-arriba.png";

                break;
            case "D":
                agregarEjeY = 1;
                direccionImagenCabeza = "/jcb.com.recursos/cabeza-abajo.png";
                direccionImagenCentro = "/jcb.com.recursos/centro-arriba-abajo.png";
                direccionImagenCola = "/jcb.com.recursos/cola-abajo.png";

                break;
        }

        //Esto evita que la serpiente se salga de la cuadricula, hace que aparezca al lado contrario de donde sale
        int[] nuevo = {Math.floorMod(ultimo[0] + agregarEjeX, cantidad),
            Math.floorMod(ultimo[1] + agregarEjeY, cantidad)};

        //se revisan las cooredenadas de la serpiente para saber si al moverse colisiona con sigo misma
        //de ser asi se termina el juego y se da la opcion de reiniciar todo
        boolean existe = false;
        for (int i = 0; i < snake.size(); i++) {

            if (nuevo[0] == snake.get(i)[0] && nuevo[1] == snake.get(i)[1]) {
                existe = true;
                break;
            }

        }
        if (existe) {

            JOptionPane.showMessageDialog(this, "Perdiste, tu puntaje fue de: " + puntoTotales + ", oprime ACEPTAR para reiniciar");
            camino.setEstado(false);
            reiniciarJuego();

        } else {

            //Si no colisiona con sigo misma la serpiente se come la manzana y crece una posicion
            if (nuevo[0] == comida[0] && nuevo[1] == comida[1]) {

                snake.add(nuevo);
                generarComida();
                puntoTotales++;
                System.out.println(puntoTotales);
                contadorPuntos = puntoTotales;
                aumentarVelocidad();

            } else {

                snake.add(nuevo);
                snake.remove(0);

            }
        }

    }

    //se genera la comida en un punto aleatorio, pero se revisa que ese punto no este dentro de la serpiente
    //ni se salga de los limites del panel
    public void generarComida() {
        boolean existe = false;
        int a = (int) (Math.random() * cantidad);
        int b = (int) (Math.random() * cantidad);

        for (int[] snk : snake) {
            if (snk[0] == a && snk[1] == b) {
                existe = true;
                generarComida();
                break;
            }
        }
        if (existe == false) {
            this.comida[0] = a;
            this.comida[1] = b;
        }

    }

    public void cambiarDireccion(String dir) {
        //Este metodo evita que la serpiente se mueva hacia la misma direccion en la que va y en contra de la direccion actual
        //por ejemplo si va hacia adelante, no se cabiara la direccion si se pulsa otra ves hacia adelante, o no se podra ir
        //en contra de esa direccion para evitar que la srpiente colisione con sigo misma
        if ((direccion.equals("R") || direccion.equals("L")) && (dir.equals("U") || dir.equals("D"))) {

            this.direccionProxima = dir;

        }
        if ((direccion.equals("U") || direccion.equals("D")) && (dir.equals("L") || dir.equals("R"))) {

            this.direccionProxima = dir;

        }

    }

    public void igualarDireccion() {

        this.direccion = this.direccionProxima;

    }

    //setea todas las variables a sus valores iniciales, para reiniciar el juego
    //solo se activa si la serpiente colisiona
    public void reiniciarJuego() {
        snake.removeAll(snake);
        camino.setEstado(true);

        this.puntoTotales = 0;
        contadorPuntos = puntoTotales;

        this.direccion = "R";
        this.direccionProxima = "R";

        this.aumentoVelocidad = 200;
        camino.setTiempoRefresco(this.aumentoVelocidad);

        int[] a = {cantidad / 2 - 1, cantidad / 2 - 1};
        int[] b = {cantidad / 2, cantidad / 2 - 1};
        snake.add(a);
        snake.add(b);
        generarComida();
    }

    //mecanica para aumentar la velocidad del juego, cada ves que se llegue a determinada cantidad de puntos
    //se le resta una cantidad al tiempo de refresco y se manda a la clase  Caminante
    public void aumentarVelocidad() {

        switch (puntoTotales) {
            case 5:
                this.aumentoVelocidad = camino.getTiempoRefresco() - 10;
                camino.setTiempoRefresco(this.aumentoVelocidad);
                break;
            case 10:
                this.aumentoVelocidad = camino.getTiempoRefresco() - 15;
                camino.setTiempoRefresco(this.aumentoVelocidad);
                break;
            case 25:
                this.aumentoVelocidad = camino.getTiempoRefresco() - 25;
                camino.setTiempoRefresco(this.aumentoVelocidad);
                break;
            case 35:
                this.aumentoVelocidad = camino.getTiempoRefresco() - 35;
                camino.setTiempoRefresco(this.aumentoVelocidad);
                break;
            case 45:
                this.aumentoVelocidad = camino.getTiempoRefresco() - 45;
                camino.setTiempoRefresco(this.aumentoVelocidad);
                break;
            case 60:
                this.aumentoVelocidad = camino.getTiempoRefresco() - 20;
                camino.setTiempoRefresco(this.aumentoVelocidad);
                break;
            case 75:
                this.aumentoVelocidad = camino.getTiempoRefresco() - 25;
                camino.setTiempoRefresco(this.aumentoVelocidad);
                break;
        }

    }

}
