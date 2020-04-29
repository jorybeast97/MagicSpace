package ObsverMode;

public class TwoObserver extends Observer {

    public TwoObserver(Subject subject) {
        this.subject = subject;
        this.subject.add(this);
    }

    @Override
    public void update() {
        System.out.println("TwoObserver监听到状态变动,变动的值为" + subject.getState());
    }
}
