package main.java.worker;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import main.java.entity.Actor;
import main.java.entity.Entity;
import main.java.entity.Meat;
import main.java.entity.Tree;
import main.java.entity.Water;
import main.java.entity.Wheat;
import main.java.entity.WheatGrain;
import main.java.entity.Wood;
import main.java.entity.WoodTool;
import main.java.environment.Environment;
import main.java.interpreter.Interpreter;

import javax.imageio.ImageIO;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
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
	private final BufferedImage actorImgRight;
	private final BufferedImage actorImgUp;
	private final BufferedImage actorImgLeft;
	private final BufferedImage actorImgDown;
	private final BufferedImage wheatGrainImg;
	private final BufferedImage grassImg;
	private final BufferedImage waterImg;
	private final BufferedImage meatImg;
	private final BufferedImage treeImg;
	private final BufferedImage woodImg;
	private final BufferedImage woodToolImg;
	
	private JPanel canvas;
	private Timer timer;
	private JToggleButton btnStep;
	private JToggleButton btnShowWorld;
	private JSlider fpsSlider;
	private JSlider scaleSlider;
	private int scaler;
	private JToggleButton btnShowVisibleMask;
	private JToggleButton btnShowRays;

	public Gui() throws IOException {
		properties = PropertyFileReader.getProperties();
		interpreter = new Interpreter(properties);
		environment = new Environment(properties, interpreter, new Random());
		
		wheatImg = ImageIO.read(new File("./sprites/wheat.png"));
		actorImgRight = ImageIO.read(new File("./sprites/actor_right.png"));
		actorImgUp = ImageIO.read(new File("./sprites/actor_up.png"));
		actorImgLeft = ImageIO.read(new File("./sprites/actor_left.png"));
		actorImgDown = ImageIO.read(new File("./sprites/actor_down.png"));
		wheatGrainImg = ImageIO.read(new File("./sprites/wheat_grain.png"));
		grassImg = ImageIO.read(new File("./sprites/grass.png"));
		waterImg = ImageIO.read(new File("./sprites/water.png"));
		meatImg = ImageIO.read(new File("./sprites/meat.png"));
		treeImg = ImageIO.read(new File("./sprites/tree.png"));
		woodImg = ImageIO.read(new File("./sprites/wood.png"));
		woodToolImg = ImageIO.read(new File("./sprites/wood_tool.png"));
		
		
		scaler = 2;
		
		initialize(environment);
		
		
		
		timer = new Timer(1000, new ActionListener() {
			private int timeSteps = 0;
            public void actionPerformed(ActionEvent evt) {
            	
            	long time = new Date().getTime();
            	
            	timer.setDelay(1000/(int)fpsSlider.getValue());
            	scaler = (int)scaleSlider.getValue();
            	
            	if (btnStep.isSelected()) {
            		if (btnShowWorld.isSelected()) {
            			canvas.revalidate();
            			paintAgain();
            		}
	            	environment.step(timeSteps);
	            	System.out.println("Timestep " + timeSteps + " completed. Time taken: " + Math.round((new Date().getTime() - time)*100f/1000f)/100f);
	            	timeSteps += 1;
            	}
            	
            	
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
				
				
				float xMultiplier = this.getWidth()/(float)environment.getWorldWidth();
				float yMultiplier = this.getHeight()/(float)environment.getWorldHeight();

				for (int x=0; x<environment.getWorldWidth(); x++) {
					for (int y=0; y<environment.getWorldHeight(); y++) {
						Entity newEntity = environment.getEntity(x, y);
						
						
						int startingY = this.getHeight()-(int)Math.round(y*yMultiplier) - (int)Math.round(yMultiplier);
						int startingX = (int)Math.round(x*xMultiplier);
						
						if (newEntity != null) {
							if (!newEntity.getIsVisible() && btnShowVisibleMask.isSelected()) {
								g.setColor(Color.red);
								g.fillRect(startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier));
								
							} else {
								g.drawImage(grassImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								if (newEntity instanceof Wheat) {
									g.drawImage(wheatImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								} else if (newEntity instanceof Actor) {
									if (((Actor) newEntity).getDirection() == 0) {
										g.drawImage(actorImgRight, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
									} else if (((Actor) newEntity).getDirection() == 1) {
										g.drawImage(actorImgUp, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
									} else if (((Actor) newEntity).getDirection() == 2) {
										g.drawImage(actorImgLeft, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
									} else if (((Actor) newEntity).getDirection() == 3) {
										g.drawImage(actorImgDown, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
									} else {
										System.out.println(((Actor) newEntity).getDirection());
									}
									
								} else if (newEntity instanceof WheatGrain) {
									g.drawImage(wheatGrainImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								} else if (newEntity instanceof Water) {
									g.drawImage(waterImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								} else if (newEntity instanceof Meat) {
									g.drawImage(meatImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								} else if (newEntity instanceof Tree) {
									g.drawImage(treeImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								} else if (newEntity instanceof Wood) {
									g.drawImage(woodImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								} else if (newEntity instanceof WoodTool) {
									g.drawImage(woodToolImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
								} else {
									System.out.println(newEntity);
								}
							}
						} else {
							g.drawImage(grassImg, startingX, startingY, (int)Math.round(xMultiplier), (int)Math.round(yMultiplier), this);
						}
						
					}
				}
				if (btnShowRays.isSelected()) {
					for (int x=0; x<environment.getWorldWidth(); x++) {
						for (int y=0; y<environment.getWorldHeight(); y++) {
							Entity newEntity = environment.getEntity(x, y);
							
							
							int startingY = this.getHeight()-(int)Math.round(y*yMultiplier) - (int)Math.round(yMultiplier);
							int startingX = (int)Math.round(x*xMultiplier);
							if (newEntity != null) {
								if (newEntity instanceof Actor) {
									Entity[] withinRange = ((Actor)(newEntity)).getRayCasts(env);
									//Arrays.sort(withinRange, (a,b));
									for(int ray=0; ray<withinRange.length;ray++) {
										
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
			}
		};
		canvas.setBackground(Color.WHITE);

		scrollPane.setViewportView(canvas);
		
		fpsSlider = new JSlider();
		fpsSlider.setMaximum(250);
		fpsSlider.setMinimum(1);
		fpsSlider.setValue(250);
		fpsSlider.setBounds(209, 12, 200, 16);
		frmSocialEvolutionSimulator.getContentPane().add(fpsSlider);
		
		scaleSlider = new JSlider();
		scaleSlider.setValue(1);
		scaleSlider.setMaximum(50);
		scaleSlider.setMinimum(1);
		scaleSlider.setBounds(486, 12, 200, 16);
		frmSocialEvolutionSimulator.getContentPane().add(scaleSlider);
		
		JLabel lblTime = new JLabel("FPS");
		lblTime.setBounds(147, 17, 70, 15);
		frmSocialEvolutionSimulator.getContentPane().add(lblTime);
		
		JLabel lblScale = new JLabel("Scale");
		lblScale.setBounds(430, 17, 70, 15);
		frmSocialEvolutionSimulator.getContentPane().add(lblScale);
		
		btnShowWorld = new JToggleButton("Show World");
		btnShowWorld.setBounds(701, 12, 126, 25);
		frmSocialEvolutionSimulator.getContentPane().add(btnShowWorld);
		
		btnShowVisibleMask = new JToggleButton("Show Visible Mask");
		btnShowVisibleMask.setBounds(839, 12, 170, 25);
		frmSocialEvolutionSimulator.getContentPane().add(btnShowVisibleMask);
		
		btnShowRays = new JToggleButton("Show Rays");
		btnShowRays.setBounds(1021, 12, 117, 25);
		frmSocialEvolutionSimulator.getContentPane().add(btnShowRays);
		scrollPane.getVerticalScrollBar().setUnitIncrement(25);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(25);
		
		
	}
}
