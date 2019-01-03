import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class LoopBackTimeStamp {
    private long sendTs;
    private long recvTs;

    public LoopBackTimeStamp() {
        this.sendTs = System.nanoTime();
    }

    public long delayInNanoSecond() {
        return recvTs - sendTs;
    }

    /**
     * Transfer 2 long number to a 16 byte-long byte[], every 8 bytes represent a long number.
     *
     * @return
     */
    public byte[] toByteArray() {

        final int byteOfLong = Long.SIZE / Byte.SIZE;
        byte[] ba = new byte[byteOfLong * 2];
        byte[] t1 = ByteBuffer.allocate(byteOfLong).putLong(sendTs).array();
        byte[] t2 = ByteBuffer.allocate(byteOfLong).putLong(recvTs).array();

        for (int i = 0; i < byteOfLong; i++) {
            ba[i] = t1[i];
        }

        for (int i = 0; i < byteOfLong; i++) {
            ba[i + byteOfLong] = t2[i];
        }
        return ba;
    }

    /**
     * Transfer a 16 byte-long byte[] to 2 long numbers, every 8 bytes represent a long number.
     *
     * @param content
     */
    public void fromByteArray(byte[] content) {
        int len = content.length;
        final int byteOfLong = Long.SIZE / Byte.SIZE;
        if (len != byteOfLong * 2) {
            System.out.println("Error on content length");
            return;
        }
        ByteBuffer buf1 = ByteBuffer.allocate(byteOfLong).put(content, 0, byteOfLong);
        ByteBuffer buf2 = ByteBuffer.allocate(byteOfLong).put(content, byteOfLong, byteOfLong);
        buf1.rewind();
        buf2.rewind();
        this.sendTs = buf1.getLong();
        this.recvTs = buf2.getLong();
    }
}
