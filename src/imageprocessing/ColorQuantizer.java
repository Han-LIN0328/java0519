package imageprocessing;

import java.awt.image.BufferedImage;

public class ColorQuantizer {
    public BufferedImage quantize(BufferedImage src, int levels) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage quantized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        int step = 256 / levels;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                int qR = Math.min(255, (r / step) * step + (step / 2));
                int qG = Math.min(255, (g / step) * step + (step / 2));
                int qB = Math.min(255, (b / step) * step + (step / 2));
                
                int qRgb = (qR << 16) | (qG << 8) | qB;
                quantized.setRGB(x, y, qRgb);
            }
        }
        return quantized;
    }
}