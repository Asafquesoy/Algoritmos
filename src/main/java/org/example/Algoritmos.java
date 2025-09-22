package org.example;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import javax.swing.*;

/**
 * Programa sencillo de generación de números pseudoaleatorios.
 * Algoritmos:
 *  - Cuadrados medios
 *  - Productos medios
 *  - Multiplicador constante
 *  - Lineal (r = X/(m-1))
 *  - Congruencial multiplicativo
 *  - Congruencial aditivo (lag-k)
 *
 * Lógica simple (if/else), campos en blanco, botón para guardar TXT y
 * selector de cantidad de decimales para r.
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
    private JTextField txtN, txtX0, txtX1, txtA, txtC, txtM, txtK, txtConstante, txtSemillas;
    private JTextField txtDecimales; // NUEVO: cantidad de decimales para r
    private JTextArea areaSalida;
    private JButton btnGenerar, btnGuardar;

    public Algoritmos() {
        super("Algoritmos");

        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}

        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/recursos/logo.png"));
            setIconImage(icon);
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
        txtDecimales = new JTextField(); // vacío por defecto (usa 4 si está vacío)

        areaSalida = new JTextArea(15, 60);
        areaSalida.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaSalida);

        btnGenerar = new JButton("Generar");
        btnGuardar = new JButton("Guardar");

        JPanel arriba = new JPanel(new GridLayout(0, 2, 5, 5));
        arriba.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        arriba.add(new JLabel("Algoritmo:")); arriba.add(comboAlgoritmo);
        arriba.add(new JLabel("Cantidad n:")); arriba.add(txtN);

        arriba.add(new JLabel("Semilla X0:")); arriba.add(txtX0);
        arriba.add(new JLabel("Semilla X1 (solo productos):")); arriba.add(txtX1);

        arriba.add(new JLabel("a:")); arriba.add(txtA);
        arriba.add(new JLabel("c:")); arriba.add(txtC);
        arriba.add(new JLabel("m:")); arriba.add(txtM);

        arriba.add(new JLabel("k dígitos (medios):")); arriba.add(txtK);
        arriba.add(new JLabel("Constante (mult. constante):")); arriba.add(txtConstante);

        arriba.add(new JLabel("Semillas:")); arriba.add(txtSemillas);

        // NUEVO: selector de decimales
        arriba.add(new JLabel("Decimales:")); arriba.add(txtDecimales);

        JPanel abajo = new JPanel();
        abajo.add(btnGuardar);
        abajo.add(btnGenerar);

        setLayout(new BorderLayout());
        add(arriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(840, 600);
        setLocationRelativeTo(null);

        comboAlgoritmo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { actualizarCampos(); }
        });
        btnGenerar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { generar(); }
        });
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { guardarComoTxt(); }
        });

        actualizarCampos();
    }

    private void actualizarCampos() {
        String alg = (String) comboAlgoritmo.getSelectedItem();
        setAllEnabled(false);
        txtN.setEnabled(true);
        txtDecimales.setEnabled(true); // siempre configurable

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
        txtDecimales.setEnabled(enabled);
    }

    private void generar() {
        areaSalida.setText("");
        try {
            String alg = (String) comboAlgoritmo.getSelectedItem();
            int n = leerEntero(txtN, "n");
            int dec = leerDecimales(txtDecimales); // 0-12, por defecto 4

            if (alg.equals("Algoritmo de cuadrados medios")) {
                String x = leerTexto(txtX0, "X0");
                int k = leerEntero(txtK, "k");
                for (int i = 1; i <= n; i++) {
                    BigInteger v = new BigInteger(x).multiply(new BigInteger(x));
                    x = extraerMedios(v.toString(), k);
                    double r = toR_fromDigits(x, k);
                    areaSalida.append(i + ") X=" + x + "  r=" + fmt(r, dec) + "\n");
                }

            } else if (alg.equals("Algoritmo de productos medios")) {
                String prev = leerTexto(txtX0, "X0");
                String curr = leerTexto(txtX1, "X1");
                int k = leerEntero(txtK, "k");
                for (int i = 1; i <= n; i++) {
                    BigInteger v = new BigInteger(prev).multiply(new BigInteger(curr));
                    String next = extraerMedios(v.toString(), k);
                    double r = toR_fromDigits(next, k);
                    areaSalida.append(i + ") X=" + next + "  r=" + fmt(r, dec) + "\n");
                    prev = curr; curr = next;
                }

            } else if (alg.equals("Algoritmo de multiplicador constante")) {
                String x = leerTexto(txtX0, "X0");
                int k = leerEntero(txtK, "k");
                BigInteger aConst = new BigInteger(leerTexto(txtConstante, "Constante"));
                for (int i = 1; i <= n; i++) {
                    BigInteger v = new BigInteger(x).multiply(aConst);
                    x = extraerMedios(v.toString(), k);
                    double r = toR_fromDigits(x, k);
                    areaSalida.append(i + ") X=" + x + "  r=" + fmt(r, dec) + "\n");
                }

            } else if (alg.equals("Algoritmo lineal")) {
                long x = leerLong(txtX0, "X0");
                long a = leerLong(txtA, "a");
                long c = leerLong(txtC, "c");
                long m = leerLong(txtM, "m");
                if (m <= 1) throw new IllegalArgumentException("m debe ser > 1 para usar r = X/(m-1).");
                for (int i = 1; i <= n; i++) {
                    x = Math.floorMod(a * x + c, m);
                    double r = x / (double) (m - 1);
                    areaSalida.append(i + ") X=" + x + "  r=" + fmt(r, dec) + "\n");
                }

            } else if (alg.equals("Algoritmo congruencial multiplicativo")) {
                long x = leerLong(txtX0, "X0");
                long a = leerLong(txtA, "a");
                long m = leerLong(txtM, "m");
                for (int i = 1; i <= n; i++) {
                    x = Math.floorMod(a * x, m);
                    double r = x / (double) m;
                    areaSalida.append(i + ") X=" + x + "  r=" + fmt(r, dec) + "\n");
                }

            } else if (alg.equals("Algoritmo congruencial aditivo")) {
                long m = leerLong(txtM, "m");
                int[] seeds = parseListaEnteros(txtSemillas);
                if (seeds.length == 0) throw new IllegalArgumentException("Falta la lista de semillas (lag-k).");
                int k = seeds.length;

                long[] X = new long[k + n];
                for (int i = 0; i < k; i++) {
                    long xi = seeds[i];
                    xi = ((xi % m) + m) % m;
                    X[i] = xi;
                }
                for (int i = k; i < k + n; i++) {
                    long xi = (X[i - 1] + X[i - k]) % m;
                    X[i] = xi;
                }
                for (int i = k; i < k + n; i++) {
                    long xi = X[i];
                    double r = xi / (double) m;
                    int idx = (i - k) + 1;
                    areaSalida.append(idx + ") X=" + xi + "  r=" + fmt(r, dec) + "\n");
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

    private String extraerMedios(String s, int k) {
        int minLen = Math.max(2*k, k+2);
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

    // NUEVO: lectura y formateo de decimales
    private int leerDecimales(JTextField tf) {
        String s = tf.getText();
        if (s == null || s.trim().isEmpty()) return 4; // por defecto
        int d = Integer.parseInt(s.trim());
        if (d < 0) d = 0;
        if (d > 12) d = 12;
        return d;
    }
    private String fmt(double r, int dec) {
        return String.format("%." + dec + "f", r);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Algoritmos().setVisible(true));
    }
}
