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
 * <p>
 * Title: ImageLineFiller
 * </p>
 * <p>
 * Description: Image transformer that inverts the row color
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003 Colin Barr�-Brisebois, �ric Paquette
 * </p>
 * <p>
 * Company: ETS - �cole de Technologie Sup�rieure
 * </p>
 * 
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
	 * Creates an ImageLineFiller with default parameters. Default pixel change
	 * color is black.
	 */
	public ImageLineFiller() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() {
		return ID_FLOODER;
	}

	protected boolean mouseClicked(MouseEvent e) {
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape) intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX) shape;
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
				System.out.println("width : " + currentImage.getImageWidth());
				System.out.println("width : " + currentImage.getImageHeight());
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() && 0 <= ptTransformed.y
						&& ptTransformed.y < currentImage.getImageHeight()) {
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
	 * M�thode qui modifie les couleurs des pixels qui sont � l'int. de la bordure d'une couleur X. 
	 * @param ptClicked border fill with specified color
	 */
	private void borderFill(Point ptClicked) {
		
		//Initialisation des variables qui seront utilis�es dans la m�thode 
		boolean thresholdBorder = false;
		Pixel baseLinePixel = currentImage.getPixel(ptClicked.x, ptClicked.y);
		Stack stack = new Stack();
		stack.push(ptClicked);
		// System.out.println("BaseLinePixel"+baseLinePixel);

		// Remplissage de la zone int. avant la couleur de la bordure
		while (!stack.empty()) {
			Point current = (Point) stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth() && 0 <= current.y
					&& current.y < currentImage.getImageHeight()
					&& !currentImage.getPixel(current.x, current.y).equals(fillColor)) {

				System.out.println("Current Pixel: " + currentImage.getPixel(current.x, current.y));
				thresholdBorder = validationThreshold(currentImage.getPixel(current.x, current.y), borderColor);
				System.out.println("Threshold Validation border" + thresholdBorder);


				if (thresholdBorder == false && currentImage.getPixel(current.x, current.y).equals(baseLinePixel)) {

					currentImage.setPixel(current.x, current.y, fillColor);
					
					// M�thode des qutre voisins pour la recherche des autres pixels
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

	/**
	 * M�thode qui modifie les couleurs des pixels qui sont � l'int. de la r�gion 
	 * @param ptClicked Inside line fill with specified color
	 */
	private void insideFill(Point ptClicked) {
		
		//Initialisation des variables qui seront utilis�es dans la m�thode 
		boolean thresholdHSV = false;
		Stack stack = new Stack();
		stack.push(ptClicked);
		Pixel baseLinePixel = currentImage.getPixel(ptClicked.x, ptClicked.y);

		//Boucle pour v�rifier tous les pixels 
		while (!stack.empty()) {
			Point current = (Point) stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth()
					&& !currentImage.getPixel(current.x, current.y).equals(fillColor)) {

				// Validation de la région pour le changement de la couleur
				thresholdHSV = validationThreshold(currentImage.getPixel(current.x, current.y), baseLinePixel);
				System.out.println("Threshold Validation flood" + thresholdHSV);
				if (thresholdHSV == true) {

					currentImage.setPixel(current.x, current.y, fillColor);

					// M�thode des qutre voisins pour la recherche des autres pixels
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
		double testHSV[];
		borderColor = pixel;
		System.out.println("new border color" + borderColor);
		testHSV = rgb2hsv(borderColor);
		System.out.println("new border color HSV  " + testHSV[0] + "  " + testHSV[1] + "  " + testHSV[2]);
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}

	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is
	 *         set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary
	 *          Fill.
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
	 * Validation du Threshold avant le remplissage
	 * @param currentPixel
	 * @param baseline
	 * @return thresholdCheck
	 */
	private boolean validationThreshold(Pixel currentPixel, Pixel baseline) {
		
		//Variables qui seront utilis�es par la m�thode pour valider le seuil
		boolean thresholdCheck = false;
		double hsvcurrentPixel[] = rgb2hsv(currentPixel);
		double hsvbaseline[] = rgb2hsv(baseline);

		// System.out.println("Thresholf: h= "+hueThreshold+" s= "+saturationThreshold+"
		// v= "+valueThreshold);
		// System.out.println("baseline: h= "+(int)hsvbaseline[0]+" s=
		// "+(int)hsvbaseline[1]+" v= "+(int)hsvbaseline[2]);
		// System.out.println("HsvCurrentPixel: h= "+(int)hsvcurrentPixel[0]+" s=
		// "+(int)hsvcurrentPixel[1]+" v= "+(int)hsvcurrentPixel[2]);

		int h1, s1, v1, h2, s2, v2;

		//Variables de l'intervalle du seuil selon le syst�me de ref. HSV
		h1 = (int) (hsvbaseline[0] - hueThreshold);
		s1 = (int) (hsvbaseline[1] - saturationThreshold);
		v1 = (int) (hsvbaseline[2] - valueThreshold);
		h2 = (int) (hsvbaseline[0] + hueThreshold);
		s2 = (int) (hsvbaseline[1] + saturationThreshold);
		v2 = (int) (hsvbaseline[2] + valueThreshold);

		// System.out.println("h1: "+h1 + " h2: "+h2);
		// System.out.println("s1: "+s1 + " s2: "+s2);
		// System.out.println("v1: "+v1 + " v2: "+v2);

		//Si le pixel se retrouve � l'interieur du seuil alors il changera pour vrai la constante thresholdCheck
		if (hsvcurrentPixel[0] >= h1 && hsvcurrentPixel[0] <= h2 && hsvcurrentPixel[1] >= s1 && hsvcurrentPixel[1] <= s2
				&& hsvcurrentPixel[2] >= v1 && hsvcurrentPixel[2] <= v2) {
			thresholdCheck = true;
		}
		return thresholdCheck;
	}

	
	/**
	 * *Méthode qui permet de faire la transformation de RGB a HSV
	 * selon h compris entre 0 et 360, L entre 0 et 1, V entre 0 et 1
	 * @param result2
	 * @return hsvReturn
	 */
	private double[] rgb2hsv(Pixel result2) {

		// m�thodes pour recuperer les valeurs RGB du pixel
		int red = result2.getRed();
		int green = result2.getGreen();
		int blue = result2.getBlue();

		// M�thodes pour le calcul du HSV
		double r, g, b;
		double hsvReturn[] = new double[3];
		double delta, cmax, cmin;
		r = (double) red / 255;
		g = (double) green / 255;
		b = (double) blue / 255;
		cmax = Math.max(Math.max(r, g), b);
		cmin = Math.min(Math.min(r, g), b);
		delta = cmax - cmin;

		// Calcul de la teinte du HSV
		if (delta == 0.0) {
			hsvReturn[0] = 0.0;
		}
		if (cmax == r) {
			hsvReturn[0] = 60 * (((g - b) / delta) % 6);
		}
		if (cmax == g) {
			hsvReturn[0] = 60 * (((b - r) / delta) + 2);
		}
		if (cmax == b) {
			hsvReturn[0] = 60 * (((r - g) / delta) + 4);
		}

		// Calcul de la saturation du HSV
		if (cmax == 0.0) {
			hsvReturn[1] = 0.0;
		} else {
			hsvReturn[1] = (delta / cmax) * 255;
		}

		// Calcul de la valeur du HSV
		hsvReturn[2] = cmax * 255;

		return hsvReturn;
	}

}
