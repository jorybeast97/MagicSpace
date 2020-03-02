package basicKnowledge;


import java.nio.ByteBuffer;

public class TestIO {

    public static void main(String[] args) {
        TestIO testIO = new TestIO();
        testIO.getBuffer();
    }

    public void getBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("hello".getBytes());
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        System.out.println(byteBuffer.position());
        System.out.println(new String(bytes, 0, bytes.length));
    }
}
