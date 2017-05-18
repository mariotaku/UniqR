import com.google.zxing.qrcode.encoder.QRCode;
import org.mariotaku.uniqr.QrData;

/**
 * Created by mariotaku on 2017/4/10.
 */
public class QrCodeData implements QrData {
    private QRCode qrCode;

    QrCodeData(QRCode qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public int getSize() {
        return qrCode.getVersion().getDimensionForVersion();
    }

    @Override
    public int getVersion() {
        return qrCode.getVersion().getVersionNumber();
    }

    @Override
    public boolean get(int x, int y) {
        return qrCode.getMatrix().get(x, y) == 1;
    }
}
