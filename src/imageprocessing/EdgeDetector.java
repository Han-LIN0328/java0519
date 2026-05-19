package imageprocessing;

import java.awt.image.BufferedImage;

public class EdgeDetector {
    
    private BufferedImage gaussianBlur(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        
        float[] kernel = {
            1/16f, 2/16f, 1/16f,
            2/16f, 4/16f, 2/16f,
            1/16f, 2/16f, 1/16f
        };
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float sum = 0;
                int kIdx = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = src.getRGB(x + kx, y + ky);
                        int val = rgb & 0xFF;
                        sum += val * kernel[kIdx++];
                    }
                }
                int pixelOut = Math.min(255, Math.max(0, (int) sum));
                dest.setRGB(x, y, (pixelOut << 16) | (pixelOut << 8) | pixelOut);
            }
        }
        return dest;
    }

    public BufferedImage detect(BufferedImage srcGray) {
        BufferedImage blurred = gaussianBlur(srcGray);
        int width = blurred.getWidth();
        int height = blurred.getHeight();
        
        BufferedImage edgeMap = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        
        int[][] gx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] gy = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        
        int[][] magnitudes = new int[width][height];
        int sumMag = 0;
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int resX = 0;
                int resY = 0;
                
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int val = blurred.getRGB(x + kx, y + ky) & 0xFF;
                        resX += val * gx[ky + 1][kx + 1];
                        resY += val * gy[ky + 1][kx + 1];
                    }
                }
                
                int mag = (int) Math.sqrt(resX * resX + resY * resY);
                magnitudes[x][y] = mag;
                sumMag += mag;
            }
        }
        
        int threshold = (int) ((sumMag / (double) (width * height)) * 1.5);
        threshold = Math.max(threshold, 30);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (magnitudes[x][y] > threshold) {
                    edgeMap.setRGB(x, y, 0x000000); 
                } else {
                    edgeMap.setRGB(x, y, 0xFFFFFF);
                }
            }
        }
        return edgeMap;
    }
}