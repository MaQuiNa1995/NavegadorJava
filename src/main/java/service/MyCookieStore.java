package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CookieManager is a simple utilty for handling cookies when working with
 * java.net.URL and java.net.URLConnection objects.
 *
 *
 * Cookiemanager cm = new CookieManager(); URL url = new
 * URL("http://www.hccp.org/test/cookieTest.jsp");
 *
 * . . .
 *
 *     // getting cookies: URLConnection conn = url.openConnection();
 * conn.connect();
 *
 *     // setting cookies cm.storeCookies(conn);
 * cm.setCookies(url.openConnection());
 *
 * @author Ian Brown
 *
 *
 */
public class MyCookieStore {

    private static final Logger LOG = Logger.getLogger(MyCookieStore.class.getName());

    private Map store;

    private static final String FICHERO_NOMBRE = "hashmap.ser";

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_VALUE_DELIMITER = ";";
    private static final String PATH = "path";
    private static final String EXPIRES = "expires";
    private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
    private static final String SET_COOKIE_SEPARATOR = "; ";
    private static final String COOKIE = "Cookie";

    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final char DOT = '.';

    public MyCookieStore() {

        store = new HashMap<>();
        
    }

    /**
     * Retrieves and stores cookies returned by the host on the other side of
     * the the open java.net.URLConnection.
     *
     * The connection MUST have been opened using the connect() method or a
     * IOException will be thrown.
     *
     * @param conn a java.net.URLConnection - must be open, or IOException will
     * be thrown
     * @throws java.io.IOException Thrown if conn is not open.
     */
    public void storeCookies(URLConnection conn) throws IOException {

        // let's determine the domain from where these cookies are being sent
        String domain = getDomainFromHost(conn.getURL().getHost());

        Map<String,HashMap<String,String>> domainStore; // this is where we will store cookies for this domain

        // now let's check the store to see if we have an entry for this domain
        if (store.containsKey(domain)) {
            // we do, so lets retrieve it from the store
            domainStore = (Map<String,HashMap<String,String>>) store.get(domain);
        } else {
            // we don't, so let's create it and put it in the store
            domainStore = leerCookies();
            store.put(domain, domainStore);
        }

        // OK, now we are ready to get the cookies out of the URLConnection
        String headerName = null;
        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
            if (headerName.equalsIgnoreCase(SET_COOKIE)) {
                HashMap<String,String> cookie = new HashMap<>();
                StringTokenizer st = new StringTokenizer(conn.getHeaderField(i), COOKIE_VALUE_DELIMITER);

                // the specification dictates that the first name/value pair
                // in the string is the cookie name and value, so let's handle
                // them as a special case: 
                if (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
                    String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length());
                    domainStore.put(name, cookie);
                    cookie.put(name, value);
                }

                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    try {
                        cookie.put(
                                token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR)).toLowerCase(),
                                token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length())
                        );
                    } catch (StringIndexOutOfBoundsException excep) {

                    }
                }
            }
        }
        guardarCookies(domainStore);
    }

    /**
     * Prior to opening a URLConnection, calling this method will set all
     * unexpired cookies that match the path or subpaths for thi underlying URL
     *
     * The connection MUST NOT have been opened method or an IOException will be
     * thrown.
     *
     * @param conn a java.net.URLConnection - must NOT be open, or IOException
     * will be thrown
     * @throws java.io.IOException Thrown if conn has already been opened.
     */
    public void setCookies(URLConnection conn) throws IOException {

        // let's determine the domain and path to retrieve the appropriate cookies
        URL url = conn.getURL();

        String domain = getDomainFromHost(url.getHost());
        String path = url.getPath();

        Map<String,HashMap<String,String>> domainStore;

        if (existeFichero()) {
            domainStore = (Map<String,HashMap<String,String>>) leerCookies().get(domain);
        } else {
            domainStore = (Map<String,HashMap<String,String>>) store.get(domain);
        }

        if (domainStore == null) {
            return;
        }
        StringBuilder cookieStringBuffer = new StringBuilder();

        Iterator<String> cookieNames = domainStore.keySet().iterator();
        while (cookieNames.hasNext()) {
            String cookieName = cookieNames.next();
            Map<String,String> cookie;
            cookie = (Map<String,String>) domainStore.get(cookieName);
            // check cookie to ensure path matches  and cookie is not expired
            // if all is cool, add cookie to header string 
            if (comparePaths(cookie.get(PATH), path) && isNotExpired(cookie.get(EXPIRES))) {
                cookieStringBuffer.append(cookieName);
                cookieStringBuffer.append("=");
                cookieStringBuffer.append(cookie.get(cookieName));
                if (cookieNames.hasNext()) {
                    cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
                }
            }
        }
        try {
            conn.setRequestProperty(COOKIE, cookieStringBuffer.toString());
        } catch (java.lang.IllegalStateException ise) {
            IOException ioe = new IOException("Illegal State! Cookies cannot be set on a URLConnection that is already connected. "
                    + "Only call setCookies(java.net.URLConnection) AFTER calling java.net.URLConnection.connect().");
            throw ioe;
        }
    }

    private String getDomainFromHost(String host) {
        if (host.indexOf(DOT) != host.lastIndexOf(DOT)) {
            return host.substring(host.indexOf(DOT) + 1);
        } else {
            return host;
        }
    }

    private boolean isNotExpired(String cookieExpires) {
        if (cookieExpires == null) {
            return true;
        }
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return (now.compareTo(dateFormat.parse(cookieExpires))) <= 0;
        } catch (java.text.ParseException pe) {
            return false;
        }
    }

    private boolean comparePaths(String cookiePath, String targetPath) {
        if (cookiePath == null) {
            return true;
        } else if (cookiePath.equals("/")) {
            return true;
        } else if (targetPath.regionMatches(0, cookiePath, 0, cookiePath.length())) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Returns a string representation of stored cookies organized by domain.
     *
     * @return
     */
    @Override
    public String toString() {
        return store.toString();
    }

    public static void main(String[] args) {
        MyCookieStore cm = new MyCookieStore();
        try {
            URL url = new URL("http://10.60.2.246:8888/serversso/login");
            URLConnection conn = url.openConnection();
            conn.connect();
            cm.storeCookies(conn);
            System.out.println(cm);
            cm.setCookies(url.openConnection());
        } catch (IOException ioe) {
            LOG.info("Hubo un error: ".concat(ioe.getMessage()));
        }
    }

    private void guardarCookies(Map<String,HashMap<String,String>> mapaGuardar) {

        if (!existeFichero()) {
            try (FileOutputStream fos = new FileOutputStream(FICHERO_NOMBRE, false);
                    ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                oos.writeObject(mapaGuardar);
                LOG.info("Objeto Serializado Con Éxito");

            } catch (FileNotFoundException ex) {
                LOG.log(Level.INFO, "Archivo No Encontrado, Raz\u00f3n {0}", ex.getMessage());
            } catch (IOException ex) {
                LOG.log(Level.INFO, "No se pudo escribir en el fichero, Raz\u00f3n {0}", ex.getMessage());
            }
        } else {
            LOG.info("Ya existen unas cookies creadas");
        }

    }

    private Map leerCookies() {

        Map mapaCookies = null;

        try (FileInputStream fos = new FileInputStream(FICHERO_NOMBRE);
                ObjectInputStream ois = new ObjectInputStream(fos)) {

            mapaCookies = (Map) ois.readObject();
            LOG.info("Objeto Deserializado Con Éxito");
        } catch (FileNotFoundException ex) {
            LOG.info("Archivo No Encontrado, Razón ".concat(ex.getMessage()));
        } catch (IOException ex) {
            LOG.info("No se puede escribir en el fichero, Razón ".concat(ex.getMessage()));
        } catch (ClassNotFoundException ex) {
            LOG.info("Clase No Encontrada, Mas info: ".concat(ex.getMessage()));
        }
        return mapaCookies;
    }

    private boolean existeFichero() {
        File ficheroVerificar = new File(FICHERO_NOMBRE);

        return ficheroVerificar.exists();
    }
}
