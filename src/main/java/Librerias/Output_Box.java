// --------------------- Clase Output_Box Open Source ---------------------------------------
// Creado Por MaQuina reconoce el trabajo del autor ;)

package Librerias;

//Imports
import javax.swing.JOptionPane;

// Comienzo De La Clase
public class Output_Box {

    // Constructor 
    Output_Box() {
        
    }
    
    // Parametros:
    //      Titulo: String que define el titulo de la ventana
    //      Contenido: String que define el "Cuerpo" de la ventana
    
    // -------------------------------------Metodos------------------------------------------
  
    // Muestra un Cuadro De Dialogo Con Un Simple Boton Aceptar y Con Una Imagen: "Informacion"
    public static void Cuadro_Aceptar(String Titulo, String Contenido) {
        JOptionPane.showMessageDialog(null, Contenido, Titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    // Muestra un Cuadro De Dialogo Con Un Simple Boton Aceptar y Con Una Imagen: "Error"
    void Cuadro_Error(String Titulo, String Contenido) {
        JOptionPane.showMessageDialog(null, Contenido, Titulo, JOptionPane.ERROR_MESSAGE);
    }
    
    // Muestra un Cuadro De Dialogo Con Un Simple Boton Aceptar y Con Una Imagen: "Warning"
    void Cuadro_Warning(String Titulo, String Contenido) {
        JOptionPane.showMessageDialog(null, Contenido, Titulo, JOptionPane.WARNING_MESSAGE);
    }
    
    // Muestra un Cuadro De Dialogo Con Un Simple Boton Aceptar y Sin Imagenes
    public static void Cuadro_Vacio(String Titulo, String Contenido) {
        JOptionPane.showMessageDialog(null, Contenido, Titulo, JOptionPane.PLAIN_MESSAGE);
    }
    
    String Pedir_Strings(String Titulo, String Contenido) {
        String Strings = JOptionPane.showInputDialog(null,Contenido ,Titulo , 1);
        
        return Strings;
    }
    
}