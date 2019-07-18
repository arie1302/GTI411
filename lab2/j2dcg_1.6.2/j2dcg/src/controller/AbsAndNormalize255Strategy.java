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
		
		//max value for RGBA https://stackoverflow.com/questions/19671453/how-do-i-get-the-max-and-min-values-from-a-set-of-numbers-entered
		PixelDouble max = new PixelDouble();

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				if (Math.abs(curPixelDouble.getRed()) > max.getRed() && Math.abs(curPixelDouble.getGreen()) > max.getGreen()
					&& Math.abs(curPixelDouble.getBlue()) > max.getBlue()) {
							max.setRed(curPixelDouble.getRed());
							max.setGreen(curPixelDouble.getGreen());
							max.setBlue(curPixelDouble.getBlue());
							max.setAlpha(curPixelDouble.getAlpha());
				}
			}
		}
		
		newImage.beginPixelUpdate();
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				newImage.setPixel(x, y, new Pixel(((int)norm255(Math.abs(curPixelDouble.getRed()), Math.abs(max.getRed()))),
												  ((int)norm255(Math.abs(curPixelDouble.getGreen()), Math.abs(max.getBlue()))),
												  ((int)norm255(Math.abs(curPixelDouble.getBlue()), Math.abs(max.getGreen()))),
												  ((int)norm255(Math.abs(curPixelDouble.getAlpha()), Math.abs(max.getAlpha())))));
			}
		}
		newImage.endPixelUpdate();
		return newImage;
	}
	
	private double norm255(double color, double maxColor) {
		return (255.0 * color / maxColor);

	}

}