package controller;

import model.*;


public class customFilter3x3 extends Filter {	
	private double filterMatrix[][] = null;
	private double inverse= 0;
	
	/**
	 * Constructor by default
	 * @param paddingStrategy
	 * @param conversionStrategy
	 */
	public customFilter3x3(PaddingStrategy paddingStrategy, 
						 ImageConversionStrategy conversionStrategy) {
		super(paddingStrategy, conversionStrategy);	
		filterMatrix = new double[3][3];
	}
	
	/**
	 * Filters an ImageX and returns a ImageDouble.
	 */
	public ImageDouble filterToImageDouble(ImageX image) {
		return filter(conversionStrategy.convert(image));
	}
	
	/**
	 * Filters an ImageDouble and returns a ImageDouble.
	 */	
	public ImageDouble filterToImageDouble(ImageDouble image) {
		return filter(image);
	}
	
	/**
	 * Filters an ImageX and returns an ImageX.
	 */	
	public ImageX filterToImageX(ImageX image) {
		ImageDouble filtered = filter(conversionStrategy.convert(image)); 
		return conversionStrategy.convert(filtered);
	}
	
	/**
	 * Filters an ImageDouble and returns a ImageX.
	 */	
	public ImageX filterToImageX(ImageDouble image) {
		ImageDouble filtered = filter(image); 
		return conversionStrategy.convert(filtered);		
	}
	
	/**
	 * Custom filter Implementation
	 * @param image
	 * @return
	 */
	private ImageDouble filter(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		ImageDouble newImage = new ImageDouble(imageWidth, imageHeight);
		PixelDouble newPixel = null;
		double result = 0; 
		this.fillArray();
	
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				newPixel = new PixelDouble();
			
				//*******************************
				// RED
				for (int i = 0; i <= 2; i++) {
					for (int j = 0; j <= 2; j++) {
						result += filterMatrix[i][j] * inverse * getPaddingStrategy().pixelAt(image, x+
										(i-1), y+(j-1)).getRed();
					}
				}
				
				newPixel.setRed(result);
				result = 0;
						
				//*******************************
				// Green
				for (int i = 0; i <= 2; i++) {
					for (int j = 0; j <= 2; j++) {
						result += filterMatrix[i][j] * inverse *getPaddingStrategy().pixelAt(image,
										x+(i-1), y+(j-1)).getGreen();
					}
				}
				
				newPixel.setGreen(result);
				result = 0;
							  
				//*******************************
				// Blue
				for (int i = 0; i <= 2; i++) {
					for (int j = 0; j <= 2; j++) {
						result += filterMatrix[i][j] * inverse * getPaddingStrategy().pixelAt(image, x+(i-1), 
										y+(j-1)).getBlue();
					}
				}
				
				newPixel.setBlue(result);
				result = 0;
							
				//*******************************
				// Alpha
				newPixel.setAlpha(getPaddingStrategy().pixelAt(image, x,y).getAlpha());
							 
				//*******************************
				// Done
				newImage.setPixel(x, y, newPixel);
			}
		}
		
		return newImage;
	}

	/**
	 * 
	 */
	private void fillArray() {
		double value = 0;
		for (int i = 0; i <=2; i++) {
			for (int j = 0; j <= 2; j++) {
				value = value + this.filterMatrix[i][j];
			}
		}
		
		//When the value is null then change the value to 1 
		if(value == 0) {
			value = 1;
		}else {
			inverse = Math.pow(value, -1);
		}
		
		
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @param _value
	 */
	public void updatek(int i, int j, float _value) {
		this.filterMatrix[i][j] = _value;

	}
	
}
