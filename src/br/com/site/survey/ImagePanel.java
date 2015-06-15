package br.com.site.survey;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Adapted JPanel for Canvas and User Interation
 * @author Lucas Corsi
 *
 */
public class ImagePanel extends JPanel implements Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image myImage;
	private Image heatMapLayer;
	private Point pointStart = null;
	private Point pointEnd   = null;
	private boolean drawPtStarted = false;
	private boolean drawAntPoint = false;
	private boolean drawWallActived = false;
	private boolean drawAntennaActived = false;
	private boolean drawHeatLayer = false;
	private ArrayList<Object> objects;
	private ArrayList<Object> redo;
	private JFrame parent;
	private JButton undoBtn, redoBtn, wallBtn, antennaBtn;
	private AntennaType atnType;
	private Point2D scale;
	private double betaParam;
	private double imgWidth;
	private double imgHeight;

	public Point2D getScale() {
		return scale;
	}

	public void setScale(Point2D scale) {
		this.scale = scale;
	}

	/**
	 * Default constructor
	 * @param parent is JFrame parent
	 * @param undoBt is Undo button
	 * @param redoBt is Redo button
	 * @param beta is beta Param from New Dialog
	 * @param width
	 * @param height
	 */
	public ImagePanel(JFrame parent, JButton undoBt, JButton redoBt, JButton wallBtn, JButton antennaBtn, double beta, double width, double height) {
		objects = new ArrayList<Object>();
		redo = new ArrayList<Object>();
		this.parent = parent;
		this.betaParam = beta;
		this.undoBtn = undoBt;
		this.redoBtn = redoBt;
		this.imgHeight = height;
		this.imgWidth = width;
		this.setAntennaBtn(antennaBtn);
		this.setWallBtn(wallBtn);
		this.setSize((int) this.getImgWidth(), (int) this.getImgHeight());
		this.setPreferredSize(new Dimension((int) this.getImgWidth(), (int)this.getImgHeight()));
		//key map
		InputMap im = this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = this.getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK), "TestPoint");
		am.put("TestPoint", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Point2D p1 = new Point2D.Double();
				Point p2 = getMousePosition();
				p1.setLocation(p2.getX(), p2.getY());
				Thread t = new PixelCalc(null, parent, (int) imgWidth, (int)imgHeight, scale, objects, p1);
				t.start();
				try {
					t.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		im.put(KeyStroke.getKeyStroke("ESCAPE"), "Escape");
		am.put("Escape", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(drawAntennaActived && (antennaBtn != null)) {
					antennaBtn.doClick();
				} else if(drawWallActived && (wallBtn != null)) {
					wallBtn.doClick();
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if(drawAntennaActived) {
					pointStart = e.getPoint();
					if(pointStart.getX() > imgWidth) {
						pointStart.setLocation((int)imgWidth, pointStart.getY());
					}
					if(pointStart.getY() > imgHeight) {
						pointStart.setLocation(pointStart.getX(), (int)imgHeight);
					}
				}
			}

			public void mouseDragged(MouseEvent e) {
				if(drawWallActived) {
					pointEnd = e.getPoint();
					if(pointEnd.getX() > imgWidth) {
						pointEnd.setLocation((int)imgWidth, pointEnd.getY());
					}
					if(pointEnd.getY() > imgHeight) {
						pointEnd.setLocation(pointEnd.getX(), (int)imgHeight);
					}
				}
				redraw();
			}
		});

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(drawAntennaActived) {
					pointStart = e.getPoint();
					if(pointStart.getX() > imgWidth) {
						pointStart.setLocation((int)imgWidth, pointStart.getY());
					}
					if(pointStart.getY() > imgHeight) {
						pointStart.setLocation(pointStart.getX(), (int)imgHeight);
					}
					drawAntPoint = true;
				}
				redraw();
			}

			public void mousePressed(MouseEvent e) {
				if(drawWallActived) {
					pointStart = e.getPoint();
					if(pointStart.getX() > imgWidth) {
						pointStart.setLocation((int)imgWidth, pointStart.getY());
					}
					if(pointStart.getY() > imgHeight) {
						pointStart.setLocation(pointStart.getX(), (int)imgHeight);
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if(drawWallActived) {
					pointEnd = e.getPoint();
					if(pointEnd.getX() > imgWidth) {
						pointEnd.setLocation((int)imgWidth, pointEnd.getY());
					}
					if(pointEnd.getY() > imgHeight) {
						pointEnd.setLocation(pointEnd.getX(), (int)imgHeight);
					}
					drawPtStarted = true;
				}
				redraw();
			}
		});
		new Thread(this).start();
	}

	/**
	 * clear image panel - new/reset
	 */
	public void newSchema() {
		myImage = null;
		drawAntennaActived = false;
		drawAntPoint = false;
		drawPtStarted = false;
		drawHeatLayer = false;
		drawWallActived = false;
		pointStart = null;
		pointEnd = null;
		this.objects.clear();
		this.redo.clear();
		this.scale = null;
		this.heatMapLayer = null;
		repaint();
	}

	public void activeHeatLayer() {
		class MeaningOfLifeFinder extends SwingWorker<Object, Object> {
			@Override
			public Void doInBackground() {
				ProgressMonitor monitor = ProgressUtil.createModalProgressMonitor(parent, 100, false); 
				PixelCalc t = new PixelCalc(monitor, parent, (int) imgWidth, (int) imgHeight,
						scale, objects, null);
				t.start();
				monitor.start("Processing...");
				try {
					t.join();
					if(monitor.getDiag() != null) monitor.getDiag().dispose();
					heatMapLayer = t.getHeatmap();
					drawHeatLayer = true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}
		(new MeaningOfLifeFinder()).execute();
	}

	public void disableHeatLayer() {
		this.drawHeatLayer = false;
		this.heatMapLayer = null;
	}

	public void activeDrawAntenna() {
		drawAntennaActived = true;
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR); 
		setCursor(cursor);
	}

	public void disableDrawAntenna() {
		drawAntennaActived = false;
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
		setCursor(cursor);
	}

	public void activeDrawWall() {
		drawWallActived = true;
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR); 
		setCursor(cursor);
	}

	public void disableDrawWall() {
		drawWallActived = false;
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
		setCursor(cursor);
	}

	public void undoWall() {
		if(objects.size() > 0) {
			int index = objects.size() - 1;
			redo.add(objects.get(index));
			objects.remove(index);
		}
		redraw();
	}

	public void redraw() {
		revalidate();
		parent.pack();
		setBackground(Color.WHITE);
		repaint();

	}

	public void redoWall() {
		if(redo.size() > 0) {
			int index = redo.size() - 1;
			objects.add(redo.get(index));
			redo.remove(index);
		}
		redraw();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) this.getImgWidth(), (int) this.getImgHeight());
	}

	@Override
	public void setSize(Dimension d) {
		// TODO Auto-generated method stub
		super.setSize(new Dimension((int) this.getImgWidth(), (int) this.getImgHeight()));
	}

	public void setAddAntennaType(AntennaType antennaType) {
		this.atnType = antennaType;
	}

	public void paint( Graphics g ) {
		//force repaint of graphic components
		super.paintComponents(g);
		//paint all with gray (background color)
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, (int) this.getWidth(), (int) this.getHeight());	
		if(objects != null && objects.size() > 0) {
			//enable undo button
			this.undoBtn.setEnabled(true);
		} else {
			this.undoBtn.setEnabled(false);
		}
		if(redo != null && redo.size() > 0) {
			this.redoBtn.setEnabled(true);
		} else {
			this.redoBtn.setEnabled(false);
		}
		//paint image dimensions with white color
		g.setColor(Color.white);
		g.fillRect(0, 0, (int) this.getImgWidth(), (int) this.getImgHeight());
		//if image loaded, paint this image
		if(myImage != null) {
			g.drawImage( myImage, 0, 0, this );
		}
		//redraw all wall lines and antennas
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < objects.size(); i++) {
			Object obj = objects.get(i);
			if(obj instanceof Wall) {
				Wall wall = (Wall) obj;
				g2.setColor(Color.red);
				g2.setStroke(new BasicStroke(4));
				g2.drawLine(wall.getStart().x, wall.getStart().y, wall.getEnd().x, wall.getEnd().y);
			} else if(obj instanceof Antenna) {
				Antenna antenna = (Antenna) obj;
				try {
					//load resources symbologies
					Image img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/antenna-2-32_32x32.png"));
					if(antenna.getAntennaType() == AntennaType.OMNIDIRECTIONAL) {
						img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/tag_icon_antenna.png"));
					}
					int px = antenna.getPoint().x - (img.getWidth(null) / 2);
					int py = antenna.getPoint().y - (img.getHeight(null) / 2);
					if(antenna.getAntennaType() == AntennaType.BIDIRECTIONAL) {
						img = ImageTool.rotate(img, antenna.getAngle());
					}
					g2.drawImage(img, px, py, img.getWidth(null), img.getHeight(null), null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//if heatMap generated and active, draw image with 50% of opacity
		if(drawHeatLayer && heatMapLayer != null) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2.drawImage(this.heatMapLayer, 0, 0, this.heatMapLayer.getWidth(null), this.heatMapLayer.getHeight(null), null);
		}
		if(drawAntennaActived) {
			//redraw temp point
			if (pointStart != null) {
				try {
					Image img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/antenna-2-32_32x32.png"));
					if(this.atnType == AntennaType.OMNIDIRECTIONAL) {
						img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/tag_icon_antenna.png"));
					}
					int px = pointStart.x - (img.getWidth(null) / 2);
					int py = pointStart.y - (img.getHeight(null) / 2);
					g2.drawImage(img, px, py, img.getWidth(null), img.getHeight(null), null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(drawAntPoint) {
				drawAntPoint = false;
				Antenna antenna = new Antenna();
				antenna.setPoint(pointStart);
				antenna.setAntennaType(atnType);
				antenna.setAngle(90);
				AntennaInfo diag = new AntennaInfo(parent);
				if(diag.getFreqMhz() > 0 /*&& diag.getPtxPowerVal() > 0*/) {
					antenna.setFreqMhz(diag.getFreqMhz());
					//antenna.setPtxPower(diag.getPtxPowerVal());
					objects.add(antenna);
				}
				diag.dispose();
				pointStart = null;
				redraw();
			}
		}
		if(drawWallActived) {
			// redraw temp line
			if (pointStart != null && pointEnd != null) {
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(4));
				g2.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
			}
			if(drawPtStarted) {
				drawPtStarted = false;
				Wall wall = new Wall();
				wall.setStart(pointStart);
				wall.setEnd(pointEnd);
				try {
					double wall_c = Point2D.distance(pointStart.getX()*this.getScale().getX(),
							pointStart.getY()*this.getScale().getY(),
							pointEnd.getX()*this.getScale().getX(),
							pointEnd.getY()*this.getScale().getY());
					String wall_cStr = String.format("%.2f", wall_c);
					WallInfo dialog = new WallInfo(parent, wall_cStr.replace('.', ','));
					if(dialog.getLength() > 0 && dialog.getmType() != null) {
						wall.setLength(dialog.getLength());
						wall.setMaterialType(dialog.getmType());
						objects.add(wall);
					}
					dialog.dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
				pointEnd = null;
				pointStart = null;
				redraw();
			}
		}
	}

	public Image getImage() {
		return( myImage );
	}

	public void setImage( Image i ) {
		myImage = i;
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		// TODO Auto-generated method stub
		Dimension aux = new Dimension((int)this.getImgHeight(), (int)this.getImgHeight());
		super.setPreferredSize(aux);
	}

	@Override
	public void run() {
		// syncs screen frequency 100 frames per sec (1000/100 = 10)
		//this thread forces repaint panel
		while(true)
			try {
				Thread.sleep(10);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ImagePanel.this.repaint();
					}
				});
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
	}

	public double getBetaParam() {
		return betaParam;
	}

	public void setBetaParam(double betaParam) {
		this.betaParam = betaParam;
	}

	public double getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(double imgWidth) {
		this.imgWidth = imgWidth;
	}

	public double getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(double imgHeight) {
		this.imgHeight = imgHeight;
	}

	public JButton getWallBtn() {
		return wallBtn;
	}

	public void setWallBtn(JButton wallBtn) {
		this.wallBtn = wallBtn;
	}

	public JButton getAntennaBtn() {
		return antennaBtn;
	}

	public void setAntennaBtn(JButton antennaBtn) {
		this.antennaBtn = antennaBtn;
	}
}