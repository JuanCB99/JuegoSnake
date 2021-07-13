package jcb.com.snake;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Juan
 */
public class VistaFondo extends JPanel {

    Color fondo = Color.LIGHT_GRAY;
    int dimensionPanel, dimensionCuadricula, cantidad, residuoBorde;

    public VistaFondo(int dimensionPanel, int cantidad) {
        this.dimensionPanel = dimensionPanel;
        this.cantidad = cantidad;
        this.dimensionCuadricula = dimensionPanel / cantidad;
        this.residuoBorde = dimensionPanel % cantidad;
    }

    @Override
    //Se renderiza el fondo con los parametros enviados desde el formulario vistaSnake
    public void paint(Graphics pintor) {
        super.paint(pintor);
        pintor.setColor(fondo);
        for (int i = 0; i < cantidad; i++) {
            for (int j = 0; j < cantidad; j++) {
                pintor.fill3DRect(residuoBorde / 2 + i * dimensionCuadricula,residuoBorde / 2 + j * dimensionCuadricula, 
                        dimensionCuadricula - 1, dimensionCuadricula - 1, true);
            }
        }
    }

}
