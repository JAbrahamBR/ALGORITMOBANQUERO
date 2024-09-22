import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Main extends JFrame {
    private JTable tableMax, tableAlloc, tableNeed;
    private JTextField[] resourceFields;
    private JTextField[] availableFields;
    private int[][] maximum = new int[3][3];
    private int[][] allocation = new int[3][3];
    private int[][] need = new int[3][3];
    private int[] totalResources = {10, 5, 7};
    private int[] availableResources = {14, 6, 5};
    private Random random = new Random();

    public Main() {
        setTitle("Algoritmo del Banquero");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.PINK);
        add(panel, BorderLayout.CENTER);


        JPanel titlePanel = new JPanel(new GridLayout(1, 3));
        titlePanel.setBackground(Color.GRAY);
        titlePanel.add(new JLabel("Máximo", SwingConstants.CENTER));
        titlePanel.add(new JLabel("Asignación", SwingConstants.CENTER));
        titlePanel.add(new JLabel("Necesidad", SwingConstants.CENTER));

        // el codigo de tablass
        tableMax = crearTabla();
        tableAlloc = crearTabla();
        tableNeed = crearTabla();

        JScrollPane scrollPaneMax = new JScrollPane(tableMax);
        JScrollPane scrollPaneAlloc = new JScrollPane(tableAlloc);
        JScrollPane scrollPaneNeed = new JScrollPane(tableNeed);

        JPanel tablePanel = new JPanel(new GridLayout(1, 3));
        tablePanel.setBackground(Color.darkGray); //colorsito
        tablePanel.add(scrollPaneMax);
        tablePanel.add(scrollPaneAlloc);
        tablePanel.add(scrollPaneNeed);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);


        tableMax.setBackground(Color.LIGHT_GRAY);
        tableAlloc.setBackground(Color.LIGHT_GRAY);
        tableNeed.setBackground(Color.LIGHT_GRAY);


        JPanel resourcePanel = new JPanel(new GridLayout(1, 2));
        resourcePanel.setBackground(Color.LIGHT_GRAY);
        resourcePanel.add(new JLabel("Recursos Totales:"));
        resourceFields = new JTextField[totalResources.length];
        JPanel totalResourcesPanel = new JPanel(new GridLayout(1, totalResources.length));
        totalResourcesPanel.setBackground(Color.LIGHT_GRAY); // Color de fondo
        for (int i = 0; i < totalResources.length; i++) {
            resourceFields[i] = new JTextField(String.valueOf(totalResources[i]));
            resourceFields[i].setEditable(false);
            totalResourcesPanel.add(resourceFields[i]);
        }
        resourcePanel.add(totalResourcesPanel);

        resourcePanel.add(new JLabel("Recursos Disponibles:"));
        availableFields = new JTextField[totalResources.length];
        JPanel availableResourcesPanel = new JPanel(new GridLayout(1, totalResources.length));
        availableResourcesPanel.setBackground(Color.LIGHT_GRAY);
        for (int i = 0; i < totalResources.length; i++) {
            availableFields[i] = new JTextField(String.valueOf(availableResources[i]));
            availableFields[i].setEditable(false);
            availableResourcesPanel.add(availableFields[i]);
        }
        resourcePanel.add(availableResourcesPanel);

        add(resourcePanel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel();
        JButton requestButton = new JButton("Solicitar Recursos");
        JButton releaseButton = new JButton("Liberar Recursos");
        buttonPanel.add(requestButton);
        buttonPanel.add(releaseButton);
        add(buttonPanel, BorderLayout.PAGE_END);


        requestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manejarSolicitud();
            }
        });

        releaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manejarLiberacion();
            }
        });


        reiniciarDatos();
        calcularNecesidad();
        actualizarTablas();
    }

    private JTable crearTabla() {
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"R1", "R2", "R3"}, 3);
        JTable tabla = new JTable(modelo);
        tabla.setBorder(BorderFactory.createEmptyBorder());
        return tabla;
    }

    private void actualizarTablas() {
        actualizarTabla(tableMax, maximum);
        actualizarTabla(tableAlloc, allocation);
        actualizarTabla(tableNeed, need);
        actualizarRecursosDisponibles();
        verificarReinicio();
    }

    private void actualizarTabla(JTable tabla, int[][] datos) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        for (int i = 0; i < datos.length; i++) {
            for (int j = 0; j < datos[i].length; j++) {
                modelo.setValueAt(datos[i][j], i, j);
            }
        }
    }

    private void actualizarRecursosDisponibles() {
        for (int i = 0; i < availableResources.length; i++) {
            availableFields[i].setText(String.valueOf(availableResources[i]));
        }
    }

    private void calcularNecesidad() {
        for (int i = 0; i < need.length; i++) {
            for (int j = 0; j < need[i].length; j++) {
                need[i][j] = maximum[i][j] - allocation[i][j];
            }
        }
    }

    private void manejarSolicitud() {
        try {
            int proceso = Integer.parseInt(JOptionPane.showInputDialog(this, "Ingrese el número del proceso (0, 1, 2):"));
            if (proceso < 0 || proceso >= allocation.length) {
                JOptionPane.showMessageDialog(this, "Número de proceso inválido.");
                return;
            }
            int[] solicitud = new int[totalResources.length];
            for (int i = 0; i < totalResources.length; i++) {
                solicitud[i] = Integer.parseInt(JOptionPane.showInputDialog(this, "Ingrese la cantidad de recurso R" + (i + 1) + " que solicita el proceso " + proceso + ":"));
                if (solicitud[i] < 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad solicitada no puede ser negativa.");
                    return;
                }
            }

            if (esSegura(solicitud, proceso)) {
                // Actualizar matrices de asignación y recursos disponibles
                for (int i = 0; i < totalResources.length; i++) {
                    allocation[proceso][i] += solicitud[i];
                    availableResources[i] -= solicitud[i];
                    need[proceso][i] -= solicitud[i];
                }
                JOptionPane.showMessageDialog(this, "Solicitud concedida.");
            } else {
                JOptionPane.showMessageDialog(this, "La solicitud no es segura.");
            }
            actualizarTablas();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Entrada inválida.");
        }
    }

    private boolean esSegura(int[] solicitud, int proceso) {

        for (int i = 0; i < solicitud.length; i++) {
            if (solicitud[i] > need[proceso][i]) {
                return false;
            }
        }


        int[] tempAvailable = availableResources.clone();
        int[][] tempAllocation = new int[3][3];
        int[][] tempNeed = new int[3][3];
        for (int i = 0; i < allocation.length; i++) {
            System.arraycopy(allocation[i], 0, tempAllocation[i], 0, allocation[i].length);
        }
        for (int i = 0; i < need.length; i++) {
            System.arraycopy(need[i], 0, tempNeed[i], 0, need[i].length);
        }

        for (int i = 0; i < solicitud.length; i++) {
            tempAvailable[i] -= solicitud[i];
            tempAllocation[proceso][i] += solicitud[i];
            tempNeed[proceso][i] -= solicitud[i];
        }


        boolean[] finish = new boolean[3];
        int[] work = tempAvailable.clone();
        boolean progressMade;

        do {
            progressMade = false;
            for (int i = 0; i < finish.length; i++) {
                if (!finish[i]) {
                    boolean canFinish = true;
                    for (int j = 0; j < totalResources.length; j++) {
                        if (tempNeed[i][j] > work[j]) {
                            canFinish = false;
                            break;
                        }
                    }
                    if (canFinish) {
                        for (int j = 0; j < totalResources.length; j++) {
                            work[j] += tempAllocation[i][j];
                        }
                        finish[i] = true;
                        progressMade = true;
                    }
                }
            }
        } while (progressMade);

        for (boolean f : finish) {
            if (!f) {
                return false;
            }
        }

        return true;
    }

    private void manejarLiberacion() {
        try {
            int proceso = Integer.parseInt(JOptionPane.showInputDialog(this, "Ingrese el número del proceso (0, 1, 2):"));
            if (proceso < 0 || proceso >= allocation.length) {
                JOptionPane.showMessageDialog(this, "Número de proceso inválido.");
                return;
            }
            int[] liberacion = new int[totalResources.length];
            for (int i = 0; i < totalResources.length; i++) {
                liberacion[i] = Integer.parseInt(JOptionPane.showInputDialog(this, "Ingrese la cantidad de recurso R" + (i + 1) + " que libera el proceso " + proceso + ":"));
                if (liberacion[i] < 0 || liberacion[i] > allocation[proceso][i]) {
                    JOptionPane.showMessageDialog(this, "La cantidad liberada no puede ser negativa o mayor que la cantidad asignada.");
                    return;
                }
            }


            for (int i = 0; i < totalResources.length; i++) {
                allocation[proceso][i] -= liberacion[i];
                availableResources[i] += liberacion[i];
                need[proceso][i] += liberacion[i];
            }
            JOptionPane.showMessageDialog(this, "Liberación completada.");
            actualizarTablas();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Entrada inválida.");
        }
    }

    private void reiniciarDatos() {
        for (int i = 0; i < maximum.length; i++) {
            for (int j = 0; j < maximum[i].length; j++) {
                maximum[i][j] = random.nextInt(10) + 1;
                allocation[i][j] = random.nextInt(maximum[i][j] + 1);
            }
        }
        calcularNecesidad();
        availableResources = totalResources.clone();
        actualizarTablas();
    }

    private void verificarReinicio() {
        boolean necesidadZero = true;
        boolean asignacionMaxima = true;


        for (int i = 0; i < need.length; i++) {
            for (int j = 0; j < need[i].length; j++) {
                if (need[i][j] != 0) {
                    necesidadZero = false;
                    break;
                }
            }
        }


        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < allocation[i].length; j++) {
                if (allocation[i][j] != maximum[i][j]) {
                    asignacionMaxima = false;
                    break;
                }
            }
        }

        if (necesidadZero && asignacionMaxima) {
            reiniciarDatos();
            JOptionPane.showMessageDialog(this, "Tablas reiniciadas con datos aleatorios.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
