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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

import model.Document;
import model.Shape;
import view.Application;

/**
 * <p>Title: ScaleCommand</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jean-Fran�ois Barras, �ric Paquette</p>
 * <p>Company: (�TS) - �cole de Technologie Sup�rieure</p>
 * <p>Created on: 2004-03-19</p>
 * @version $Revision: 1.2 $
 */
public class ScaleCommand extends AnchoredTransformationCommand {

	/**
	 * @param sx the multiplier to the horizontal size
	 * @param sy the multiplier to the vertical size
	 * @param anchor one of the predefined positions for the anchor point
	 */
	public ScaleCommand(double sx, double sy, int anchor, List aObjects) {
		super(anchor);
		this.sx = sx;
		this.sy = sy;
		objects = aObjects;
	}
	
	/* (non-Javadoc)
	 * @see controller.Command#execute()
	 */
	public void execute() {
		System.out.println("command: scaling x by " + sx +
                           " and y by " + sy + " ; anchored on " + getAnchor() );
		Iterator iter = objects.iterator();
		Shape shape;
		Point anchorSelected = null;
		int anchorX = 0 ;
		int anchorY = 0 ;
		//get the selected object
		Document doc = Application.getInstance().getActiveDocument();
		List selectedObjects = doc.getSelectedObjects();
		
		//Anchor selection for the scale
		switch (getAnchor()) {
			case 0: //TopLeft
			{
				System.out.println("Top Left, Get Anchor Point ... " );
				anchorSelected = getAnchorPoint(selectedObjects);
				System.out.println("Point X Y  "+anchorSelected.x +"  "+ anchorSelected.y);
			}
			break;
			case 1:  //Top Center
			{
				System.out.println("Top Center, Get Anchor Point ... " );
				anchorSelected = getAnchorPoint(selectedObjects);
				System.out.println("Point X Y  "+anchorSelected.x +"  "+ anchorSelected.y);
			}
			break;
			case 2:  //Top Right
			{
				System.out.println("Top Right, Get Anchor Point ... " );
				anchorSelected = getAnchorPoint(selectedObjects);
				System.out.println("Point X Y  "+anchorSelected.x +"  "+ anchorSelected.y);
			}
			break;
			case 3:  //Middle Left
			{
				System.out.println("Middle Left, Get Anchor Point ... " );
				anchorSelected = getAnchorPoint(selectedObjects);
				System.out.println("Point X Y  "+anchorSelected.x +"  "+ anchorSelected.y);
			}
			break;
			case 4:  //Center
			{
				System.out.println("Center, Get Anchor Point ... " );
				anchorSelected = getAnchorPoint(selectedObjects);
				System.out.println("Point X Y  "+anchorSelected.x +"  "+ anchorSelected.y);
			}
			break;
			case 5:  //Middle Right
			{
				System.out.println("Middle Right, Get Anchor Point ... " );
				anchorSelected = getAnchorPoint(selectedObjects);
				System.out.println("Point X Y  "+anchorSelected.x +"  "+ anchorSelected.y);
			}
			break;
			case 6:  //Bottom Left
			{
				System.out.println("Bottom Left, Get Anchor Point ... " );
				anchorSelected = getAnchorPoint(selectedObjects);
				System.out.println("Point X Y  "+anchorSelected.x +"  "+ anchorSelected.y);
			}
			break;
			case 7:  //Bottom center
			{
				System.out.println("Bottom center, Get Anchor Point ... " );
				anchorSelected = getAnchorPoint(selectedObjects);
				System.out.println("Point X Y  "+anchorSelected.x +"  "+ anchorSelected.y);
			}
		}
		//appliquer le scale sur l'object
		while(iter.hasNext()){
			shape = (Shape)iter.next();
			mt.addMememto(shape);
			AffineTransform t = shape.getAffineTransform();
			t.translate(-anchorX,-anchorY);
			t.scale(sx, sy);
			t.translate((1-sx)*anchorSelected.x,(1-sy)*anchorSelected.y);
			shape.setAffineTransform(t);
		}
			
	}

	/* (non-Javadoc)
	 * @see controller.Command#undo()
	 */
	public void undo() {
		mt.setBackMementos();
	}

	private MementoTracker mt = new MementoTracker();
	private List objects;
	private double sx;
	private double sy;

}