package medium;

public class PrintABC implements Runnable {

    private String word;

    public static void main(String[] args) throws InterruptedException {

    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.print(word);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public PrintABC(String word) {
        this.word = word;
    }
}
