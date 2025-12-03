package it.unibo.oop.reactivegui03;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.oop.JFrameUtil;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnotherConcurrentGUI.class);
    private static final int WAIT = 10_000;

    private final JLabel display = new JLabel(); 
    private final transient Agent agent = new Agent();

    /** 
     * Creates new ConcurrentGUI that stops after 10 seconds.
     */
    public AnotherConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        final JButton stop = new JButton("Stop");
        panel.add(stop);
        final JButton down = new JButton("Down");
        panel.add(down);
        final JButton up = new JButton("Up");
        panel.add(up);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final StopAgent stopAgent = new StopAgent();
        final Thread master = new Thread(stopAgent);
        final Thread consumer = new Thread(this.agent);
        consumer.start();
        stop.addActionListener(s -> this.agent.stop());
        up.addActionListener(u -> this.agent.moveUp());
        down.addActionListener(d -> this.agent.moveDown());
        master.start();
    }

    private final class Agent implements Runnable {
        private volatile boolean stop;
        private int count;
        private volatile int direction;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.count);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (direction == 1) {
                        count++;
                    } else if (direction == -1) {
                        count--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        public void stop() {
            this.stop = true;
        }

        public void moveUp() {
            this.direction = 1;
        }

        public void moveDown() {
            this.direction = -1;
        }
    }

    private final class StopAgent implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(WAIT);
            } catch (final InterruptedException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            AnotherConcurrentGUI.this.agent.stop();
        }
    }
}
