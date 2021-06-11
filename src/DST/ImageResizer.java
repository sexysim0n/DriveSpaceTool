package DST;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Methoden um Bilder zu verändern
 *
 * @author simon
 */
public class ImageResizer {

    /**
     * Originalbild und Verändertes Bild
     */
    private final File originalImg, resizedImg;
    /**
     * Zielbreite und -höhe
     */
    private final int targetWidth, targetHeight;
    /**
     * Format der Bilder
     */
    private final String format;

    public ImageResizer(String original, String resized, int newWidth, int newHeight, String format) {
        originalImg = new File(original);
        resizedImg = new File(resized);
        targetWidth = newWidth;
        targetHeight = newHeight;
        this.format = format;
    }

    /**
     * Verändert die Breite und Höhe eines Bilds
     *
     * @return Ob es funktioniert hat: "Successful!" oder "Failed!"
     * @throws IOException throws IOException
     */
    public String resizeImg() throws IOException {
        BufferedImage original = ImageIO.read(originalImg);
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, original.getType());
        Graphics2D g = resized.createGraphics();
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        ImageIO.write(resized, format, resizedImg);
        return resizedImg.exists() ? "Successful!" : "Failed!";
    }

    /**
     * Löscht die Originaldatei
     */
    public void deleteOriginal() {
        originalImg.delete();
    }

}
