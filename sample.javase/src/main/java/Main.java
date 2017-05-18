import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import hu.kazocsaba.imageviewer.ImageViewer;
import hu.kazocsaba.imageviewer.ResizeStrategy;
import org.mariotaku.uniqr.JavaSEPlatform;
import org.mariotaku.uniqr.UniqR;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mariotaku on 2017/4/9.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        final BufferedImage background = ImageIO.read(Main.class.getResource("nyan_sakamoto.png"));

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.QR_VERSION, 5);
        QRCode qrCode = Encoder.encode("Hello world, UniqR!", ErrorCorrectionLevel.H, hints);
        UniqR<BufferedImage> uniqR = new UniqR<>(new JavaSEPlatform(), background, new QrCodeData(qrCode));
        uniqR.setQrPatternColor(0xFF003366);
        showImage(uniqR.build().produceResult(), "Image");
    }

    static void showImage(BufferedImage image, String title) {
        final ImageViewer imageViewer = new ImageViewer(image);
        imageViewer.setPixelatedZoom(true);
        imageViewer.setResizeStrategy(ResizeStrategy.RESIZE_TO_FIT);
        final JFrame frame = new JFrame();
        frame.setTitle(title);
        frame.add(imageViewer.getComponent());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);
    }
}
