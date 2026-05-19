package imageprocessing;

import java.awt.image.BufferedImage;

public class CartoonRenderer {
    public BufferedImage render(BufferedImage quantizedImg, BufferedImage edgeMap) {
        int width = quantizedImg.getWidth();
        int height = quantizedImg.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int edgeRgb = edgeMap.getRGB(x, y);
                
                if ((edgeRgb & 0xFFFFFF) == 0x000000) {
                    result.setRGB(x, y, 0x000000);
                } else {
                    result.setRGB(x, y, quantizedImg.getRGB(x, y));
                }
            }
        }
        return result;
    }
}