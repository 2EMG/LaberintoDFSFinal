import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class LaberintoDFS extends JFrame {
    private JPanel panelLaberinto;
    private int tamaño = 20;
    private int celdaSize = 25;
    private boolean[][] laberinto;
    private boolean[][] visitado;
    private boolean[][] caminoSolucion;
    private boolean[][] caminoOptimo;
    private Random random = new Random();
    private javax.swing.Timer timerExploracion;
    private javax.swing.Timer timerCaminoOptimo;
    private Stack<Point> pila = new Stack<>();
    private Point entrada = new Point(0, 0);
    private Point salida;
    private boolean solucionEncontrada = false;
    private Map<Point, Point> padres = new HashMap<>();
    private List<Point> mejorCamino = new ArrayList<>();
    private int pasoActual = 0;

    public LaberintoDFS() {
        setTitle("Laberinto DFS con Camino Óptimo");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnGenerar = new JButton("Generar Laberinto");
        JButton btnBorrar = new JButton("Borrar");
        JButton btnResolver = new JButton("Resolver con DFS");

        btnGenerar.addActionListener(e -> {
            generarLaberinto();
            panelLaberinto.repaint();
        });

        btnBorrar.addActionListener(e -> {
            borrarLaberinto();
            panelLaberinto.repaint();
        });

        btnResolver.addActionListener(e -> {
            if (laberinto != null) {
                resolverLaberintoDFS();
            }
        });

        panelBotones.add(btnGenerar);
        panelBotones.add(btnBorrar);
        panelBotones.add(btnResolver);

        // Panel del laberinto
        panelLaberinto = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (laberinto != null) {
                    for (int i = 0; i < tamaño; i++) {
                        for (int j = 0; j < tamaño; j++) {
                            // Dibujar celdas
                            if (i == entrada.x && j == entrada.y) {
                                g.setColor(Color.GREEN); // Entrada
                            } else if (i == salida.x && j == salida.y) {
                                g.setColor(solucionEncontrada ? Color.BLUE : Color.RED); // Salida
                            } else if (caminoOptimo != null && caminoOptimo[i][j]) {
                                g.setColor(Color.MAGENTA); // Camino óptimo
                            } else if (caminoSolucion != null && caminoSolucion[i][j]) {
                                g.setColor(Color.CYAN); // Exploración
                            } else if (laberinto[i][j]) {
                                g.setColor(Color.BLACK); // Pared
                            } else {
                                g.setColor(Color.WHITE); // Pasillo
                            }
                            g.fillRect(j * celdaSize, i * celdaSize, celdaSize, celdaSize);

                            // Dibujar bordes
                            g.setColor(Color.GRAY);
                            g.drawRect(j * celdaSize, i * celdaSize, celdaSize, celdaSize);
                        }
                    }
                }
            }
        };
        panelLaberinto.setPreferredSize(new Dimension(tamaño * celdaSize, tamaño * celdaSize));

        add(panelBotones, BorderLayout.NORTH);
        add(panelLaberinto, BorderLayout.CENTER);

        // Timer para la fase de exploración
        timerExploracion = new javax.swing.Timer(20, e -> {
            if (!pila.isEmpty() && !solucionEncontrada) {
                Point actual = pila.pop();
                visitado[actual.x][actual.y] = true;
                caminoSolucion[actual.x][actual.y] = true;

                // Verificar si llegamos a la salida
                if (actual.equals(salida)) {
                    solucionEncontrada = true;
                    reconstruirCaminoOptimo();
                    timerExploracion.stop();
                    mostrarCaminoOptimo();
                    return;
                }

                agregarVecinos(actual);
                panelLaberinto.repaint();
            } else if (!solucionEncontrada) {
                timerExploracion.stop();
                JOptionPane.showMessageDialog(this, "No se encontró solución");
            }
        });

        // Timer para mostrar el camino óptimo
        timerCaminoOptimo = new javax.swing.Timer(100, e -> {
            if (pasoActual < mejorCamino.size()) {
                Point paso = mejorCamino.get(pasoActual);
                caminoOptimo[paso.x][paso.y] = true;
                panelLaberinto.repaint();
                pasoActual++;
            } else {
                timerCaminoOptimo.stop();
                JOptionPane.showMessageDialog(this, 
                    "Camino óptimo encontrado! Longitud: " + mejorCamino.size());
            }
        });

        salida = new Point(tamaño-1, tamaño-1);
        borrarLaberinto();
    }

    private void generarLaberinto() {
        solucionEncontrada = false;
        laberinto = new boolean[tamaño][tamaño];
        // Generar un laberinto con caminos más conectados
        for (int i = 0; i < tamaño; i++) {
            for (int j = 0; j < tamaño; j++) {
                laberinto[i][j] = random.nextDouble() < 0.2; // 20% de probabilidad de pared
            }
        }
        // Asegurar entrada y salida libres
        laberinto[entrada.x][entrada.y] = false;
        laberinto[salida.x][salida.y] = false;
        caminoSolucion = null;
        caminoOptimo = null;
    }

    private void borrarLaberinto() {
        solucionEncontrada = false;
        laberinto = new boolean[tamaño][tamaño];
        caminoSolucion = null;
        caminoOptimo = null;
    }

    private void resolverLaberintoDFS() {
        solucionEncontrada = false;
        visitado = new boolean[tamaño][tamaño];
        caminoSolucion = new boolean[tamaño][tamaño];
        caminoOptimo = new boolean[tamaño][tamaño];
        padres.clear();
        mejorCamino.clear();
        pasoActual = 0;
        pila.clear();

        pila.push(entrada);
        visitado[entrada.x][entrada.y] = true;
        padres.put(entrada, null); // La entrada no tiene padre
        timerExploracion.start();
    }

    private void agregarVecinos(Point actual) {
        int[] dx = {0, 1, 0, -1}; // Derecha, Abajo, Izquierda, Arriba
        int[] dy = {1, 0, -1, 0};

        for (int i = 0; i < 4; i++) {
            int nx = actual.x + dx[i];
            int ny = actual.y + dy[i];

            if (nx >= 0 && nx < tamaño && ny >= 0 && ny < tamaño && 
                !laberinto[nx][ny] && !visitado[nx][ny]) {
                Point vecino = new Point(nx, ny);
                pila.push(vecino);
                visitado[nx][ny] = true;
                padres.put(vecino, actual); // Guardamos quién es el padre
            }
        }
    }

    private void reconstruirCaminoOptimo() {
        mejorCamino.clear();
        Point actual = salida;
        
        // Reconstruir el camino desde la salida hasta la entrada
        while (actual != null) {
            mejorCamino.add(actual);
            actual = padres.get(actual);
        }
        
        // Invertir para tener el camino de entrada a salida
        Collections.reverse(mejorCamino);
    }

    private void mostrarCaminoOptimo() {
        // Limpiar el camino de exploración
        caminoSolucion = new boolean[tamaño][tamaño];
        // Preparar para mostrar el camino óptimo
        caminoOptimo = new boolean[tamaño][tamaño];
        pasoActual = 0;
        timerCaminoOptimo.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LaberintoDFS laberinto = new LaberintoDFS();
            laberinto.setVisible(true);
        });
    }
}