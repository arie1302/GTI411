package view;

import java.awt.image.BufferedImage;
import model.ObserverIF;
import model.Pixel;

public class HSVColorMediator extends Object implements SliderObserver, ObserverIF{
	ColorSlider hueCS;
	ColorSlider saturationCS;
	ColorSlider valueCS;

	int red;
	int green;
	int blue;
	
	float hue;
	float saturation ;
	float value;
	
	BufferedImage hueImage;
	BufferedImage SaturationImage;
	BufferedImage valueImage;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		this.red = result.getPixel().getRed();
	    this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();
		
		this.result = result;
		result.addObserver(this);
		
		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		SaturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);	
		
		computeHueImage(red, green, blue);
		computeSaturationImage(red, green, blue);
		computeValueImage(red, green, blue); 
	}
	
	private int[] hsvToRgb(float resultPixelHue, float resultPixelSaturation, float resultPixelValue) {
		float primeR = 0;
		float primeG = 0;
		float primeB = 0;
		float h = (float)(resultPixelHue/255.0);
		float s = (float)(resultPixelSaturation/255.0);
		float v = (float)(resultPixelValue/255.0);
		float c;
		float x;
		float m;
		
		int[] rgbResult = new int [3] ;
		
		c = resultPixelValue * resultPixelSaturation; 
		x = c * (1-Math.abs((resultPixelHue/60) % 2-1));
		m = resultPixelValue - c;
		
		if(resultPixelHue/255.00 < 60 &&  resultPixelHue/255.0 >= 0) {
			primeR = c;
			primeG = x;
			primeB = 0;
		}else if(resultPixelHue < 120 &&  resultPixelHue >= 60) {
			primeR = x;
			primeG = c;
			primeB = 0;
		}else if(resultPixelHue < 180 &&  resultPixelHue >= 120) {
			primeR = 0;
			primeG = c;
			primeB = x;
		}else if(resultPixelHue < 240 &&  resultPixelHue >= 180) {
			primeR = 0;
			primeG = x;
			primeB = c;
		}else if(resultPixelHue < 300 &&  resultPixelHue >= 240) {
			primeR = x;
			primeG = 0;
			primeB = c;
		}else if(resultPixelHue < 360 &&  resultPixelHue >= 300) {
			primeR = c;
			primeG = 0;
			primeB = x;
		}
		
		rgbResult[0]= (int) ((primeR + m)*255);
		rgbResult[1]= (int) ((primeG + m)*255);
		rgbResult[2]= (int) ((primeB + m)*255);
		
		return rgbResult;
		
	}
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		
		boolean updateHue = false;
		boolean updateSaturation = false;
		boolean updateValue = false;

		float hue = computeHue (red, green, blue);
		float saturation = computeSaturation (red, green, blue);
		float value	= computeValue (red, green, blue);
		
		if (s == hueCS && v != hue) {
			hue = v;
			System.out.println("hue inside update : " + hue);
			updateSaturation = true;
			updateValue = true;
		}
		if (s == saturationCS && v != saturation) {
			saturation = v;
			System.out.println("saturation inside update : " + saturation);
			updateHue = true;
			updateValue = true;
		}
		if (s == valueCS && v != value) {
			value = v;
			System.out.println("value inside update : " + value);
			updateHue = true;
			updateSaturation = true;
		}
		
		int[] newRGB = hsvToRgb(hue, saturation, value); 
		
		if (updateHue) {
			computeHueImage((int)(newRGB[0]), (int)(newRGB[1]),(int)(newRGB[2]));
		}
		if (updateSaturation) {
			computeSaturationImage((int)(newRGB[0]), (int)(newRGB[1]),(int)(newRGB[2]));
		}
		if (updateValue) {
			computeValueImage((int)(newRGB[0]), (int)(newRGB[1]),(int)(newRGB[2]));
		}		
		
		Pixel pixel = new Pixel((int)(newRGB[0]), (int)(newRGB[1]),(int)(newRGB[2]), 255);
		result.setPixel(pixel);
	}
	
	public void computeHueImage(int red, int green, int blue)  { 

		Pixel pixel = new Pixel(red, green, blue,255);
		float s =0; 
		float v =0;
		
		s = computeSaturation(red, green, blue);
		v = computeValue(red,green,blue);
		
		for (int i = 0; i<imagesWidth; ++i) {
			int[] newRGB = hsvToRgb((float)(((double)i / (double)imagesWidth)*255.0), s, v); 
			System.out.println("compute hue, red : " + newRGB[0]);
			pixel.setRed(newRGB[0]);
			pixel.setGreen(newRGB[1]);
			pixel.setBlue(newRGB[2]);
			
			int rgb = pixel.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (hueCS != null) {
			hueCS.update(hueImage);
		}
	}
	
	public void computeSaturationImage(float hue, float saturation, float value)  {

		Pixel pixel = new Pixel(red, green, blue,255);
		float h =0;
		float v =0;
		
		h = computeHue(red, green, blue);
		v = computeValue(red,green,blue);
		
		for (int i = 0; i<imagesWidth; ++i) {
			int[] newRGB = hsvToRgb(h, (float)(((double)i / (double)imagesWidth)*255.0), v); 
			pixel.setRed(newRGB[0]);
			pixel.setGreen(newRGB[1]);
			pixel.setBlue(newRGB[2]);
			
			int rgb = pixel.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (saturationCS != null) {
			saturationCS.update(SaturationImage);
		}
	}
	
	public void computeValueImage(float hue, float saturation, float value) { 
		Pixel pixel = new Pixel(red, green, blue,255);
		float h =0;
		float s =0; 
		
		h = computeHue(red, green, blue);
		s = computeSaturation(red,green,blue);
 
		for (int i = 0; i<imagesWidth; ++i) {
			int[] newRGB = hsvToRgb(h, s, (float)(((double)i / (double)imagesWidth)*255.0)); 
			pixel.setRed(newRGB[0]);
			pixel.setGreen(newRGB[1]);
			pixel.setBlue(newRGB[2]);
			
			int rgb = pixel.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (valueCS != null) {
			valueCS.update(valueImage);
		}
	}
	
	public float computeSaturation(int red, int green, int blue){
		
		float primeR = (float)(red/255.0);
		float primeG = (float) (green/255.0);
		float primeB = (float) (blue/255.0);
		float saturation =0;
		float Cmax =0;
		float Cmin =0;
		float Delta =0;
		
		Cmin = Math.min( Math.min(primeR, primeG), Math.min(primeG, primeB ));
		Cmax = Math.max( Math.max(primeR, primeG), Math.max(primeG, primeB));
		Delta = Cmax - Cmin;

		saturation= (int)(Delta / Cmax * 255.0);
		
		return saturation;
	}
	
	public float computeValue(int red, int green, int blue){
			
			float primeR = (float)(red/255.0);
			float primeG = (float) (green/255.0);
			float primeB = (float) (blue/255.0);
			float value =0;
			float Cmax =0;
			
			Cmax = Math.max( Math.max(primeR, primeG), Math.max(primeG, primeB));
			value = Cmax*255;
			
			return (int)value;
		}
	
	public float computeHue(int red, int green, int blue){
		float primeR = (float)(red/255.0);
		float primeG = (float) (green/255.0);
		float primeB = (float) (blue/255.0);
		float hue = 0;
		float Cmax =0;
		float Cmin =0;
		float Delta =0; 
		float r =0;
		float g =0;
		float b =0;
		
		Cmax= Math.max(primeR, Math.max(primeG, primeB));
		Cmin= Math.min(primeR, Math.min(primeG, primeB));
		Delta = Cmax - Cmin;
		
		if (Delta == 0) {
			r =0;
			g =0;
			b =0;
		}else{
			r = (float) ((((Cmax-primeR)/6) + (Delta*0.5))/Delta);
			g = (float) ((((Cmax-primeG)/6) + (Delta*0.5))/Delta);
			b = (float) ((((Cmax-primeB)/6) + (Delta*0.5))/Delta); 
		}
		
		if (primeR == Cmax )		hue = b - g;
		if (primeG == Cmax )		hue = (1 + 3*r - 3*b)/3;
		if (primeB == Cmax )		hue = (2 + 3*g - 3*r)/3;
		
		if ( hue < 0 ) hue += 1;
		if ( hue > 1 ) hue -= 1;
		
		return hue*255;
	}

	/**
	 * @return
	 */
	public BufferedImage getHueImage() {
		return hueImage;
	}
	
	public BufferedImage getSaturationImage() {
		return SaturationImage;
	}

	public BufferedImage getValueImage() {
		return valueImage;
	}

	/**
	 * @param slider
	 */
	public void setHueCS(ColorSlider slider) {
		hueCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setSaturationCS(ColorSlider slider) {
		saturationCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setValueCS(ColorSlider slider) {
		valueCS = slider;
		slider.addObserver(this);
	}
	
	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		
		Pixel currentColor = new Pixel(red, green, blue, 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();
						
		hueCS.setValue((int) computeHue(red,green,blue));
		saturationCS.setValue((int)computeSaturation(red,green,blue));
		valueCS.setValue((int)computeValue(red,green,blue));
		
		computeHueImage(red, green, blue);
		computeSaturationImage(red, green, blue);
		computeValueImage(red, green, blue);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

