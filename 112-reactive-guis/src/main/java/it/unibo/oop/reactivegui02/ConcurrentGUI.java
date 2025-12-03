package it.unibo.oop.reactivegui02;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentGUI.class);
    private final JLabel display = new JLabel(); 

    /** 
     * Createsa new ConcurrentGUI with command up, down, stop.
     */
    public ConcurrentGUI() {
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

        final Agent agent = new Agent();
        new Thread(agent).start();

        stop.addActionListener(s -> agent.stop());
        up.addActionListener(u -> agent.moveUp());
        down.addActionListener(d -> agent.moveDown());

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
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
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
}
