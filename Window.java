import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Window extends JFrame {

    public static final int CANVAS_WIDTH = 780;
    public static final int CANVAS_HEIGHT = 480;
    public int currentWindowWidth;
    public int currentWindowHeight;
    public static final Color CANVAS_BG_COLOR = new Color(120,80,120);
    public Color playerColor = new Color(180,45,50);

    private DrawPanel drawPanel;
    private Player player;
    private ArrayList<Lazer> lazers;
    private ArrayList<HealPod> healPods;

    private Font font;

    private Engine engine;

    public Window() {
        drawPanel = new DrawPanel();
        drawPanel.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        JPanel labelPanel = new JPanel(new FlowLayout());
        labelPanel.add(new JLabel("Use the Arrow Keys to move. Hold shift to move slow."));

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(drawPanel, BorderLayout.CENTER);
        cp.add(labelPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Rendering test");
        pack();

        currentWindowWidth = drawPanel.getWidth();
        currentWindowHeight = drawPanel.getHeight();

        engine = new Engine(drawPanel,
                            new Player(CANVAS_WIDTH / 2 -10,CANVAS_HEIGHT / 2 -10,20, 20, playerColor));

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.getAllFonts();

        font = new Font("SukhumvitSet-Thin", Font.PLAIN, 35);

        addKeyListener(engine.getPlayer().controller);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle r = getBounds();
                currentWindowWidth = r.width;
                currentWindowHeight = r.height;
            }
        });

        setVisible(true);
        requestFocus();
    }

    class DrawPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(CANVAS_BG_COLOR);

            /**
             * Opptimalisere "Death message" ved å sette mest mulig med en gang
             * før man displayer det ved player.health == 0
             */
            boolean first = true;
            int fontX = 0;
            int fontY = 0;
            if(first) {
                g.setFont(font);
                FontMetrics metrics = g.getFontMetrics();
                fontX = ((currentWindowWidth / 2) - metrics.stringWidth("YOU'RE DEAD.") / 2);
                fontY = currentWindowHeight / 2 - metrics.getHeight() / 2 + metrics.getAscent();
                first = false;
            }

            for(int i = 0; i < engine.getLazers().size(); i++) {
                engine.getLazers().get(i).paint(g);
            }

            for(HealPod hp : engine.getHealPods()) {
                hp.paint(g);
            }

            engine.getPlayer().paint(g);
            engine.getPlayer().paintHealthbar(g, currentWindowWidth/23, currentWindowHeight/17,
                    currentWindowWidth/4, currentWindowHeight/13);

            repaint(currentWindowWidth/23, currentWindowHeight/17,
                    currentWindowWidth/4, currentWindowHeight/13);

            if(engine.getPlayer().health == 0) {
                g.setColor(Color.black);
                g.drawString("YOU'RE DEAD.", fontX, fontY);
                repaint();
            }

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                new Window();
            }
        });
    }
}