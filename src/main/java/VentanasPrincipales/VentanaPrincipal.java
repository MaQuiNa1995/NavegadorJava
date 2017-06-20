package VentanasPrincipales;

import VentanasSecundarias.Navigator;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class VentanaPrincipal extends JFrame implements ActionListener {

    private static final long serialVersionUID = -1885355866097185815L;

    public VentanaPrincipal() {
        definirVentana();
        definirMenuPrincipal();
    }

    void definirVentana() {
        setSize(200, 100);
        setLocation(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Navegador En Java");
    }

    public void definirMenuPrincipal() {

        String arrayNombreBotones = "Abrir Navegador";

        String arrayToolTipText = "Abre El Navegador";

        JPanel menuPrincipal;

        menuPrincipal = new JPanel();
        menuPrincipal.setLayout(new GridLayout(1, 1, 2, 2));

        JButton boton = new JButton(arrayNombreBotones);
        boton.addActionListener(this);
        boton.setToolTipText(arrayToolTipText);
        menuPrincipal.add(boton);

        add(menuPrincipal);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        switch (ae.getActionCommand()) {

            case "Abrir Navegador":
                Navigator browser = new Navigator();
                SwingUtilities.invokeLater(() -> {

                    browser.iniciarComponentes();

                    browser.setVisible(true);

                    browser.loadURL("http://10.60.2.246:8888/serversso/login");
                });

                break;

        }
    }
}
