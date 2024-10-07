package com.hogemann.bsky2rss.bsky;

import com.hogemann.bsky2rss.Result;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageService {

    /**
     * Resize an image to fit within the specified dimensions and compress it to fit within the specified size.
     *
     * @param image the image to resize
     * @param maxWidth the maximum width of the resized image
     * @param maxHeight the maximum height of the resized image
     * @param maxSize the maximum size of the compressed image
     * @return the resized and compressed image
     */
    public static Result<byte[]> resize(byte[] image, int maxWidth, int maxHeight, int maxSize) {
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(image));

            // Convert the image to RGB color space if it's not already
            BufferedImage rgbImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
            op.filter(original, rgbImage);

            // Use rgbImage instead of original for further processing
            int width = rgbImage.getWidth();
            int height = rgbImage.getHeight();
            if (width <= maxWidth && height <= maxHeight && image.length <= maxSize) {
                return Result.ok(image);
            }

            double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
            int newWidth = (int) (width * scale);
            int newHeight = (int) (height * scale);
            BufferedImage resized = new BufferedImage(newWidth, newHeight, rgbImage.getType());
            Graphics2D g = resized.createGraphics();
            g.drawImage(rgbImage, 0, 0, newWidth, newHeight, null);
            g.dispose();
            try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(resized, "jpg", out);
                byte[] compressed = compressImage(resized, maxSize);
                return Result.ok(compressed);
            }
        } catch (IOException e) {
            return Result.error(e);
        }
    }

    private static byte[] compressImage(BufferedImage image, long targetSize) throws IOException {
        float quality = 1.0f;
        byte[] imageData = null;

        while (quality > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }

            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
            ios.close();

            imageData = baos.toByteArray();
            if (imageData.length <= targetSize) {
                break;
            }
            quality -= 0.05f; // Decrease quality for next iteration
        }

        return imageData;
    }

}
