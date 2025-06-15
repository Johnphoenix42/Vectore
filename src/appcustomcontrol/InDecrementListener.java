package appcustomcontrol;

import javafx.scene.control.SpinnerValueFactory;

public interface InDecrementListener<T> {

    public void onInDecrement(SpinnerValueFactory<T> valueFactory);
}
