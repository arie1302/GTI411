/**
 * 
 */
/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider hueCS;
	ColorSlider lumCS;
	ColorSlider valueCS;
	double hue;
	double lum;
	double value;

	double hsv[] = new double[3];
	BufferedImage hueImage;
	BufferedImage lumImage;
	BufferedImage valueImage;

	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.hsv = rgb2hsv(result);
		
		this.hue = hsv[0];
		this.lum = hsv[1];
		this.value = hsv[2];

		this.result = result;
		result.addObserver(this);
		
		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		lumImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		computeHueImage(hue,lum,value);
		computeLumImage(hue,lum,value);
		computeValueImage(hue,lum,value); 	

	}
	
	private void computeValueImage(double hue2, double lum2, double value2) {
		// TODO Auto-generated method stub
	
		for (int i = 0; i<imagesWidth; ++i) {
			double value = ((double)i / (double)imagesWidth);
			Pixel p = hsv2Rgb(hue2,lum2,value); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				valueImage.setRGB(i, j, rgb);
			}
		}
		if (valueCS != null) {
			valueCS.update(valueImage);
		}
	}

	private void computeLumImage(double hue2, double lum2, double value2) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i<imagesWidth; ++i) {
			double lum = ((double)i / (double)imagesWidth);
			Pixel p = hsv2Rgb(hue2,lum,value2); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				lumImage.setRGB(i, j, rgb);
			}
		}
		if (lumCS != null) {
			lumCS.update(lumImage);
		}
	}

	private void computeHueImage(double hue2, double lum2, double value2) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i<imagesWidth; ++i) {
			double hue = ((double)i / (double)imagesWidth)*360.0;
			Pixel p = hsv2Rgb(hue,lum2,value2); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (hueCS != null) {
			hueCS.update(hueImage);
		}
	}

	private double[] rgb2hsv(ColorDialogResult result2) {
		// TODO Auto-generated method stub
		// h compris entre 0 et 360, L entre 0 et 1, V entre 0 et 1
		int red = result2.getPixel().getRed();
		int green = result2.getPixel().getGreen();
		int blue = result2.getPixel().getBlue();
		
		double r,g,b;
		double hsvReturn[] = new double[3];
		double delta, cmax, cmin;
		r = (double)red/255;
		g = (double)green/255;
		b = (double)blue/255;
		cmax = Math.max(Math.max(r,g), b);
		cmin = Math.min(Math.min(r,g), b);
		delta = cmax - cmin;

		// Hue calculation
		if (delta == 0.0) {hsvReturn[0] = 0.0;}
		if (cmax==r) {
			hsvReturn[0] = 60*(((g-b)/delta)%6);
		}
		if (cmax==g) {
			hsvReturn[0] = 60*(((b-r)/delta)+2);
		}
		if (cmax==b) {
			hsvReturn[0] = 60*(((r-g)/delta)+4);
		}
		
		//luminance calculation
		if (cmax==0.0) {
			hsvReturn[1] = 0.0;
		}else {
			hsvReturn[1] = delta/cmax;
		}
		
		//value calculation
		hsvReturn[2] = cmax;
		
		return hsvReturn;
	}
	
	private Pixel hsv2Rgb(double hue, double lum, double value) {
		// TODO Auto-generated method stub
		int red;
		int green;
		int blue;
		double c,x,r,g,b,m;
		r=0.0;
		g=0.0;
		b=0.0;
		
		c = lum*value;
		x = c*(1-Math.abs(((hue/60)%2)-1));
		m = value - c;
		
		if (0.0<=hue && hue<60.0) {
			r=c;
			g=x;
			b=0.0;
		}
		if (60.0<=hue && hue<120.0) {
			r=x;
			g=c;
			b=0.0;
		}
		if (120.0<=hue && hue<180.0) {
			r=0.0;
			g=c;
			b=x;
		}
		if (180.0<=hue && hue<240.0) {
			r=0.0;
			g=x;
			b=c;
		}
		if (240.0<=hue && hue<300.0) {
			r=x;
			g=0;
			b=c;
		}
		if (300.0<=hue && hue<360.0) {
			r=c;
			g=0.0;
			b=x;
		}
		red = (int)((r+m)*255.0);
		green = (int)((g+m)*255.0);
		blue = (int)((b+m)*255.0);
		
		Pixel p = new Pixel(red, green, blue, 255);
		return p;
	}


	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateHue = false;
		boolean updateLum = false;
		boolean updateValue = false;

		if (s == hueCS && v != (int)hue*255/360) {
			hue = (double)v*360/255;
			
			updateLum = true;
			updateValue = true;
		}
		if (s == lumCS && v != (int)lum*255) {
			lum = (double)v/255;
			
			updateHue = true;
			updateValue = true;
		}
		if (s == valueCS && v != (int)value*255) {
			value = (double)v/255;
			
			updateLum = true;
			updateHue = true;
		}
		
		if (updateHue) {
			computeHueImage(hue,lum,value);
		}
		if (updateLum) {
			computeLumImage(hue,lum,value);
		}
		if (updateValue) {
			computeValueImage(hue,lum,value);
		}	
		
		result.setPixel(hsv2Rgb(hue,lum,value));
	}
	
	
	/**
	 * @return
	 */
	public BufferedImage getHueImage() {
		return hueImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getLumImage() {
		return lumImage;
	}

	/**
	 * @return
	 */
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
	public void setLumCS(ColorSlider slider) {
		lumCS = slider;
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
	public double getHue() {
		return hue*255/360;
	}

	/**
	 * @return
	 */
	public double getLum() {
		return lum*255;
	}

	/**
	 * @return
	 */
	public double getValue() {
		return value*255;
	}

	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = hsv2Rgb(hue, lum, value);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		double hsv[]= rgb2hsv(result);
		
		this.hue = hsv[0];
		this.lum = hsv[1];
		this.value = hsv[2];
		
		hueCS.setValue((int)this.hue*255/360);
		lumCS.setValue((int)this.lum*255);
		valueCS.setValue((int)this.value*255);
		
		computeHueImage(hue,lum,value);
		computeLumImage(hue,lum,value);
		computeValueImage(hue,lum,value);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}
