package ObsverMode;

public class OneObserver extends Observer {

    public OneObserver(Subject subject) {
        this.subject = subject;
        this.subject.add(this);
    }

    @Override
    public void update() {
        System.out.println("OneObserver监听到状态变动,变动的值为" + subject.getState());
    }
}
