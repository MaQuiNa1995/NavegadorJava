package VentanasSecundarias;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import static javafx.concurrent.Worker.State.FAILED;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import service.MyCookieStore;

public class Navigator extends JFrame {

    private static final Logger LOG = Logger.getLogger(Navigator.class.getName());
    private static final long serialVersionUID = -1951385676682823399L;

    private WebView view;
    private JFXPanel javaFxPanel;
    private WebEngine engine;

    private MyCookieStore cookieStore;

    private JLabel estadoLabel;
    private JTextField direccion;
    private JProgressBar barraProgreso;
    private static final String FICHERO_NOMBRE = "hashmap.ser";

    public void iniciarComponentes() {
        Platform.setImplicitExit(false);

        cookieStore = new MyCookieStore();

        try {
            URL url = new URL("http://10.60.2.246:8888/serversso/login");

            File e = new File(FICHERO_NOMBRE);
            if (!e.exists()) {
                URLConnection conn = url.openConnection();
                conn.connect();
                cookieStore.storeCookies(conn);
//                System.out.println(cookieStore);
            }
            cookieStore.setCookies(url.openConnection());
        } catch (IOException ioe) {
            LOG.info("Liada: ".concat(ioe.getMessage()));
        }

        javaFxPanel = new JFXPanel();
        estadoLabel = new JLabel();

        JPanel panelTodo = new JPanel(new BorderLayout());
        JButton botonBuscar = new JButton("Buscar");
        direccion = new JTextField();
        barraProgreso = new JProgressBar();

        crearEscena();

        ActionListener direcctionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadURL(direccion.getText());
            }
        };

        botonBuscar.addActionListener(direcctionListener);
        direccion.addActionListener(direcctionListener);

        barraProgreso.setPreferredSize(new Dimension(150, 18));
        barraProgreso.setStringPainted(true);

        JPanel topBar = new JPanel(new BorderLayout(5, 0));
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        topBar.add(direccion, BorderLayout.CENTER);
        topBar.add(botonBuscar, BorderLayout.EAST);

        JPanel estadoPanel = new JPanel(new BorderLayout(5, 0));
        estadoPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        estadoPanel.add(estadoLabel, BorderLayout.CENTER);
        estadoPanel.add(barraProgreso, BorderLayout.EAST);

        panelTodo.add(topBar, BorderLayout.NORTH);
        panelTodo.add(javaFxPanel, BorderLayout.CENTER);
        panelTodo.add(estadoPanel, BorderLayout.SOUTH);

        getContentPane().add(panelTodo);

        setPreferredSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
    }

    private void crearEscena() {

        Platform.runLater(() -> {
            view = new WebView();
            engine = view.getEngine();

            engine.titleProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Navigator.this.setTitle(newValue);
                        }
                    });
                }
            });

            engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
                @Override
                public void handle(final WebEvent<String> event) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("----------------------------------------");
                                URL url = new URL(direccion.getText());
                                URLConnection conn = url.openConnection();
                                cookieStore.storeCookies(conn);
                                System.out.println(cookieStore);
                                cookieStore.setCookies(url.openConnection());
                            } catch (IOException ioe) {
                                LOG.info("Liada: ".concat(ioe.getMessage()));
                            }

//                            Collection<List<String>> cookiesHeader = connection.getHeaderFields().values();
//                            System.out.println("----------- Lista De Cookies Header ----------------");
//
//                            for (List<String> cooky : cookiesHeader) {
//                                System.out.println(cooky);
//                            }
//                            System.out.println("------------------------------------------------------");
//                            Collection<List<String>> cookiesRequest = connection.getRequestProperties().values();
//                            System.out.println("----------- Lista De Cookies Request -----------------");
//
//                            for (List<String> cooky : cookiesRequest) {
//                                for (String cadenaSacada : cooky) {
//                                    System.out.println(cadenaSacada);
//                                }
//                            }
//                            System.out.println("-----------------------------------------------------");
                            estadoLabel.setText(event.getData());
                        }
                    });
                }
            });

            engine.locationProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, String viejoValor, final String nuevoValor) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            direccion.setText(nuevoValor);
                        }
                    });
                }
            });

            engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            barraProgreso.setValue(newValue.intValue());
                        }
                    });
                }
            });

            engine.getLoadWorker().exceptionProperty()
                    .addListener(new ChangeListener<Throwable>() {

                        @Override
                        public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
                            if (engine.getLoadWorker().getState() == FAILED) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        LOG.info("Algo Sali� Mal");
                                    }
                                });
                            }
                        }
                    });

            javaFxPanel.setScene(new Scene(view));
        });
    }

    public void loadURL(String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String urlTemporal = toURL(url);

                if (urlTemporal == null) {
                    urlTemporal = toURL("http://" + url);
                }

                engine.load(urlTemporal);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException mue) {
            LOG.info("URL Mal Formada: ".concat(mue.getMessage()));
            return null;
        }
    }

}
