import hu.kazocsaba.imageviewer.ImageViewer;
import hu.kazocsaba.imageviewer.ResizeStrategy;
import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrSegment;
import org.mariotaku.uniqr.JavaSEPlatform;
import org.mariotaku.uniqr.UniqR;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by mariotaku on 2017/4/9.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        final BufferedImage background = ImageIO.read(new File("nyan_sakamoto.png"));
        final List<QrSegment> qrSegments = QrSegment.makeSegments("Hello world, UniqR!");
        final QrCode qrCode = QrCode.encodeSegments(qrSegments, QrCode.Ecc.HIGH, 5, 40, -1, true);

        UniqR<BufferedImage> uniqR = new UniqR<>(new JavaSEPlatform(), background, new QrCodeData(qrCode));
        uniqR.setQrPatternColor(0xFF003366);
        uniqR.setScale(4);
        uniqR.setDotSize(2);
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
