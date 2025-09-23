package org.example;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashSet;
import javax.swing.*;

/**
 * Generadores Pseudoaleatorios (Swing + FlatLaf opcional).
 * Algoritmos:
 *  - Cuadrados medios (k fijo = 4)
 *  - Productos medios (k fijo = 4)
 *  - Multiplicador constante (k fijo = 4)
 *  - Lineal  (r = X/(m-1))
 *  - Congruencial multiplicativo (r = X/m)
 *  - Congruencial aditivo (lag-k) con semillas coma-separadas
 *
 * Incluye: decimales configurables, modo "ciclo completo" o por cantidad n, guardar TXT.
 */
public class Algoritmos extends JFrame {

    private final String[] OPCIONES = {
            "Algoritmo de cuadrados medios",
            "Algoritmo de productos medios",
            "Algoritmo de multiplicador constante",
            "Algoritmo lineal",
            "Algoritmo congruencial multiplicativo",
            "Algoritmo congruencial aditivo"
    };

    // Controles UI
    private JComboBox<String> comboAlgoritmo;
    private JTextField txtN, txtX0, txtX1, txtA, txtC, txtM, txtConstante, txtSemillas, txtDecimales;
    private JCheckBox chkCicloCompleto;
    private JTextArea areaSalida;
    private JButton btnGenerar, btnGuardar;

    public Algoritmos() {
        super("Algoritmos");

        // FlatLaf (si no está en el classpath, se ignora)
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacDarkLaf");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}

        // Icono (si existe en /recursos/logo.png)
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/recursos/logo.png"));
            setIconImage(icon);
        } catch (Exception ignored) {}

        // ----- Inicializar controles -----
        comboAlgoritmo = new JComboBox<>(OPCIONES);
        txtN = new JTextField();
        txtX0 = new JTextField();
        txtX1 = new JTextField();      // solo para productos medios
        txtA  = new JTextField();
        txtC  = new JTextField();
        txtM  = new JTextField();
        txtConstante = new JTextField();
        txtSemillas  = new JTextField(); // aditivo lag-k: "56, 95, 81, ..."
        txtDecimales = new JTextField(); // vacío -> 4 decimales por defecto
        chkCicloCompleto = new JCheckBox("Ciclo de vida");

        areaSalida = new JTextArea(16, 70);
        areaSalida.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaSalida);

        btnGenerar = new JButton("Generar");
        btnGuardar = new JButton("Guardar");

        // ----- Panel superior -----
        JPanel arriba = new JPanel(new GridLayout(0, 2, 5, 5));
        arriba.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        arriba.add(new JLabel("Algoritmo:")); arriba.add(comboAlgoritmo);

        arriba.add(new JLabel("Cantidad n:")); arriba.add(txtN);
        arriba.add(new JLabel("")); arriba.add(chkCicloCompleto);

        arriba.add(new JLabel("Semilla X0:")); arriba.add(txtX0);
        arriba.add(new JLabel("Semilla X1:")); arriba.add(txtX1);

        arriba.add(new JLabel("a:")); arriba.add(txtA);
        arriba.add(new JLabel("c:")); arriba.add(txtC);
        arriba.add(new JLabel("m:")); arriba.add(txtM);

        arriba.add(new JLabel("Constante (mult. constante):")); arriba.add(txtConstante);
        arriba.add(new JLabel("Semillas:")); arriba.add(txtSemillas);

        arriba.add(new JLabel("Decimales:")); arriba.add(txtDecimales);

        // ----- Panel inferior -----
        JPanel abajo = new JPanel();
        abajo.add(btnGuardar);
        abajo.add(btnGenerar);

        // ----- Frame -----
        setLayout(new BorderLayout());
        add(arriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 670);
        setLocationRelativeTo(null);

        // Eventos
        comboAlgoritmo.addActionListener(e -> actualizarCampos());
        chkCicloCompleto.addActionListener(e -> actualizarCampos());
        btnGenerar.addActionListener(this::generar);
        btnGuardar.addActionListener(e -> guardarComoTxt());

        actualizarCampos();
    }

    private void actualizarCampos() {
        String alg = (String) comboAlgoritmo.getSelectedItem();
        setAllEnabled(false);

        // siempre configurables
        txtDecimales.setEnabled(true);
        chkCicloCompleto.setEnabled(true);
        txtN.setEnabled(!chkCicloCompleto.isSelected()); // n no se usa en ciclo completo

        if (alg.equals("Algoritmo de cuadrados medios")) {
            txtX0.setEnabled(true);

        } else if (alg.equals("Algoritmo de productos medios")) {
            txtX0.setEnabled(true);
            txtX1.setEnabled(true);

        } else if (alg.equals("Algoritmo de multiplicador constante")) {
            txtX0.setEnabled(true);
            txtConstante.setEnabled(true);

        } else if (alg.equals("Algoritmo lineal")) {
            txtX0.setEnabled(true);
            txtA.setEnabled(true);
            txtC.setEnabled(true);
            txtM.setEnabled(true);

        } else if (alg.equals("Algoritmo congruencial multiplicativo")) {
            txtX0.setEnabled(true);
            txtA.setEnabled(true);
            txtM.setEnabled(true);

        } else if (alg.equals("Algoritmo congruencial aditivo")) {
            txtM.setEnabled(true);
            txtSemillas.setEnabled(true);
        }
    }

    private void setAllEnabled(boolean enabled) {
        txtN.setEnabled(enabled);
        txtX0.setEnabled(enabled);
        txtX1.setEnabled(enabled);
        txtA.setEnabled(enabled);
        txtC.setEnabled(enabled);
        txtM.setEnabled(enabled);
        txtConstante.setEnabled(enabled);
        txtSemillas.setEnabled(enabled);
        txtDecimales.setEnabled(enabled);
        chkCicloCompleto.setEnabled(enabled);
    }

    private void generar(ActionEvent ev) {
        areaSalida.setText("");
        try {
            final int K_FIJO = 4; // k siempre = 4 para los algoritmos de medios
            String alg = (String) comboAlgoritmo.getSelectedItem();
            boolean ciclo = chkCicloCompleto.isSelected();
            int dec = leerDecimales(txtDecimales);
            int n = ciclo ? 0 : leerEntero(txtN, "n");

            final int LIMITE = 50_000; // seguridad en ciclo completo
            int i = 0;

            if (alg.equals("Algoritmo de cuadrados medios")) {
                String x = leerTexto(txtX0, "X0");
                HashSet<String> vistos = new HashSet<>();
                while (true) {
                    BigInteger v = new BigInteger(x).multiply(new BigInteger(x));
                    String y = v.toString();
                    String xNext = extraerMedios(y, K_FIJO);
                    double r = toR_fromDigits(xNext, K_FIJO);
                    i++;
                    areaSalida.append(String.format(
                            "%d) x = %s^2 = %s; x= %s; r = %s%n",
                            i, x, y, xNext, fmt(r, dec)));

                    if (!ciclo) { if (i >= n) break; } else {
                        if (vistos.contains(xNext)) { areaSalida.append("— Fin: ciclo detectado.\n"); break; }
                        vistos.add(xNext);
                        if (i >= LIMITE) { areaSalida.append("— Límite alcanzado.\n"); break; }
                    }
                    x = xNext;
                }

            } else if (alg.equals("Algoritmo de productos medios")) {
                String prev = leerTexto(txtX0, "X0");
                String curr = leerTexto(txtX1, "X1");
                HashSet<String> vistos = new HashSet<>();
                while (true) {
                    BigInteger v = new BigInteger(prev).multiply(new BigInteger(curr));
                    String y = v.toString();
                    String next = extraerMedios(y, K_FIJO);
                    double r = toR_fromDigits(next, K_FIJO);
                    i++;
                    areaSalida.append(String.format(
                            "%d) x = %s*%s = %s; x= %s; r = %s%n",
                            i, prev, curr, y, next, fmt(r, dec)));

                    if (!ciclo) { if (i >= n) break; } else {
                        if (vistos.contains(next)) { areaSalida.append("— Fin: ciclo detectado.\n"); break; }
                        vistos.add(next);
                        if (i >= LIMITE) { areaSalida.append("— Límite alcanzado.\n"); break; }
                    }
                    prev = curr; curr = next;
                }

            } else if (alg.equals("Algoritmo de multiplicador constante")) {
                String x = leerTexto(txtX0, "X0");
                BigInteger aConst = new BigInteger(leerTexto(txtConstante, "Constante"));
                HashSet<String> vistos = new HashSet<>();
                while (true) {
                    BigInteger v = new BigInteger(x).multiply(aConst);
                    String y = v.toString();
                    String xNext = extraerMedios(y, K_FIJO);
                    double r = toR_fromDigits(xNext, K_FIJO);
                    i++;
                    areaSalida.append(String.format(
                            "%d) x = %s*%s = %s; x= %s; r = %s%n",
                            i, aConst.toString(), x, y, xNext, fmt(r, dec)));

                    if (!ciclo) { if (i >= n) break; } else {
                        if (vistos.contains(xNext)) { areaSalida.append("— Fin: ciclo detectado.\n"); break; }
                        vistos.add(xNext);
                        if (i >= LIMITE) { areaSalida.append("— Límite alcanzado.\n"); break; }
                    }
                    x = xNext;
                }

            } else if (alg.equals("Algoritmo lineal")) {
                long x = leerLong(txtX0, "X0");
                long a = leerLong(txtA, "a");
                long c = leerLong(txtC, "c");
                long m = leerLong(txtM, "m");
                if (m <= 1) throw new IllegalArgumentException("m debe ser > 1 para usar r = X/(m-1).");
                HashSet<Long> vistos = new HashSet<>();
                while (true) {
                    long xPrev = x;
                    x = Math.floorMod(a * x + c, m);
                    double r = x / (double) (m - 1);
                    i++;
                    areaSalida.append(String.format(
                            "%d) X%d = (%d*%d + %d) mod %d = %d; r = %s%n",
                            i, i, a, xPrev, c, m, x, fmt(r, dec)));

                    if (!ciclo) { if (i >= n) break; } else {
                        if (vistos.contains(x)) { areaSalida.append("— Fin: ciclo detectado.\n"); break; }
                        vistos.add(x);
                        if (i >= LIMITE) { areaSalida.append("— Límite alcanzado.\n"); break; }
                    }
                }

            } else if (alg.equals("Algoritmo congruencial multiplicativo")) {
                long x = leerLong(txtX0, "X0");
                long a = leerLong(txtA, "a");
                long m = leerLong(txtM, "m");
                HashSet<Long> vistos = new HashSet<>();
                while (true) {
                    long xPrev = x;
                    x = Math.floorMod(a * x, m);
                    double r = x / (double) m;
                    i++;
                    areaSalida.append(String.format(
                            "%d) X%d = (%d*%d) mod %d = %d; r = %s%n",
                            i, i, a, xPrev, m, x, fmt(r, dec)));

                    if (!ciclo) { if (i >= n) break; } else {
                        if (vistos.contains(x)) { areaSalida.append("— Fin: ciclo detectado.\n"); break; }
                        vistos.add(x);
                        if (i >= LIMITE) { areaSalida.append("— Límite alcanzado.\n"); break; }
                    }
                }

            } else if (alg.equals("Algoritmo congruencial aditivo")) {
                long m = leerLong(txtM, "m");
                int[] seeds = parseListaEnteros(txtSemillas);
                if (seeds.length == 0) throw new IllegalArgumentException("Falta la lista de semillas (lag-k).");
                int k = seeds.length;

                // Ventana de tamaño k con las semillas normalizadas
                long[] win = new long[k];
                for (int j = 0; j < k; j++) {
                    long xi = seeds[j];
                    xi = ((xi % m) + m) % m;
                    win[j] = xi;
                }

                HashSet<String> vistos = new HashSet<>();
                vistos.add(estadoLagK(win));

                while (true) {
                    long prev = win[k - 1]; // X_{i-1}
                    long lag  = win[0];     // X_{i-k}
                    long xi = (prev + lag) % m;

                    // Desplazar ventana: [x2,...,xk, xi]
                    for (int j = 0; j < k - 1; j++) win[j] = win[j + 1];
                    win[k - 1] = xi;

                    double r = xi / (double) m;
                    i++;
                    areaSalida.append(String.format(
                            "%d) X = (%d + %d) mod %d = %d; r = %s%n",
                            i, prev, lag, m, xi, fmt(r, dec)));

                    if (!ciclo) { if (i >= n) break; } else {
                        String estado = estadoLagK(win);
                        if (vistos.contains(estado)) { areaSalida.append("— Fin: ciclo detectado.\n"); break; }
                        vistos.add(estado);
                        if (i >= LIMITE) { areaSalida.append("— Límite alcanzado.\n"); break; }
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void guardarComoTxt() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("resultado.txt"));
            int r = fc.showSaveDialog(this);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                    pw.print(areaSalida.getText());
                }
                JOptionPane.showMessageDialog(this, "Guardado en: " + f.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(),
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Helpers =====

    private int leerEntero(JTextField tf, String nombre) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException("Falta " + nombre);
        return Integer.parseInt(s.trim());
    }
    private long leerLong(JTextField tf, String nombre) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException("Falta " + nombre);
        return Long.parseLong(s.trim());
    }
    private String leerTexto(JTextField tf, String nombre) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException("Falta " + nombre);
        return s.trim();
    }
    private int[] parseListaEnteros(JTextField tf) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty()) return new int[0];
        String[] p = s.split(",");
        int[] arr = new int[p.length];
        for (int i = 0; i < p.length; i++) arr[i] = Integer.parseInt(p[i].trim());
        return arr;
    }

    // Dígitos medios: toma k dígitos centrales (rellena con ceros a la izquierda si hace falta)
    private String extraerMedios(String s, int k) {
        int minLen = Math.max(2*k, k+2);
        if (s.length() < minLen) { while (s.length() < minLen) s = "0" + s; }
        int inicio = (s.length() - k) / 2;
        return s.substring(inicio, inicio + k);
    }
    private double toR_fromDigits(String digits, int k) {
        double x = new BigInteger(digits).doubleValue();
        return x / Math.pow(10, k);
    }

    // Decimales
    private int leerDecimales(JTextField tf) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty()) return 4;
        int d = Integer.parseInt(s.trim());
        if (d < 0) d = 0;
        if (d > 12) d = 12;
        return d;
    }
    private String fmt(double r, int dec) {
        return String.format("%." + dec + "f", r);
    }

    // Estado para detectar ciclo en lag-k (concatena la k-tupla)
    private String estadoLagK(long[] win) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < win.length; i++) {
            if (i > 0) sb.append('|');
            sb.append(win[i]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Algoritmos().setVisible(true));
    }
}
