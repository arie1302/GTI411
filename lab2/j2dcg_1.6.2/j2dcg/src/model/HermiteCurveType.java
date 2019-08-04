package model;

import java.awt.Point;
import java.util.List;

public class HermiteCurveType extends CurveType{

	//not completed
	public HermiteCurveType(String name) {
		super(name);
	}

	@Override
	public int getNumberOfSegments(int numberOfControlPoints) {
		// TODO Auto-generated method stub
		if (numberOfControlPoints >= 4) {
			return (numberOfControlPoints - 1) / 3;
		} else {
			return 0;
		}
	}

	@Override
	public int getNumberOfControlPointsPerSegment() {
		return 4;
	}

	@Override
	public ControlPoint getControlPoint(List controlPoints, int segmentNumber, int controlPointNumber) {
		int controlPointIndex = segmentNumber * 3 + controlPointNumber;
		return (ControlPoint)controlPoints.get(controlPointIndex);
	}

	@Override
	public Point evalCurveAt(List controlPoints, double t) {
		List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);
		Point r1 = new Point(((ControlPoint)controlPoints.get(1)).getCenter().x - 
				((ControlPoint)controlPoints.get(0)).getCenter().x, 
				((ControlPoint)controlPoints.get(1)).getCenter().y - 
				((ControlPoint)controlPoints.get(0)).getCenter().y);

		Point r4 = new Point(((ControlPoint)controlPoints.get(3)).getCenter().x - 
				((ControlPoint)controlPoints.get(2)).getCenter().x, 
				((ControlPoint)controlPoints.get(3)).getCenter().y - 
				((ControlPoint)controlPoints.get(2)).getCenter().y);
		
		List gVector = Matrix.buildColumnVector4(
            ((ControlPoint)controlPoints.get(0)).getCenter(),
            ((ControlPoint)controlPoints.get(3)).getCenter(), r1,r4);

		Point p = Matrix.eval(tVector, matrix, gVector);
		return p;

	}
	
	private List HermiteMatrix = 
			(Matrix.buildMatrix4(2,-2,1,1, 
								 -3,3,-2,-1, 
								 0,0,1,0, 
								 1,0,0,0));
								 
		private List matrix = HermiteMatrix;
}
