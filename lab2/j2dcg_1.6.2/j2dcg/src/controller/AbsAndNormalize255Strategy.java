package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

public class AbsAndNormalize255Strategy extends ImageConversionStrategy {

	@Override
	public ImageX convert(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
		PixelDouble curPixelDouble = null;

		newImage.beginPixelUpdate();
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				newImage.setPixel(x, y, new Pixel((int)(absNorm255(curPixelDouble.getRed(),curPixelDouble.getRed(),curPixelDouble.getGreen(),curPixelDouble.getBlue())),
						(int)(absNorm255(curPixelDouble.getGreen(),curPixelDouble.getRed(),curPixelDouble.getGreen(),curPixelDouble.getBlue())),
						(int)(absNorm255(curPixelDouble.getBlue(),curPixelDouble.getRed(),curPixelDouble.getGreen(),curPixelDouble.getBlue())),						 
						(int)(absNorm255(curPixelDouble.getAlpha(),curPixelDouble.getRed(),curPixelDouble.getGreen(),curPixelDouble.getBlue()))));
			}
		}
		newImage.endPixelUpdate();
		return newImage;
		
		
		
		// TODO Auto-generated method stub
	}
	
	
	private double absNorm255(double color, double red, double green, double blue) {
		
		return (Math.abs(color)/Math.abs(red) + Math.abs(blue) + Math.abs(green))*255.0;

	}

}
