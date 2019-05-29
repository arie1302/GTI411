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
	float saturation;
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
		
		//calculer les valeurs du HSV
		//float[] HSV = rgbToHSV(red, green, blue);
	  //  hue = HSV[0];
		//saturation = HSV[1];
		//value = HSV[2];

		
		this.result = result;
		result.addObserver(this);
		
		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		SaturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		computeHueImage(red, green, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value); 	
	}
	
	/**
	 * Méthode pour calculer la conversion de RGB à HSV
	 */
	private float[] rgbToHSV(int resultPixelRed, int resultPixelgreen, int resultPixelBlue) {
		
		float primeR = resultPixelRed/255;
		float primeG = resultPixelgreen/255;
		float primeB = resultPixelBlue/255;
		float hue = 0;
		float saturation =0;
		float Cmax =0;
		float Cmin =0;
		float Delta =0; 

		float[] hsvResult = new float [3] ;
		
		Cmax= Math.max(primeR, Math.max(primeG, primeB));
		Cmin= Math.min(primeR, Math.min(primeG, primeB));
		Delta = Cmax - Cmin;
		/**
		if (Delta == 0) {
			hue = 0;
		}else if (Cmax == primeR) {
			hue = 60*((((primeG-primeB)/Delta))%6); 
		}else if (Cmax == primeG) {
			hue = 60*((((primeB-primeR)/Delta))+2); 
		}else if (Cmax == primeB) {
			hue = 60*((((primeR-primeG)/Delta))+4); 
		}
		
		if (Cmax != 0) {
			saturation = Delta/Cmax;
		}else if (Cmax == 0) {
			saturation = 0; 
		}**/
		
		if (primeR == Cmax && primeG == Cmin)
			hue = 5 + (primeR - primeB) / (primeR - primeG);
		else if (primeR == Cmax && primeB == Cmin)
			hue = 1 - (primeR - primeG) / (primeR - primeB);
		else if (primeG == Cmax && primeB == Cmin)
			hue = 1 + (primeG - primeR) / (primeG - primeB);
		else if (primeG == Cmax && primeR == Cmin)
			hue = 3 - (primeG - primeB) / (primeG - primeR);
		else if (primeB == Cmax && primeR == Cmin)
			hue = 3 + (primeB - primeG) / (primeB - primeR);
		else if (primeB == Cmax && primeG == Cmin)
			hue = 5 - (primeB - primeR) / (primeB - primeG);
		
		hue = hue * 60;
		
		if (hue < 0)
			hue += 360;
		
		saturation = (Cmax - Cmin) / Cmax;
		hsvResult[0] = hue;
		hsvResult[1] = saturation;
		hsvResult[2] = Cmax;
		
		return hsvResult; 
	}
	
	private float[] hsvToRgb(float resultPixelHue, float resultPixelSaturation, float resultPixelValue) {
		
		float primeR = 0;
		float primeG = 0;
		float primeB = 0;
		float c = 0;
		float x =0;
		float m =0;
		
		float[] rgbResult = new float [3] ;
		
		c = resultPixelValue * resultPixelSaturation; 
		x = c * (1-Math.abs((resultPixelHue/60) % 2-1));
		m = resultPixelValue - c;
		
		if(resultPixelHue < 60 &&  resultPixelHue >= 0) {
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
		
		rgbResult[0]= ((primeR + m)*255);
		rgbResult[1]= ((primeG + m)*255);
		rgbResult[2]= ((primeB + m)*255);
		
		return rgbResult;
	}
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		float [] newHSV;
		
		boolean updateHue = false;
		boolean updateSaturation = false;
		boolean updateValue = false;

		if (s == hueCS && v != hue) {
			hue = (float) ((v/255)*360.0);
			updateSaturation = true;
			updateValue = true;
		}
		if (s == saturationCS && v != saturation) {
			saturation = (float)(v/360);
			updateHue = true;
			updateValue = true;
		}
		if (s == valueCS && v != value) {
			value = (float)(v/360);
			updateHue = true;
			updateSaturation = true;
		}
		if (updateHue) {
			computeHueImage(hue, saturation, value);
		}
		if (updateSaturation) {
			computeSaturationImage(hue, saturation, value);
		}
		if (updateValue) {
			computeValueImage(hue, saturation, value);
		}		
		
		newHSV = hsvToRgb(hue, saturation, value); 
		
		Pixel pixel = new Pixel((int)(newHSV[0]), (int)(newHSV[1]),(int)(newHSV[2]), 255);
		result.setPixel(pixel);
	}
	
	public void computeHueImage(float hue, float saturation, float value)  { 
		
		
		float[] newHSV = hsvToRgb(hue, saturation, value);
		
		int rCalculated = (int)newHSV[0];
		int gCalculated = (int)newHSV[1];
		int bCalculated = (int)newHSV[2];
		
		Pixel p = new Pixel(rCalculated, gCalculated, bCalculated, 255); 
		
		for (int i = 0; i<imagesWidth; ++i) {
			//ajouter transofmration 255
			newHSV = hsvToRgb(hue, saturation, value);
			p.setRed(rCalculated); 
			p.setGreen(gCalculated); 
			p.setBlue(bCalculated); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (hueCS != null) {
			hueCS.update(hueImage);
		}
	}
	
	public void computeSaturationImage(float hue, float saturation, float value)  {
		
		float[] newHSV = hsvToRgb(hue, saturation, value);
		
		int rCalculated = (int)newHSV[0];
		int gCalculated = (int)newHSV[1];
		int bCalculated = (int)newHSV[2];
		
		Pixel p = new Pixel(rCalculated, gCalculated, bCalculated, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(rCalculated); 
			p.setGreen(gCalculated); 
			p.setBlue(bCalculated); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				SaturationImage.setRGB(i, j, rgb);
			}
		}
		if (saturationCS != null) {
			saturationCS.update(SaturationImage);
		}
	}
	
	public void computeValueImage(float hue, float saturation, float value) { 
		float[] newHSV = hsvToRgb(hue, saturation, value);
		
		int rCalculated = (int)newHSV[0];
		int gCalculated = (int)newHSV[1];
		int bCalculated = (int)newHSV[2];
		
		Pixel p = new Pixel(rCalculated, gCalculated, bCalculated, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(rCalculated); 
			p.setGreen(gCalculated); 
			p.setBlue(bCalculated);
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				valueImage.setRGB(i, j, rgb);
			}
		}
		if (valueCS != null) {
			valueCS.update(valueImage);
		}
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
	
	/**
	 * @return
	 */
	public double getBlue() {
		return blue;
	}

	/**
	 * @return
	 */
	public double getGreen() {
		return green;
	}

	public float getHue() {
		return hue;
	}

	public float getSaturation() {
		return saturation;
	}
	
	public float getValue() {
		return value;
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
		
		//calculer les valeurs du CYMK
		float[] HSV = rgbToHSV(red, green, blue);
		
		hue = HSV[0];
		saturation = HSV[1];
		value = HSV[2];
		
		
		hueCS.setValue((int)(hue));
		saturationCS.setValue((int)(saturation));
		valueCS.setValue((int)(value));
		
		computeHueImage(hue, saturation, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

