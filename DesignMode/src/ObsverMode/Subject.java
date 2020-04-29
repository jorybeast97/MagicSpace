package ObsverMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体类,此类更新会通知观察者们
 */
public class Subject {

    private List<Observer> list = new ArrayList<>();
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        notifyAllList();
    }

    public void add(Observer observer) {
        list.add(observer);
    }

    public void notifyAllList() {
        list.forEach(observer -> {
            observer.update();
        });
    }
}
