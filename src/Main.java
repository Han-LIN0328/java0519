import imageprocessing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Main extends JFrame {
    private JLabel imageLabel;
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    
    private GrayConverter grayConverter = new GrayConverter();
    private EdgeDetector edgeDetector = new EdgeDetector();
    private ColorQuantizer colorQuantizer = new ColorQuantizer();
    private CartoonRenderer cartoonRenderer = new CartoonRenderer();

    public Main() {
        setTitle("Traditional Cartoon Filter App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        imageLabel = new JLabel("請先載入圖片...", SwingConstants.CENTER);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton loadBtn = new JButton("載入圖片");
        JButton processBtn = new JButton("套用卡通濾鏡");
        JButton saveBtn = new JButton("儲存圖片");

        panel.add(loadBtn);
        panel.add(processBtn);
        panel.add(saveBtn);
        add(panel, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    originalImage = ImageIO.read(file);
                    processedImage = originalImage;
                    imageLabel.setIcon(new ImageIcon(scaleImage(originalImage, 750, 500)));
                    imageLabel.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "圖片載入失敗！");
                }
            }
        });

        processBtn.addActionListener(e -> {
            if (originalImage == null) {
                JOptionPane.showMessageDialog(this, "請先載入一張圖片！");
                return;
            }
            
            BufferedImage gray = grayConverter.toGray(originalImage);
            BufferedImage edge = edgeDetector.detect(gray);
            BufferedImage quantized = colorQuantizer.quantize(originalImage, 6); 
            processedImage = cartoonRenderer.render(quantized, edge);
            
            imageLabel.setIcon(new ImageIcon(scaleImage(processedImage, 750, 500)));
        });

        saveBtn.addActionListener(e -> {
            if (processedImage == null) {
                JOptionPane.showMessageDialog(this, "沒有可儲存的處理結果！");
                return;
            }
            JFileChooser chooser = new JFileChooser();
            // 預設儲存檔名帶入今日日期後綴
            chooser.setSelectedFile(new File("cartoon0519.png"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File outFile = chooser.getSelectedFile();
                    if (!outFile.getName().toLowerCase().endsWith(".png")) {
                        outFile = new File(outFile.getAbsolutePath() + ".png");
                    }
                    ImageIO.write(processedImage, "PNG", outFile);
                    JOptionPane.showMessageDialog(this, "儲存成功！");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "圖片儲存失敗！");
                }
            }
        });
    }

    private Image scaleImage(BufferedImage img, int maxWidth, int maxHeight) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        
        double ratio = Math.min((double) maxWidth / imgWidth, (double) maxHeight / imgHeight);
        if (ratio > 1.0) ratio = 1.0;
        
        int newWidth = (int) (imgWidth * ratio);
        int newHeight = (int) (imgHeight * ratio);
        
        return img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}