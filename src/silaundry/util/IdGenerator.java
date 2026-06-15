package silaundry.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

// Membuat ID unik sederhana dengan prefix agar jenis datanya mudah dikenali.
public final class IdGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    private IdGenerator() {
    }

    public static String generate(String prefix) {
        // Timestamp dan nomor urut membantu ID tetap unik sekaligus mudah dibaca.
        int sequence = SEQUENCE.updateAndGet(value -> value >= 99 ? 0 : value + 1);
        return prefix + LocalDateTime.now().format(FORMATTER) + String.format("%02d", sequence);
    }
}
