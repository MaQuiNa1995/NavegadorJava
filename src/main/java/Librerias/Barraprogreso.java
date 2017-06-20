/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Librerias;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.plaf.FontUIResource;

@SuppressWarnings("serial")
public final class Barraprogreso extends JFrame {

    Font algerian = new FontUIResource("Algerian", Font.BOLD, 15);

    JProgressBar bar;
    JLabel porcentaje;
    JButton botonAceptar;
    int value,
            i,
            completadoCampeones = i * 100 / 133,
            completadoPasivas = i * 100 / 133,
            completadoHabilidades = i * 100 / 24;

    public Barraprogreso(String titulo,String contenido,int completado) {
        setTitle(titulo);
        crearVentana();
        operaciones(completado);
        Output_Box.Cuadro_Aceptar(titulo, contenido);
    }

    public void crearVentana() {
        setLocation(500, 500);
        setSize(200, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        porcentaje = new JLabel();
        porcentaje.setFont(algerian);

        botonAceptar = new JButton("Aceptar");

        bar = new JProgressBar();
        bar.setMaximum(100);

        add(bar, BorderLayout.NORTH);
        add(porcentaje, BorderLayout.CENTER);
        add(botonAceptar, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void operaciones(int completado) {
        while (true) {
            porcentaje.setText(value + " % Completado");
            value = bar.getValue() + 1;
            bar.setValue(value);
            if (value > completado) {
                break;
            }
        }
    }

}
