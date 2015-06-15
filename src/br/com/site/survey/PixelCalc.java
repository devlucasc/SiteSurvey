package br.com.site.survey;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.apache.commons.math3.distribution.NormalDistribution;

public class PixelCalc extends Thread {

	private int imgWidth;
	private int imgHeight;
	private double betaParam;
	private ArrayList<Object> objects;
	private Point2D scale;
	private Image heatmap;
	private Point2D testPoint;
	private ProgressMonitor monitor;
	private JFrame parent;
	private int j;
	private int i;

	/**
	 * Generate a gradient color sequence
	 * @param i is number of step
	 * @param steps is number of total steps or number of total gradient colors
	 * @return gererated color
	 */
	@SuppressWarnings("unused")
	private Color getGradientColor(int i, int steps) {
		Color color1 = Color.RED;
		Color color2 = Color.BLUE;
		float ratio = (float) i / (float) steps;
		int red = (int) (color2.getRed() * ratio + color1.getRed() * (1 - ratio));
		int green = (int) (color2.getGreen() * ratio + color1.getGreen() * (1 - ratio));
		int blue = (int) (color2.getBlue() * ratio + color1.getBlue() * (1 - ratio));
		Color stepColor = new Color(red, green, blue);
		return stepColor;
	}


	/**
	 * Calculate path loss pixel by pixel
	 * @return Image with pixel path loss layer
	 */
	private Image calculatePixelIteration() {
		//showProgress(0, (int)(this.getImgWidth()*this.getImgHeight()));
		System.out.println("Processando...");
		Image hResult = null;
		double pathLoss = 0.0f;
		//set test point (Keyboard shortcut method, calculate and show results in input point)
		int startXPixel = (testPoint != null) ? (int)testPoint.getX() : 0;
		int startYPixel = (testPoint != null) ? (int)testPoint.getY() : 0;
		int endXPixel = (startXPixel != 0) ? startXPixel + 1 : imgWidth;
		int endYPixel = (startYPixel != 0) ? startYPixel + 1: imgHeight;
		int deltaProgress = (endXPixel - startXPixel)*(endYPixel - startYPixel);
		//set position to progress bar dialog
		int curPosition = 1;
		if(scale == null) {
			return hResult;
		}
		//set maximal and minimal values of path loss found
		Double maxPathLoss = null, minPathLoss = null;
		//create matrix of path loss values
		double[][] matrixLoss = new double[imgWidth][imgHeight];
		//iterate pixel by pixel in (x, y)
		for (i = startXPixel; i < endXPixel; i++) {
			for (j = startYPixel; j < endYPixel; j++) {
				//generate pixel with real meters scale
				//point with real scale
				Point2D pixel = new Point2D.Double(i*scale.getX(), j*scale.getY());
				double aux = 0.0f;
				boolean isFirst = true;
				//iteration for all objects
				for (int k = 0; k < objects.size(); k++) {
					Object obj = objects.get(k);
					//check if object is an antenna
					if(obj instanceof Antenna) {
						Antenna antenna = (Antenna) obj;
						//check distance between antenna and pixel
						double distance = pixel.distance(antenna.getPoint().getX()*scale.getX(),
								antenna.getPoint().getY()*scale.getY());
						if(testPoint != null) {
							System.out.println(String.format("Test point [%d][%d]: %.3f meters [Antenna %d]", i, j, distance, k));
						}
						//calculate initial path loss (aux)
						aux = 20*Math.log10(1.0f) + 20*Math.log10(antenna.getFreqMhz()) - 27.55f; // free space path loss at 1 meter
						//generate normal Gaussian Distribution
						NormalDistribution normalDist = new NormalDistribution();
						//calculate path loss with distance
						aux += 10*betaParam*Math.log10(distance) + normalDist.sample(); // shadowning at distance + Normal distribution
						int countWallObstruction = 0;
						//iterate all objects
						for (int l = 0; l < objects.size(); l++) {
							Object obj2 = objects.get(l);
							//check if object is antenna
							if(obj2 instanceof Wall) {
								//check line intersection
								Wall wall = (Wall) obj2;
								boolean inter =
										Line2D.linesIntersect(i, j,
												antenna.getPoint().getX(), antenna.getPoint().getY(),
												wall.getStart().getX(), wall.getStart().getY(),
												wall.getEnd().getX(), wall.getEnd().getY());
								if(inter) {
									//if intersect
									countWallObstruction++;
									double beforeAux = aux;
									//sum with Wall Attenuation Factor
									aux += wall.getMaterialType().getWAF();
									if(testPoint != null) {
										System.out.println(String.format("Test point [%d][%d]: %.3f - Aux before [Antenna %d]", i, j, beforeAux, k));
										System.out.println(String.format("Test point [%d][%d]: %.3f - Aux after [Antenna %d]", i, j, aux, k));
									}
								}
							}
						}
						if(testPoint != null) {
							System.out.println(String.format("Test point [%d][%d]: %d obstructions found [Antenna %d]", i, j, countWallObstruction, k));
						}
						//if first set path loss
						if(isFirst) {
							pathLoss = aux;
							isFirst = false;
						} else {
							// check for the best path loss
							if(aux < pathLoss) {
								pathLoss = aux;
							}
						}
						if(testPoint != null) {
							System.out.println(String.format("Test point[%d][%d]: %.3f [Antenna %d]", i, j, pathLoss, k));
						}
					}
				}
				//check for minimal path loss
				if(minPathLoss == null) {
					minPathLoss = new Double(pathLoss);
				} else {
					if(Double.isFinite(pathLoss) && pathLoss < minPathLoss.doubleValue()) {
						minPathLoss = pathLoss;
					}
				}
				//check for maximal path loss
				if(maxPathLoss == null) {
					maxPathLoss = new Double(pathLoss);
				} else {
					if(Double.isFinite(pathLoss) && pathLoss > maxPathLoss.doubleValue()) {
						maxPathLoss = pathLoss;
					}
				}
				//set path loss in matrix
				matrixLoss[i][j] = pathLoss;
				//calculate process percent for progress dialog
				double processPercent = (100*curPosition/deltaProgress);
				String status = String.format("Processing.. %.2f %% completed.", processPercent);
				if(monitor != null) {
					monitor.setCurrent(status, (int)processPercent);
				}
				curPosition++;
			}
		}
		//math for 1 unit of max
		double factor = ((maxPathLoss - minPathLoss)/100);
		//set scales (10, 20, 30%....)
		double scales[] = {
				minPathLoss + factor*10,
				minPathLoss + factor*20,
				minPathLoss + factor*30,
				minPathLoss + factor*40,
				minPathLoss + factor*50,
				minPathLoss + factor*60,
				minPathLoss + factor*70,
				minPathLoss + factor*80,
				minPathLoss + factor*90,
				minPathLoss + factor*100};
		//set scale colors for each scale
		Color scaleColors[] = {
				Color.BLUE,
				Color.GREEN,
				Color.ORANGE,
				Color.YELLOW,
				Color.MAGENTA,
				Color.PINK,
				Color.RED,
				Color.LIGHT_GRAY,
				Color.GRAY,
				Color.DARK_GRAY,
				Color.WHITE
		};
		//draw heatmap layer
		if(testPoint == null) {
			//get empty image
			BufferedImage heatmap = ImageTool.toBufferedImage(
					ImageTool.getEmptyImage(imgWidth, imgHeight));
			//colorate pixel by pixel calculated by scales
			for (int k = 0; k < matrixLoss.length; k++) {
				for (int k2 = 0; k2 < matrixLoss[k].length; k2++) {
					Color c = null;
					double aux = matrixLoss[k][k2];
					if(Double.isFinite(aux)) {
						for (int hc = 0; hc < scales.length; hc++) {
							if((hc == 0) && (aux < scales[hc])) {
								//if lower than first scale
								c = scaleColors[hc];
								break;
							} else if (hc != 0) {
								if((aux >= scales[hc - 1]) && (aux <= scales[hc])) {
									//if beetween scale and next scale
									c = scaleColors[hc];
									break;
								} else if ((hc == scales.length - 1) && (aux > scales[hc])) {
									//if greater than last scale
									c = scaleColors[scaleColors.length-1];
									break;
								}
							}
						}
						
						if(c != null) {
							//colorate the pixel with rgb color
							heatmap.setRGB(k, k2, c.getRGB());
						}
					}
				}
			}
			//convert bufferedImage to Image
			hResult = ImageTool.toImage(heatmap);
		}
		System.out.println(String.format("Fim do processamento. Min %.3f - Max: %.3f", minPathLoss, maxPathLoss));
		return hResult;
	}

	/**
	 * Default constructor
	 * @param m is progress dialog monitor
	 * @param j is parent (ImagePanel)
	 * @param imgWidth
	 * @param imgHeight
	 * @param scale
	 * @param objects
	 * @param testpoint
	 */
	public PixelCalc(ProgressMonitor m, JFrame j, int imgWidth, int imgHeight, Point2D scale, ArrayList<Object> objects, Point2D testpoint) {
		this.imgHeight = imgHeight;
		this.imgWidth = imgWidth;
		this.scale = scale;
		this.objects = objects;
		this.testPoint = testpoint;
		this.setParent(j);
		this.monitor = m;
	}

	@Override
	/**
	 * 
	 */
	public synchronized void run() {
		this.heatmap = calculatePixelIteration();
		if(monitor != null) {
			if(monitor.getCurrent()!=monitor.getTotal()) 
				monitor.setCurrent(null, monitor.getTotal());
			if(monitor.getDiag() != null) {
				monitor.getDiag().dispose();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public Image getHeatmap() {
		return heatmap;
	}

	/**
	 * 
	 * @return
	 */
	public JFrame getParent() {
		return parent;
	}

	/**
	 * 
	 * @param parent
	 */
	public void setParent(JFrame parent) {
		this.parent = parent;
	}

}
