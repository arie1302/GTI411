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

class CYMKColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider cyanCS;
	ColorSlider yellowCS;
	ColorSlider magentaCS;
	ColorSlider blackCS;
	
	int red;
	int green;
	int blue;
	int cyan;
	int yellow;
	int magenta;
	int black;
	
	BufferedImage cyanImage;
	BufferedImage yellowImage;
	BufferedImage magentaImage;
	BufferedImage blackImage;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	CYMKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		this.red = result.getPixel().getRed();
	    this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();
		
		//calculer les valeurs du CYMK
		int[] CYMK = rgbToCymk(red, green, blue);
		
		this.cyan = CYMK[0];
		this.yellow = CYMK[1];
		this.magenta = CYMK[2];
		this.black = CYMK[3];
		
		this.result = result;
		result.addObserver(this);
		
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		computeCyanImage(cyan, yellow, magenta, black);
		computeYellowImage(cyan, yellow, magenta, black);
		computeMagentaImage(cyan, yellow, magenta, black); 	
		computeBlackImage(cyan, yellow, magenta, black);
	}
	
	/**
	 * Méthode pour calculer la conversion de RGB à CYMK
	 * @param resultPixelRed
	 * @param resultPixelgreen
	 * @param resultPixelBlue
	 * @return cymkResult
	 */
	private int[] rgbToCymk(int resultPixelRed, int resultPixelgreen, int resultPixelBlue) {
		
		float primeR = resultPixelRed/255;
		float primeG = resultPixelgreen/255;
		float primeB = resultPixelBlue/255;
		float k = 1; 

		int[] cymkResult = new int [4] ;
		
		System.out.println("primeR:  "+ primeR);
		System.out.println("primeG:  "+ primeG);
		System.out.println("primeB:  "+ primeB);
		
		k = 1 - Math.max(primeR, Math.max(primeG, primeB));
		int m = (int)((1 -primeG - k)/(1-k)*100);
		int y = (int)((1 -primeB - k)/(1-k)*100);
		int c = (int)((1 -primeR - k)/(1-k)*100);
		
		cymkResult[0] = c;
		cymkResult[1] = y;
		cymkResult[2] = m;
		cymkResult[3] = (int)k;
		
		return cymkResult; 
	}

	private int[] cymkToRgb(int resultPixelCyan, int resultPixelYellow, int resultPixelMagenta, int resultPixelBlack) {
		
		int calculatedR = 255* (1-resultPixelCyan)*(1-resultPixelBlack);
		int calculatedG = 255* (1-resultPixelMagenta)*(1-resultPixelBlack);
		int calculatedB = 255* (1-resultPixelYellow)*(1-resultPixelBlack);
		
		int[] rgbResult = new int [3] ;
		
		rgbResult[0] = calculatedR;
		rgbResult[1] = calculatedB;
		rgbResult[2] = calculatedG;
		
		return rgbResult;
		
	}
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateCyan = false;
		boolean updateYellow = false;
		boolean updateMagenta = false;
		boolean updateBlack = false;

		if (s == cyanCS && v != cyan) {
			cyan = v;
			updateYellow = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == yellowCS && v != yellow) {
			yellow = v;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == magentaCS && v != magenta) {
			magenta = v;
			updateYellow = true;
			updateCyan = true;
			updateBlack = true;
		}
		if (s == blackCS && v != black) {
			black = v;
			updateCyan = true;
			updateYellow = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (updateCyan) {
			computeCyanImage(cyan, yellow, magenta, black);
		}
		if (updateYellow) {
			computeYellowImage(cyan, yellow, magenta, black);
		}
		if (updateMagenta) {
			computeMagentaImage(cyan, yellow, magenta, black);
		}		
		if (updateBlack) {
			computeBlackImage(cyan, yellow, magenta, black);
		}			
		
		Pixel pixel = new Pixel(red, green, blue, 255);
		result.setPixel(pixel);
	}
	
	public void computeCyanImage(int cyan, int yellow, int magenta, int black) { 
		int[] rgbArray = cymkToRgb(cyan, yellow, magenta, black);
		
		int rCalculated = rgbArray[0];
		int gCalculated = rgbArray[1];
		int bCalculated = rgbArray[2];
		
		Pixel p = new Pixel(rCalculated, gCalculated, bCalculated, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setGreen((int)(((double)i / (double)imagesWidth)*255.0)); 
			p.setBlue((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
	}
	
	public void computeYellowImage(int cyan, int yellow, int magenta, int black) {
		
		int[] rgbArray = cymkToRgb(cyan, yellow, magenta, black);
		
		int rCalculated = rgbArray[0];
		int gCalculated = rgbArray[1];
		int bCalculated = rgbArray[2];
		
		Pixel p = new Pixel(rCalculated, gCalculated, bCalculated, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed((int)(((double)i / (double)imagesWidth)*255.0)); 
			p.setGreen((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}
	
	public void computeMagentaImage(int cyan, int yellow, int magenta, int black) { 
		int[] rgbArray = cymkToRgb(cyan, yellow, magenta, black);
		
		int rCalculated = rgbArray[0];
		int gCalculated = rgbArray[1];
		int bCalculated = rgbArray[2];
		
		Pixel p = new Pixel(rCalculated, gCalculated, bCalculated, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed((int)(((double)i / (double)imagesWidth)*255.0)); 
			p.setBlue((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}
	
	public void computeBlackImage(int cyan, int yellow, int magenta, int black) { 
		int[] rgbArray = cymkToRgb(cyan, yellow, magenta, black);
		
		int rCalculated = rgbArray[0];
		int gCalculated = rgbArray[1];
		int bCalculated = rgbArray[2];
		
		Pixel p = new Pixel(rCalculated, gCalculated, bCalculated, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed((int)(((double)i / (double)imagesWidth)*255.0));
			p.setGreen((int)(((double)i / (double)imagesWidth)*255.0));
			p.setBlue((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				blackImage.setRGB(i, j, rgb);
			}
		}
		if (blackCS != null) {
			blackCS.update(blackImage);
		}
	}
	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}
	
	public BufferedImage getBlackImage() {
		return blackImage;
	}

	public BufferedImage getCyanImage() {
		return cyanImage;
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		blackCS = slider;
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

	public int getCyan() {
		return cyan;
	}

	public int getYellow() {
		return yellow;
	}
	
	public int getMagenta() {
		return magenta;
	}
	
	public int getBlack() {
		return black;
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
		int[] CYMK = rgbToCymk(red, green, blue);
		
		cyan = CYMK[0];
		yellow = CYMK[1];
		magenta = CYMK[2];
		black = CYMK[3];
		
		
		cyanCS.setValue(cyan);
		magentaCS.setValue(magenta);
		yellowCS.setValue(yellow);
		blackCS.setValue(black);
		
		computeCyanImage(cyan, yellow, magenta, black);
		computeYellowImage(cyan, yellow, magenta, black);
		computeMagentaImage(cyan, yellow, magenta, black);
		computeBlackImage(cyan, yellow, magenta, black);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

