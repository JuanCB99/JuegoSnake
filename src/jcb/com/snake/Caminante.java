package jcb.com.snake;

/**
 *
 * @author Juan
 */
public class Caminante implements Runnable {

    private MostrarSnake snake;
    private boolean estado = true;
    private int tiempoRefresco = 200;

    public Caminante(MostrarSnake snake) {
        this.snake = snake;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean isEstado() {
        return estado;
    }

    public int getTiempoRefresco() {
        return tiempoRefresco;
    }

    public void setTiempoRefresco(int tiempoRefresco) {
        this.tiempoRefresco = tiempoRefresco;
    }
    

    @Override
    public void run() {

        //el metodo refresca el juego a cantidad de veces que diga  la variable tiempo de refresco
        while (this.estado) {

            snake.avanzar();
            snake.repaint();

            try {

                Thread.sleep(this.tiempoRefresco);

            } catch (Exception e) {

                e.printStackTrace(System.out);

            }

        }

    }
    
//    public void parar(){
//        
//        this.estado = false;
//        
//    }
//    
//    public void reiniciarCamino(){
//        
//        this.estado = true;
//        
//    }

}
