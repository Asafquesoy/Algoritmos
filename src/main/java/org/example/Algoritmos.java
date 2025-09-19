package org.example;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import javax.swing.*;

/**
 * Proyecto: Algoritmos
 * 6 métodos: Cuadrados medios, Productos medios, Multiplicador constante,
 * Lineal (CORREGIDO: r = X/(m-1)), Congruencial multiplicativo, Aditivo (lag-k).
 *
 * - Todos los JTextField inician en blanco.
 * - Se habilitan solo los campos necesarios para el algoritmo elegido.
 * - Se imprime r=... (no U=...).
 * - Usa FlatLaf si está disponible (opcional).
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

    private JComboBox<String> comboAlgoritmo;

    private JTextField txtN, txtX0, txtX1, txtA, txtC, txtM, txtK, txtConstante;
    private JTextField txtSemillas;   // para aditivo lag-k (lista coma-separada)
    private JTextArea areaSalida;
    private JButton btnGenerar;

    public Algoritmos() {
        super("Generadores Pseudoaleatorios");

        // FlatLaf (opcional)
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}

        comboAlgoritmo = new JComboBox<>(OPCIONES);

        txtN = new JTextField();
        txtX0 = new JTextField();
        txtX1 = new JTextField();
        txtA  = new JTextField();
        txtC  = new JTextField();
        txtM  = new JTextField();
        txtK  = new JTextField();
        txtConstante = new JTextField();
        txtSemillas  = new JTextField();

        areaSalida = new JTextArea(15, 50);
        areaSalida.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaSalida);

        btnGenerar = new JButton("Generar");

        JPanel panelArriba = new JPanel(new GridLayout(0, 2, 5, 5));
        panelArriba.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panelArriba.add(new JLabel("Algoritmo:")); panelArriba.add(comboAlgoritmo);
        panelArriba.add(new JLabel("Cantidad n:")); panelArriba.add(txtN);

        panelArriba.add(new JLabel("Semilla X0:")); panelArriba.add(txtX0);
        panelArriba.add(new JLabel("Semilla X1:")); panelArriba.add(txtX1);

        panelArriba.add(new JLabel("a:")); panelArriba.add(txtA);
        panelArriba.add(new JLabel("c:")); panelArriba.add(txtC);
        panelArriba.add(new JLabel("m:")); panelArriba.add(txtM);

        panelArriba.add(new JLabel("k dígitos (medios):")); panelArriba.add(txtK);
        panelArriba.add(new JLabel("Constante (mult. constante):")); panelArriba.add(txtConstante);

        panelArriba.add(new JLabel("Semillas:")); panelArriba.add(txtSemillas);

        setLayout(new BorderLayout());
        add(panelArriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnGenerar, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 560);
        setLocationRelativeTo(null);

        comboAlgoritmo.addActionListener(e -> actualizarCampos());
        btnGenerar.addActionListener(e -> generar());

        actualizarCampos();
    }

    private void actualizarCampos() {
        String alg = (String) comboAlgoritmo.getSelectedItem();
        setAllEnabled(false);

        txtN.setEnabled(true);

        if (alg.equals("Algoritmo de cuadrados medios")) {
            txtX0.setEnabled(true);
            txtK.setEnabled(true);

        } else if (alg.equals("Algoritmo de productos medios")) {
            txtX0.setEnabled(true);
            txtX1.setEnabled(true);
            txtK.setEnabled(true);

        } else if (alg.equals("Algoritmo de multiplicador constante")) {
            txtX0.setEnabled(true);
            txtK.setEnabled(true);
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
        txtK.setEnabled(enabled);
        txtConstante.setEnabled(enabled);
        txtSemillas.setEnabled(enabled);
    }

    private void generar() {
        areaSalida.setText("");
        try {
            String alg = (String) comboAlgoritmo.getSelectedItem();
            int n = leerEntero(txtN, "n");

            if (alg.equals("Algoritmo de cuadrados medios")) {
                String x = leerTexto(txtX0, "X0");
                int k = leerEntero(txtK, "k");
                for (int i = 1; i <= n; i++) {
                    BigInteger v = new BigInteger(x).multiply(new BigInteger(x));
                    x = extraerMedios(v.toString(), k);
                    double r = toR_fromDigits(x, k);          // r = X/10^k
                    areaSalida.append(i + ") X=" + x + "  r=" + r + "\n");
                }

            } else if (alg.equals("Algoritmo de productos medios")) {
                String prev = leerTexto(txtX0, "X0");
                String curr = leerTexto(txtX1, "X1");
                int k = leerEntero(txtK, "k");
                for (int i = 1; i <= n; i++) {
                    BigInteger v = new BigInteger(prev).multiply(new BigInteger(curr));
                    String next = extraerMedios(v.toString(), k);
                    double r = toR_fromDigits(next, k);       // r = X/10^k
                    areaSalida.append(i + ") X=" + next + "  r=" + r + "\n");
                    prev = curr; curr = next;
                }

            } else if (alg.equals("Algoritmo de multiplicador constante")) {
                String x = leerTexto(txtX0, "X0");
                int k = leerEntero(txtK, "k");
                BigInteger aConst = new BigInteger(leerTexto(txtConstante, "Constante"));
                for (int i = 1; i <= n; i++) {
                    BigInteger v = new BigInteger(x).multiply(aConst);
                    x = extraerMedios(v.toString(), k);
                    double r = toR_fromDigits(x, k);          // r = X/10^k
                    areaSalida.append(i + ") X=" + x + "  r=" + r + "\n");
                }

            } else if (alg.equals("Algoritmo lineal")) {
                // CORREGIDO: r = X/(m-1)
                long x = leerLong(txtX0, "X0");
                long a = leerLong(txtA, "a");
                long c = leerLong(txtC, "c");
                long m = leerLong(txtM, "m");
                if (m <= 1) throw new IllegalArgumentException("m debe ser > 1 para usar r = X/(m-1).");

                for (int i = 1; i <= n; i++) {
                    x = Math.floorMod(a * x + c, m);          // X_{n+1} = (aX_n + c) mod m
                    double r = x / (double) (m - 1);          // *** r = X / (m - 1) ***
                    areaSalida.append(String.format("%d) X=%d  r=%.4f%n", i, x, r));
                }

            } else if (alg.equals("Algoritmo congruencial multiplicativo")) {
                long x = leerLong(txtX0, "X0");
                long a = leerLong(txtA, "a");
                long m = leerLong(txtM, "m");
                for (int i = 1; i <= n; i++) {
                    x = Math.floorMod(a * x, m);              // X_{n+1} = (aX_n) mod m
                    double r = (double) x / (m-1);                // (se mantiene como estaba)
                    areaSalida.append(i + ") X=" + x + "  r=" + r + "\n");
                }

            } else if (alg.equals("Algoritmo congruencial aditivo")) {
                long m = leerLong(txtM, "m");
                int[] seedsRaw = parseListaEnteros(txtSemillas);
                if (seedsRaw.length == 0)
                    throw new IllegalArgumentException("Falta la lista de semillas.");
                int k = seedsRaw.length;

                long[] X = new long[k + n];

                for (int i = 0; i < k; i++) {
                    long xi = seedsRaw[i];
                    xi = ((xi % m) + m) % m; // normalizar
                    X[i] = xi;
                }

                for (int i = k; i < k + n; i++) {
                    long xi = (X[i - 1] + X[i - k]) % m;      // lag-k
                    X[i] = xi;
                }

                for (int i = k; i < k + n; i++) {
                    long xi = X[i];
                    double r = (double) xi / (double) m;
                    int idx = (i - k) + 1;
                    // Mostrar r con 8 decimales
                    areaSalida.append(String.format("%d) X=%d  r=%.8f%n", idx, xi, r));
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    // -------- Helpers --------
    private int leerEntero(JTextField tf, String nombre) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Falta " + nombre);
        return Integer.parseInt(s.trim());
    }
    private long leerLong(JTextField tf, String nombre) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Falta " + nombre);
        return Long.parseLong(s.trim());
    }
    private String leerTexto(JTextField tf, String nombre) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Falta " + nombre);
        return s.trim();
    }
    private int[] parseListaEnteros(JTextField tf) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Falta la lista de semillas.");
        String[] partes = s.split(",");
        int[] arr = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            arr[i] = Integer.parseInt(partes[i].trim());
        }
        return arr;
    }

    // Utilidades de dígitos medios
    private String extraerMedios(String s, int k) {
        int minLen = Math.max(2 * k, k + 2);
        if (s.length() < minLen) {
            while (s.length() < minLen) s = "0" + s;
        }
        int inicio = (s.length() - k) / 2;
        return s.substring(inicio, inicio + k);
    }
    private double toR_fromDigits(String digits, int k) {
        double x = new BigInteger(digits).doubleValue();
        return x / Math.pow(10, k);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Algoritmos().setVisible(true));
    }
}
