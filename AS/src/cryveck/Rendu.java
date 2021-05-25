package cryveck;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public class Rendu {
	
	private int pixelSize;
	private int maxSizeList;
	private Queue<GraphePreProcessImage> globalQueue = new ConcurrentLinkedQueue<GraphePreProcessImage>();
	
	private ColorProcess cp = (double param) -> param;//Par def., l'identite
	
	private double colorMax;
	private double colorMin;
	private double colorMaxProcess;
	private double colorMinProcess;
	
	public Rendu (int maxSizeList, int pixelSize) {
		this.maxSizeList = maxSizeList;
		this.pixelSize = pixelSize;
	}
	
	public Rendu (int maxSizeList, int pixelSize, ColorProcess cp) {
		this(maxSizeList, pixelSize);
		setColorProcess(cp);
	}
	
	public void setColorRange(double colorMax, double colorMin) {
		this.colorMax = colorMax;
		this.colorMin = colorMin;
		colorMaxProcess = cp.proccessValues(colorMax);
		colorMinProcess = cp.proccessValues(colorMin);
	}
	
	public void setColorProcess(ColorProcess cp) {
		this.cp = cp; 
		colorMaxProcess = cp.proccessValues(colorMax);
		colorMinProcess = cp.proccessValues(colorMin);
	}
	
	public synchronized void saveParallel (String nom, int[] tab, int width, int height, int maxColor) {
		globalQueue.add(new GraphePreProcessImage(nom, tab, width, height));
		if (globalQueue.size() >= maxSizeList)
			processList();
	}
	
	public void processList () {
		globalQueue.parallelStream().forEach(gppi -> processSave(gppi));
	}
	
	private void processSave (GraphePreProcessImage gppi) {
		globalQueue.remove(gppi);
		save(gppi.title, gppi.tab, gppi.width, gppi.height);
	}
	
	public void save (String nom, int[] tab, int width, int height) {
		File nomfichier = new File("/home/grothendieck/out/" + nom + ".png");
		BufferedImage bi = new BufferedImage(width*pixelSize, height*pixelSize, BufferedImage.TYPE_3BYTE_BGR);
		render(bi.getGraphics(), tab, pixelSize, width, height);
		try {
			ImageIO.write(bi, "PNG", nomfichier);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render(Graphics g, int[] tab, int size, int width, int height) {
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
//				g.setColor(getColorProcess(tab[i + j*width + 1], maxColor, 0, (double param) -> Math.pow(param, 0.5)));//(double param) -> Math.pow(param, 0.2) ; (double param) -> param renvoie id (i.e la fonction getColor)
				g.setColor(getColor(cp.proccessValues(tab[i + j*width + 1]), colorMaxProcess, colorMinProcess));
				g.fillRect(i*size, j*size, size, size);
			}
	}

	private static Color getColor(double value, double max, double min) {
		double map = (Math.max(Math.min(value, max) - min, 0)/(max - min)*255);
		
		
		double facteur = 255/85;
		if (map < 85) {
			return new Color(0, (int) (map*facteur), 255);
		} else if (map < 170) {
			return new Color((int) ((map-85)*facteur), 255, (int) (255 - (map-85)*facteur));
		} else {
			return new Color(255, (int) (255 - (map-170)*facteur), 0);
		}
	}
	
	public static void printMat(int[][] M) {
		for (int i = 0; i < M.length; i++) {
			System.out.println();
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(" " + M[i][j]);
			}
		}
	}
	
	public static void printMat(boolean[][] M) {
		for (int i = 0; i < M.length; i++) {
			System.out.println();
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(" " + M[i][j]);
			}
		}
	}
	
	public static void printMat(boolean[] M, int width) {
		for (int j = 0; j < M.length; j++) {
			System.out.print(" " + (M[j] ? 1 : 0));
			if (j % width == 0) System.out.println();
		}
		System.out.println();
	}
	

	public static void printMat(int[] M, int width) {
		for (int j = 0; j < M.length; j++) {
			System.out.print(" " + M[j]);
			if (j % width == 0) System.out.println();
		}
		System.out.println();
	}

	public static void printMat(int[] M) {
		for (int j = 0; j < M.length; j++) {
			System.out.print(" " + M[j]);
		}
		System.out.println();
	}
	
	public static void printMat(boolean[] M) {
		for (int j = 0; j < M.length; j++) {
			System.out.print(" " + M[j]);
		}
		System.out.println();
	}
	
	//Tests
	public static void main(String[] args) {
		Stream<Integer> infiniteStream = Stream.iterate(0, i -> i + 2);
		infiniteStream.parallel().limit(50).forEach(a -> System.out.println(a));
		System.out.println("a");
	}

	public double getColorMax() {
		return colorMax;
	}

	public double getColorMin() {
		return colorMin;
	}
	
}

interface ColorProcess {
	public abstract double proccessValues(double value);
}
