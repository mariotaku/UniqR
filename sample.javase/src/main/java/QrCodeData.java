import io.nayuki.qrcodegen.QrCode;
import org.mariotaku.uniqr.QrData;

/**
 * Created by mariotaku on 2017/4/10.
 */
public class QrCodeData implements QrData {
    private QrCode qrCode;

    QrCodeData(QrCode qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public int getSize() {
        return qrCode.size;
    }

    @Override
    public int getVersion() {
        return qrCode.version;
    }

    @Override
    public boolean get(int x, int y) {
        return qrCode.getModule(x, y) == 1;
    }
}
