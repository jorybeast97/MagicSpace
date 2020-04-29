package ObsverMode;

public class Test {

    public static void main(String[] args) {
        Subject subject = new Subject();
        new OneObserver(subject);
        new TwoObserver(subject);


        try {
            subject.setState(1);
            Thread.sleep(1000);
            subject.setState(2);
            Thread.sleep(1000);
            subject.setState(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
