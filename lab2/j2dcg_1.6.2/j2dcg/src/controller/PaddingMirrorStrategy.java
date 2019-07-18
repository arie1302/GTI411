package controller;
import model.*; 


public class PaddingMirrorStrategy extends PaddingStrategy{
	
	/**
	 * Mirror padding 
	 */
	public Pixel pixelAt(ImageX image, int x, int y) {
		
		int width = image.getImageWidth();
		int height = image.getImageHeight();
		
		if ( x == -1 || y ==-1 ) {
			
			if (x == -1 && y == -1) {
				x = x+1; 
				y = y+1;
			} else if (x == -1 && y == height) {
				x = x+1; 
				y = y-1;
			} else if (x == -1) {
				x = x+1; 			
			}else if (x == width && y == -1) {
				x = x-1; 
				y = y+1;
			}else if (y == -1) {
				y = y+1;				
			}
			
		}else if (x == width || y == height) {
			
			if (x == width && y == height) {
				x = x-1; 
				y = y-1;				
			}else if (x == width) {
				x = x-1; 			
			}else if (y == height) {
				y = y-1;
			}
		}

		return image.getPixel(x, y);
	}

	@Override
	public PixelDouble pixelAt(ImageDouble image, int x, int y) {
		int width = image.getImageWidth();
		int height = image.getImageHeight();
		
		if ( x == -1 || y ==-1 ) {
			
			if (x == -1 && y == -1) {
				x = x+1; 
				y = y+1;
			} else if (x == -1 && y == height) {
				x = x+1; 
				y = y-1;
			} else if (x == -1) {
				x = x+1; 			
			}else if (x == width && y == -1) {
				x = x-1; 
				y = y+1;
			}else if (y == -1) {
				y = y+1;				
			}
			
		}else if (x == width || y == height) {
			
			if (x == width && y == height) {
				x = x-1; 
				y = y-1;				
			}else if (x == width) {
				x = x-1; 			
			}else if (y == height) {
				y = y-1;
			}
		}

		return image.getPixel(x, y);
	}
	

}
