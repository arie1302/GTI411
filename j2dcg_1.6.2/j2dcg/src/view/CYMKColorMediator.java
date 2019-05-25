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
		this.result = result;
		result.addObserver(this);
		
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeCyanImage(red, green, blue);
		computeYellowImage(red, green, blue);
		computeMagentaImage(red, green, blue); 	
		computeBlackImage(red, green, blue); 
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateRed = false;
		boolean updateGreen = false;
		boolean updateBlue = false;
		if (s == cyanCS && v != red) {
			red = v;
			updateGreen = true;
			updateBlue = true;
		}
		if (s == magentaCS && v != green) {
			green = v;
			updateRed = true;
			updateBlue = true;
		}
		if (s == yellowCS && v != blue) {
			blue = v;
			updateRed = true;
			updateGreen = true;
		}
		if (s == blackCS && v != blue) {
			blue = v;
			updateRed = true;
			updateGreen = true;
		}
		if (updateRed) {
			computeCyanImage(red, green, blue);
		}
		if (updateGreen) {
			computeYellowImage(red, green, blue);
		}
		if (updateBlue) {
			computeMagentaImage(red, green, blue);
		}		
		if (updateBlue) {
			computeMagentaImage(red, green, blue);
		}
		
		
		Pixel pixel = new Pixel(red, green, blue, 255);
		result.setPixel(pixel);
	}
	
	public void computeCyanImage(int red, int green, int blue) { 
		Pixel p = new Pixel(red, green, blue, 255); 
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
	
	public void computeYellowImage(int red, int green, int blue) {
		Pixel p = new Pixel(red, green, blue, 255); 
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
	
	public void computeMagentaImage(int red, int green, int blue) { 
		Pixel p = new Pixel(red, green, blue, 255); 
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
	
	public void computeBlackImage(int red, int green, int blue) { 
		Pixel p = new Pixel(red, green, blue, 255); 
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

	/**
	 * @return
	 */
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

	/**
	 * @return
	 */
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
		
		cyanCS.setValue(cyan);
		magentaCS.setValue(magenta);
		yellowCS.setValue(yellow);
		blackCS.setValue(black);
		
		computeCyanImage(red, green, blue);
		computeYellowImage(red, green, blue);
		computeMagentaImage(red, green, blue);
		computeBlackImage(red, green, blue);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

