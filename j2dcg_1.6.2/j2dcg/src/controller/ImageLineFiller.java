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

package controller;
import model.*;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

/**
 * <p>Title: ImageLineFiller</p>
 * <p>Description: Image transformer that inverts the row color</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barr�-Brisebois, �ric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.12 $
 */
public class ImageLineFiller extends AbstractTransformer {
	private ImageX currentImage;
	private int currentImageWidth;
	private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	private boolean threshold = false;
	
	/**
	 * Creates an ImageLineFiller with default parameters.
	 * Default pixel change color is black.
	 */
	public ImageLineFiller() {
	}
	
	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FLOODER; } 
	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;
				currentImageWidth = currentImage.getImageWidth();

				Point pt = e.getPoint();
				
				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}
				ptTransformed.translate(currentImage.getPosition().x, currentImage.getPosition().y);
				System.out.println("width : "+ currentImage.getImageWidth());
				System.out.println("width : "+ currentImage.getImageHeight());
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {
					currentImage.beginPixelUpdate();
					if (floodFill == true) {
						
						insideFill(ptTransformed);

					} else if (floodFill == false) {

						borderFill(ptTransformed);
					}
					
					currentImage.endPixelUpdate();											 	
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param ptClicked
	 * border fill with specified color
	 */
	private void borderFill(Point ptClicked) {
		boolean borderbool = false;
		boolean thresholdHSV = false;
		Point BorderStarter ;
		Pixel baseLinePixel  = currentImage.getPixel(ptClicked.x, ptClicked.y);
		Stack stack = new Stack();
		stack.push(ptClicked);
		System.out.println("BaseLinePixel"+baseLinePixel);
		//Recherche de bordure
		while (!stack.empty() && !borderbool) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth()) {
				
				if(currentImage.getPixel(current.x, current.y).equals(baseLinePixel)) {
					
					// Next points to fill.
					Point nextLeft = new Point(current.x-1, current.y);
					Point nextRight = new Point(current.x+1, current.y);
					Point nextDown = new Point(current.x, current.y-1);
					Point nextUp = new Point(current.x, current.y+1);
					
					stack.push(nextLeft);
					stack.push(nextRight);
					stack.push(nextDown);
					stack.push(nextUp);
				}else {
					borderbool = true;
				    baseLinePixel  = currentImage.getPixel(current.x, current.y);
				    System.out.println("BORDER!!!!!!   "+baseLinePixel);
				    BorderStarter = new Point(current.x, current.y);
				    stack.push(BorderStarter);
				}
			}
		}
		
		//Remplissage de bordure 
		while (!stack.empty()  && borderbool) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth()) {
				if (!currentImage.getPixel(current.x, current.y).equals(borderColor)) {

					thresholdHSV = validationThreshold(borderColor, currentImage.getPixel(current.x, current.y));
					System.out.println("Threshold Validation Border"+ thresholdHSV);
					
					if (currentImage.getPixel(current.x, current.y).equals(baseLinePixel) && thresholdHSV == true) {

						currentImage.setPixel(current.x, current.y, borderColor);

						// Next points to fill.
						Point nextLeft = new Point(current.x - 1, current.y);
						Point nextRight = new Point(current.x + 1, current.y);
						Point nextDown = new Point(current.x, current.y - 1);
						Point nextUp = new Point(current.x, current.y + 1);

						stack.push(nextLeft);
						stack.push(nextRight);
						stack.push(nextDown);
						stack.push(nextUp);
					}

				} 
			}
		}
		
	}

	/**
	 * @param ptClicked
	 * Inside line fill with specified color
	 */
	private void insideFill(Point ptClicked) {
		boolean thresholdHSV = false;
		Stack stack = new Stack();
		stack.push(ptClicked);
		Pixel baseLinePixel  = currentImage.getPixel(ptClicked.x, ptClicked.y);

		while (!stack.empty()) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth() &&
				!currentImage.getPixel(current.x, current.y).equals(fillColor)) {
				thresholdHSV = validationThreshold(borderColor, currentImage.getPixel(current.x, current.y));
				System.out.println("Threshold Validation flood"+ thresholdHSV);
				if(currentImage.getPixel(current.x, current.y).equals(baseLinePixel)&& thresholdHSV == true) {
					
					currentImage.setPixel(current.x, current.y, fillColor);
					
					// Next points to fill.
					Point nextLeft = new Point(current.x-1, current.y);
					Point nextRight = new Point(current.x+1, current.y);
					Point nextDown = new Point(current.x, current.y-1);
					Point nextUp = new Point(current.x, current.y+1);
					
					stack.push(nextLeft);
					stack.push(nextRight);
					stack.push(nextDown);
					stack.push(nextUp);
				}				

			}
		}
	}
	
	/**
	 * @return
	 */
	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
		System.out.println("new border color");
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean b) {
		floodFill = b;
		if (floodFill) {
			System.out.println("now doing Flood Fill");
		} else {
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {
		return hueThreshold;
	}

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @return
	 */
	public int getValueThreshold() {
		return valueThreshold;
	}

	/**
	 * @param i
	 */
	public void setHueThreshold(int i) {
		hueThreshold = i;
		System.out.println("new Hue Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
	}
	
	/**
	 * @param sliderColor, currentPixel
	 * 
	 * Validation du Threshold avant le remplissage
	 */
	private boolean validationThreshold(Pixel sliderColor,Pixel currentPixel) {
		boolean thresholdCheck = false;
		double hsv[] = rgb2hsv(sliderColor);
		double hsvcurrentPixel[] = rgb2hsv(currentPixel);
		
		int h,s,v;
		System.out.println("SlideColor: h= "+(int)hsv[0]+" s= "+(int)hsv[1]+" v= "+(int)hsv[2]);
		System.out.println("Thresholf: h= "+hueThreshold+" s= "+saturationThreshold+" v= "+valueThreshold);
		
		h = Math.abs((int)hsv[0] - hueThreshold);
		s = Math.abs((int)hsv[1] - saturationThreshold);
		v = Math.abs((int)hsv[2] - valueThreshold);
		
		System.out.println("HsvCurrentPixel: h= "+(int)hsvcurrentPixel[0]+" s= "+(int)hsvcurrentPixel[1]+" v= "+(int)hsvcurrentPixel[2]);
		System.out.println("Différence en pixel et slider: h= "+h+" s= "+s+" v= "+v);
		if(h<= hsv[0] && h>=hueThreshold &&
				s<= hsv[1] && h>=saturationThreshold &&
					v<= hsv[2] && h>=valueThreshold) {
			thresholdCheck = true;
		}
		return thresholdCheck;
	}
	
	/**
	 * @param result2 
	 * 
	 * Méthode qui permet de faire la transformation de RGB a HSV
	 */
	private double[] rgb2hsv(Pixel result2) {
		// TODO Auto-generated method stub
		// h compris entre 0 et 360, L entre 0 et 1, V entre 0 et 1
		int red = result2.getRed();
		int green = result2.getGreen();
		int blue = result2.getBlue();
		
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
			hsvReturn[1] = (delta/cmax)*255;
		}
		
		//value calculation
		hsvReturn[2] = cmax*255;
		
		return hsvReturn;
	}
	

}
