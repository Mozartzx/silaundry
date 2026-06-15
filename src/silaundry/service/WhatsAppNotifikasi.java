package silaundry.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import silaundry.model.INotifiable;
import silaundry.model.Notifikasi;

// Membuat link template WhatsApp tanpa memakai API atau mengirim pesan otomatis.
public class WhatsAppNotifikasi implements INotifiable {
    private String nomorTujuan;
    private String linkWhatsApp;

    public WhatsAppNotifikasi(String nomorTujuan) {
        this.nomorTujuan = nomorTujuan;
    }

    @Override
    public void kirimNotifikasi(Notifikasi notifikasi) {
        this.linkWhatsApp = generateLinkWhatsApp(notifikasi.getPesan());
    }

    public String generateLinkWhatsApp(String pesan) {
        String nomor = normalisasiNomor(nomorTujuan);
        String encodedMessage = URLEncoder.encode(pesan, StandardCharsets.UTF_8);
        this.linkWhatsApp = "https://wa.me/" + nomor + "?text=" + encodedMessage;
        return linkWhatsApp;
    }

    public String getNomorTujuan() {
        return nomorTujuan;
    }

    public void setNomorTujuan(String nomorTujuan) {
        this.nomorTujuan = nomorTujuan;
    }

    public String getLinkWhatsApp() {
        return linkWhatsApp;
    }

    private String normalisasiNomor(String nomor) {
        String digits = nomor == null ? "" : nomor.replaceAll("\\D+", "");
        if (digits.startsWith("0")) {
            return "62" + digits.substring(1);
        }
        return digits;
    }
}
