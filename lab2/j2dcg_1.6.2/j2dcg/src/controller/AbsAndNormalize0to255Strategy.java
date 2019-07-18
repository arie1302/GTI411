package controller;



import java.util.ArrayList;
import java.util.Collections;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

public class AbsAndNormalize0to255Strategy extends ImageConversionStrategy {
	ArrayList<Integer> tableB = new ArrayList<Integer>();
	ArrayList<Integer> tableR = new ArrayList<Integer>();
	ArrayList<Integer> tableG = new ArrayList<Integer>();

	
	
	@Override
	public ImageX convert(ImageDouble image) {
		// TODO Auto-generated method stub
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
		PixelDouble curPixelDouble = null;

		int position = 0; 

		newImage.beginPixelUpdate();
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				redTable(curPixelDouble.getRed());
				greenTable(curPixelDouble.getGreen());
				bleuTable(curPixelDouble.getBlue());
				
				position++; 
				
				newImage.setPixel(x, y, new Pixel((int)(absNorm0to255Red(curPixelDouble.getRed())),
						(int)(absNorm0to255Green(curPixelDouble.getGreen())),
						(int)(absNorm0to255Blue(curPixelDouble.getBlue())),						 
						(int)(absNorm0to255alpha(curPixelDouble.getAlpha(),curPixelDouble.getRed(),curPixelDouble.getGreen(),curPixelDouble.getBlue()))));
			}
		}
		newImage.endPixelUpdate();
		return newImage;
		

	}

	private double absNorm0to255alpha(double alpha, double red, double green, double blue) {
		return (Math.abs(alpha)/Math.abs(red) + Math.abs(blue) + Math.abs(green))*255.0;
	}

	private double absNorm0to255Blue(double blue) {
		//B=((B-Bmin)/(Bmax-Bmin))*255.0
		return ((blue - Collections.min(tableB))/(Collections.max(tableB)-Collections.min(tableB))*255.0);

	}

	private double absNorm0to255Green(double green) {
		//G=((G-Gmin)/(Gmax-Gmin))*255.0
		return ((green - Collections.min(tableG))/(Collections.max(tableG)-Collections.min(tableG))*255.0);
	}

	private double absNorm0to255Red(double red) {
		//B=((B-Bmin)/(Bmax-Bmin))*255.0
		return ((red - Collections.min(tableR))/(Collections.max(tableR)-Collections.min(tableR))*255.0);
	}
	
	private ArrayList<Integer> bleuTable (double d) {
			
		tableB.add((int)d);
				
		return tableB;
		
	}
	
	private ArrayList<Integer> redTable (double d){
		
		tableR.add((int)d);
		
		return tableR;
		
	}

	
	private ArrayList<Integer> greenTable (double d) {
		
		tableG.add((int)d);
		
		return tableG;
		
	}
}
