package worker;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import entity.Actor;
import entity.Entity;
import entity.Wheat;
import entity.WheatGrain;
import environment.Environment;
import interpreter.Interpreter;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JSlider;
import javax.swing.JLabel;

public class Gui {

	private JFrame frmSocialEvolutionSimulator;

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frmSocialEvolutionSimulator.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private Properties properties;
	private Interpreter interpreter;
	private Environment environment;
	
	private final BufferedImage wheatImg;
	private final BufferedImage actorImg;
	private final BufferedImage wheatGrainImg;
	
	private JPanel canvas;
	private Timer timer;
	private JToggleButton btnStep;
	private JSlider fpsSlider;
	private JSlider scaleSlider;
	private int scaler;

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public Gui() throws IOException {
		properties = PropertyFileReader.getProperties();
		interpreter = new Interpreter(properties);
		environment = new Environment(properties, interpreter);
		
		wheatImg = ImageIO.read(new File("./sprites/wheat.png"));
		actorImg = ImageIO.read(new File("./sprites/actor.png"));
		wheatGrainImg = ImageIO.read(new File("./sprites/wheat_grain.png"));
		
		scaler = 20;
		
		initialize(environment);
		
		timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	if (btnStep.isSelected()) {
            		paintAgain();
	            	environment.step();
	    			
            	}
            	timer.setDelay(1000/(int)fpsSlider.getValue());
            	scaler = (int)scaleSlider.getValue();
            	canvas.revalidate();
            	
            }
        });
		timer.start();
	}
	
	public void paintAgain() {
		canvas.repaint();
	}

	private void initialize(Environment env) {
		frmSocialEvolutionSimulator = new JFrame();
		frmSocialEvolutionSimulator.setTitle("Social Evolution  Simulator");
		frmSocialEvolutionSimulator.setBounds(0, 0, 1920, 1080);
		frmSocialEvolutionSimulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSocialEvolutionSimulator.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(22, 49, 1816, 955);
		frmSocialEvolutionSimulator.getContentPane().add(scrollPane);
		
		btnStep = new JToggleButton("Play");
		btnStep.setBounds(12, 12, 117, 25);
		frmSocialEvolutionSimulator.getContentPane().add(btnStep);
		
		canvas = new JPanel() {
			public Dimension getPreferredSize()
		    {
		        return(new Dimension(env.getWorldWidth()*scaler, env.getWorldHeight()*scaler) );
		    }
			public void paintComponent(Graphics g) {
				
				
				double xMultiplier = this.getWidth()/(double)environment.getWorldWidth();
				double yMultiplier = this.getHeight()/(double)environment.getWorldHeight();
				g.setColor(Color.green);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				for (int x=0; x<environment.getWorldWidth(); x++) {
					for (int y=0; y<environment.getWorldHeight(); y++) {
						Entity newEntity = environment.getEntity(x, y);
						
						
						int startingY = this.getHeight()-(int)Math.round(y*yMultiplier) - (int)Math.round(yMultiplier);
						int startingX = (int)Math.round(x*xMultiplier);
						if (newEntity != null) {
							if (newEntity instanceof Wheat) {
								g.drawImage(wheatImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
							} else if (newEntity instanceof Actor) {
								g.drawImage(actorImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								
							} else if (newEntity instanceof WheatGrain) {
								g.drawImage(wheatGrainImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
							}
						}
						
					}
				}
				
				for (int x=0; x<environment.getWorldWidth(); x++) {
					for (int y=0; y<environment.getWorldHeight(); y++) {
						Entity newEntity = environment.getEntity(x, y);
						
						
						int startingY = this.getHeight()-(int)Math.round(y*yMultiplier) - (int)Math.round(yMultiplier);
						int startingX = (int)Math.round(x*xMultiplier);
						if (newEntity != null) {
							if (newEntity instanceof Actor) {
								Entity[] withinRange = ((Actor)(newEntity)).getRayCasts(env);
								//Arrays.sort(withinRange, (a,b));
								for(int ray=0; ray<15;ray++) {
									
									Graphics2D g2 = (Graphics2D) g;
									g2.setStroke(new BasicStroke(2));
									if (withinRange[ray] != null) {
										int[] pClosest = withinRange[ray].getPos();
										

										int startx = (startingX+(int)Math.round(xMultiplier/2));
										int starty = (startingY+(int)Math.round(yMultiplier/2));
										int endx = (int)(pClosest[0]*xMultiplier) + (int)Math.round(yMultiplier/2);
										int endy = this.getHeight()-(int)Math.round(pClosest[1]*yMultiplier) - (int)Math.round(yMultiplier*0.5);
										g.setColor(Color.red);
										g.drawLine(startx, starty,  endx, endy);
									}
								}
							}
						}
						
					}
				}
			}
		};
		canvas.setBackground(Color.WHITE);

		scrollPane.setViewportView(canvas);
		
		fpsSlider = new JSlider();
		fpsSlider.setMaximum(20);
		fpsSlider.setMinimum(1);
		fpsSlider.setBounds(209, 12, 200, 16);
		frmSocialEvolutionSimulator.getContentPane().add(fpsSlider);
		
		scaleSlider = new JSlider();
		scaleSlider.setValue(5);
		scaleSlider.setMaximum(50);
		scaleSlider.setMinimum(5);
		scaleSlider.setBounds(486, 12, 200, 16);
		frmSocialEvolutionSimulator.getContentPane().add(scaleSlider);
		
		JLabel lblTime = new JLabel("FPS");
		lblTime.setBounds(147, 17, 70, 15);
		frmSocialEvolutionSimulator.getContentPane().add(lblTime);
		
		JLabel lblScale = new JLabel("Scale");
		lblScale.setBounds(430, 17, 70, 15);
		frmSocialEvolutionSimulator.getContentPane().add(lblScale);
		scrollPane.getVerticalScrollBar().setUnitIncrement(25);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(25);
		
		
	}
}
