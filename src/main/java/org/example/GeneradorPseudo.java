package org.example;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// FlatLaf (opcional). Si no está en el classpath, caemos a Nimbus.
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
// import com.formdev.flatlaf.FlatDarculaLaf; // si prefieres oscuro

public class GeneradorPseudo extends JFrame {

    // ---- Algoritmos disponibles ----
    private static final String A_CUADRADOS = "Algoritmo de cuadrados medios";
    private static final String A_PRODUCTOS = "Algoritmo de productos medios";
    private static final String A_MULTCONST  = "Algoritmo de multiplicador constante";
    private static final String A_LINEAL     = "Algoritmo lineal";
    private static final String A_MULTIPLIC  = "Algoritmo congruencial multiplicativo";
    private static final String A_ADITIVO    = "Algoritmo congruencial aditivo";
    private static final String A_NO_LINEAR  = "Algoritmos congruenciales no lineales";

    private final JComboBox<String> cbAlg = new JComboBox<>(new String[]{
            A_CUADRADOS, A_PRODUCTOS, A_MULTCONST, A_LINEAL, A_MULTIPLIC, A_ADITIVO, A_NO_LINEAR
    });

    // Campos generales
    private final JTextField tfN  = new JTextField("10");       // cantidad
    private final JTextArea  out  = new JTextArea(14, 60);

    // Campos para congruenciales / no lineales
    private final JTextField tfX0 = new JTextField("12345");    // semilla
    private final JTextField tfX1 = new JTextField("54321");    // semilla 2 (productos/aditivo Fibonacci)
    private final JTextField tfa  = new JTextField("1103515245");
    private final JTextField tfb  = new JTextField("0");        // b solo en no lineal si quisieras
    private final JTextField tfc  = new JTextField("12345");
    private final JTextField tfm  = new JTextField("2147483648"); // 2^31

    // Opciones específicas
    private final JCheckBox  chkAditivoFibo = new JCheckBox("Aditivo tipo Fibonacci (Xn = (Xn-1 + Xn-2) mod m)");
    private final JTextField tfKdig = new JTextField("5"); // nº de dígitos a conservar (mid-square/productos/constante)
    private final JTextField tfConst = new JTextField("24693"); // multiplicador constante para mid-const

    public GeneradorPseudo() {
        super("Generadores Pseudoaleatorios — FlatLaf");

        setJMenuBar(buildMenu());
        setContentPane(buildUI());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(840, 560));
        setLocationRelativeTo(null);

        cbAlg.addActionListener(e -> toggleFields());
        toggleFields();
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu mArchivo = new JMenu("Archivo");
        JMenuItem limpiar = new JMenuItem("Limpiar salida");
        limpiar.addActionListener(e -> out.setText(""));
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> dispose());
        mArchivo.add(limpiar);
        mArchivo.addSeparator();
        mArchivo.add(salir);

        JMenu mTema = new JMenu("Tema");
        JMenuItem claro = new JMenuItem("Flat Light");
        claro.addActionListener(e -> setLookAndFeel("com.formdev.flatlaf.FlatLightLaf"));
        JMenuItem nimbus = new JMenuItem("Nimbus");
        nimbus.addActionListener(e -> setLookAndFeel("Nimbus"));
        mTema.add(claro);
        mTema.add(nimbus);

        bar.add(mArchivo);
        bar.add(mTema);
        return bar;
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12,12,12,12));

        // Top
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("Algoritmo:"), gc);
        gc.gridx = 1; gc.gridy = r++; gc.weightx = 1; top.add(cbAlg, gc);

        gc.gridx = 0; gc.gridy = r; gc.weightx = 0; top.add(new JLabel("Cantidad (n):"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfN, gc);

        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("Semilla X0:"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfX0, gc);

        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("Semilla X1 (si aplica):"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfX1, gc);

        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("a:"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfa, gc);

        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("b (no lineal opc.):"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfb, gc);

        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("c:"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfc, gc);

        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("m:"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfm, gc);

        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("k dígitos medios:"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfKdig, gc);

        gc.gridx = 0; gc.gridy = r; top.add(new JLabel("Constante (multiplicador p/constante):"), gc);
        gc.gridx = 1; gc.gridy = r++; top.add(tfConst, gc);

        gc.gridx = 1; gc.gridy = r++; top.add(chkAditivoFibo, gc);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGenerar = new JButton("Generar");
        buttons.add(btnGenerar);
        btnGenerar.addActionListener(e -> generar());

        // Output
        out.setEditable(false);
        out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(out);

        root.add(top, BorderLayout.NORTH);
        root.add(sp, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        return root;
    }

    private void toggleFields() {
        String alg = (String) cbAlg.getSelectedItem();
        boolean usaCongruencial = A_LINEAL.equals(alg) || A_MULTIPLIC.equals(alg) || A_ADITIVO.equals(alg) || A_NO_LINEAR.equals(alg);
        boolean usaNoLineal = A_NO_LINEAR.equals(alg);
        boolean usaMedios = A_CUADRADOS.equals(alg) || A_PRODUCTOS.equals(alg) || A_MULTCONST.equals(alg);
        boolean usaX1 = A_PRODUCTOS.equals(alg) || (A_ADITIVO.equals(alg) && chkAditivoFibo.isSelected());

        tfX1.setEnabled(usaX1);
        tfa.setEnabled(usaCongruencial || A_MULTCONST.equals(alg)); // 'a' también para multiplicador constante
        tfb.setEnabled(usaNoLineal);
        tfc.setEnabled(A_LINEAL.equals(alg) || A_ADITIVO.equals(alg) || usaNoLineal);
        tfm.setEnabled(usaCongruencial);
        tfKdig.setEnabled(usaMedios);
        tfConst.setEnabled(A_MULTCONST.equals(alg));
        chkAditivoFibo.setEnabled(A_ADITIVO.equals(alg));
    }

    private void generar() {
        try {
            int n = Integer.parseInt(tfN.getText().trim());
            if (n <= 0) throw new NumberFormatException();

            String alg = (String) cbAlg.getSelectedItem();
            StringBuilder sb = new StringBuilder();
            sb.append("Algoritmo: ").append(alg).append("\n");

            switch (alg) {
                case A_CUADRADOS -> {
                    String x = tfX0.getText().trim();
                    int k = Integer.parseInt(tfKdig.getText().trim());
                    for (int i = 1; i <= n; i++) {
                        BigInteger val = new BigInteger(x);
                        val = val.multiply(val);
                        x = middleDigits(val.toString(), k);
                        double u = toU(x, k);
                        sb.append(String.format("%3d) X=%s  U=%.8f%n", i, x, u));
                    }
                }
                case A_PRODUCTOS -> {
                    String x0 = tfX0.getText().trim();
                    String x1 = tfX1.getText().trim();
                    int k = Integer.parseInt(tfKdig.getText().trim());
                    String prev = x0, curr = x1;
                    for (int i = 1; i <= n; i++) {
                        BigInteger prod = new BigInteger(prev).multiply(new BigInteger(curr));
                        String next = middleDigits(prod.toString(), k);
                        double u = toU(next, k);
                        sb.append(String.format("%3d) X=%s  U=%.8f%n", i, next, u));
                        prev = curr; curr = next;
                    }
                }
                case A_MULTCONST -> {
                    String x = tfX0.getText().trim();
                    int k = Integer.parseInt(tfKdig.getText().trim());
                    BigInteger a = new BigInteger(tfConst.getText().trim());
                    for (int i = 1; i <= n; i++) {
                        BigInteger val = new BigInteger(x).multiply(a);
                        x = middleDigits(val.toString(), k);
                        double u = toU(x, k);
                        sb.append(String.format("%3d) X=%s  U=%.8f%n", i, x, u));
                    }
                }
                case A_LINEAL -> {
                    long x = Long.parseLong(tfX0.getText().trim());
                    long a = Long.parseLong(tfa.getText().trim());
                    long c = Long.parseLong(tfc.getText().trim());
                    long m = Long.parseLong(tfm.getText().trim());
                    for (int i = 1; i <= n; i++) {
                        x = Math.floorMod(a * x + c, m);
                        sb.append(String.format("%3d) X=%d  U=%.8f%n", i, x, x / (double) m));
                    }
                }
                case A_MULTIPLIC -> {
                    long x = Long.parseLong(tfX0.getText().trim());
                    long a = Long.parseLong(tfa.getText().trim());
                    long m = Long.parseLong(tfm.getText().trim());
                    for (int i = 1; i <= n; i++) {
                        x = Math.floorMod(a * x, m);
                        sb.append(String.format("%3d) X=%d  U=%.8f%n", i, x, x / (double) m));
                    }
                }
                case A_ADITIVO -> {
                    long m = Long.parseLong(tfm.getText().trim());
                    if (chkAditivoFibo.isSelected()) {
                        // Fibonacci (k=2): Xn = (Xn-1 + Xn-2) mod m
                        long xnm2 = Long.parseLong(tfX0.getText().trim());
                        long xnm1 = Long.parseLong(tfX1.getText().trim());
                        for (int i = 1; i <= n; i++) {
                            long x = Math.floorMod(xnm1 + xnm2, m);
                            sb.append(String.format("%3d) X=%d  U=%.8f%n", i, x, x / (double) m));
                            xnm2 = xnm1; xnm1 = x;
                        }
                    } else {
                        // Aditivo simple: X_{n+1} = (X_n + c) mod m
                        long x = Long.parseLong(tfX0.getText().trim());
                        long c = Long.parseLong(tfc.getText().trim());
                        for (int i = 1; i <= n; i++) {
                            x = Math.floorMod(x + c, m);
                            sb.append(String.format("%3d) X=%d  U=%.8f%n", i, x, x / (double) m));
                        }
                    }
                }
                case A_NO_LINEAR -> {
                    // Cuadrático: X_{n+1} = (a X_n^2 + b X_n + c) mod m
                    long x = Long.parseLong(tfX0.getText().trim());
                    long a = Long.parseLong(tfa.getText().trim());
                    long b = Long.parseLong(tfb.getText().trim());
                    long c = Long.parseLong(tfc.getText().trim());
                    long m = Long.parseLong(tfm.getText().trim());
                    for (int i = 1; i <= n; i++) {
                        long xn1 = a * x * x + b * x + c;
                        xn1 = Math.floorMod(xn1, m);
                        x = xn1;
                        sb.append(String.format("%3d) X=%d  U=%.8f%n", i, x, x / (double) m));
                    }
                }
            }

            out.setText(sb.toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifica que todos los campos sean números válidos.",
                    "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ---- utilidades ----

    /** Devuelve los k dígitos medios de 's' (rellenando con ceros a la izquierda si hace falta). */
    private static String middleDigits(String s, int k) {
        // normalizar: al menos 2k dígitos (práctica habitual para mid-square)
        int minLen = Math.max(2 * k, k + 2);
        if (s.length() < minLen) s = String.format("%0" + minLen + "d", new BigInteger(s));

        int start = (s.length() - k) / 2;
        return s.substring(start, start + k);
    }

    /** Convierte k dígitos a U in [0,1). */
    private static double toU(String digits, int k) {
        double x = new BigInteger(digits).doubleValue();
        double den = Math.pow(10, k);
        return x / den;
    }

    private void setLookAndFeel(String lafName) {
        try {
            if ("Nimbus".equals(lafName)) {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } else {
                UIManager.setLookAndFeel(lafName);
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo aplicar el tema: " + lafName,
                    "Tema", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Intentar FlatLaf; si no está, usar Nimbus.
        try {
            FlatMacDarkLaf.setup(); // cambia por FlatDarculaLaf.setup() si prefieres oscuro
        } catch (Throwable t) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }
        EventQueue.invokeLater(() -> new GeneradorPseudo().setVisible(true));
    }
}
