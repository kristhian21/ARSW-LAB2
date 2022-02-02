package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    public static boolean enJuego = false;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private JButton iniciar;
    private JButton pausar;
    private JButton reaundar;
    private JLabel mejorSerpiente;
    private JLabel peorSerpiente;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];

    public SnakeApp() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 40);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        board = new Board();
        frame.add(board,BorderLayout.CENTER);
        JPanel actionsBPabel=new JPanel();
        actionsBPabel.setLayout(new FlowLayout());
        iniciar = new JButton("Iniciar");
        pausar = new JButton("Suspender");
        reaundar = new JButton("Reanudar");
        mejorSerpiente = new JLabel("Mejor serpiente: ");
        peorSerpiente = new JLabel("Peor serpiente: ");
        actionsBPabel.add(iniciar);
        actionsBPabel.add(pausar);
        actionsBPabel.add(reaundar);
        actionsBPabel.add(mejorSerpiente);
        actionsBPabel.add(peorSerpiente);
        frame.add(actionsBPabel,BorderLayout.SOUTH);
        prepararAcciones();
    }

    private void prepararAcciones(){
        iniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!enJuego){
                    enJuego = true;
                    for (int i = 0; i != MAX_THREADS; i++) {
                        snakes[i].setEnPausa(false);
                        synchronized (snakes[i]){
                            snakes[i].notify();
                        }
                    }
                }
            }
        });

        pausar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int mayor = 0;
                int indice = 0;
                for (int i = 0; i != MAX_THREADS; i++) {
                    snakes[i].setEnPausa(true);
                    if (!snakes[i].isSnakeEnd() && snakes[i].getBody().size() > mayor){
                        mayor = snakes[i].getBody().size();
                        indice = i;
                    }
                }
                mejorSerpiente.setText("Mejor serpiente: " + indice);
                peorSerpiente.setText("Peor serpiente: " + Snake.numeroPrimeraMuerta);
            }
        });

        reaundar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i != MAX_THREADS; i++) {
                    snakes[i].setEnPausa(false);
                    synchronized (snakes[i]){
                        snakes[i].notify();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.init();
    }

    private void init() {
        for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i] = new Snake(i + 1, spawn[i], i + 1);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
            thread[i].start();
        }

        frame.setVisible(true);

        while (true) {
            int x = 0;
            for (int i = 0; i != MAX_THREADS; i++) {
                if (snakes[i].isSnakeEnd() == true) {
                    x++;
                }
            }
            if (x == MAX_THREADS) {
                break;
            }
        }


        System.out.println("Thread (snake) status:");
        for (int i = 0; i != MAX_THREADS; i++) {
            System.out.println("["+i+"] :"+thread[i].getState());
        }
    }

    public static SnakeApp getApp() {
        return app;
    }

}
