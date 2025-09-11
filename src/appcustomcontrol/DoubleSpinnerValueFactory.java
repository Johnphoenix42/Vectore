package appcustomcontrol;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

public class DoubleSpinnerValueFactory extends SpinnerValueFactory<Double> {
    private final Spinner<Double> numberSpinner;
    private final InDecrementListener<Double> inDecrementListener;
    private static boolean isIncremented;

    public DoubleSpinnerValueFactory(Spinner<Double> numberSpinner, InDecrementListener<Double> inDecrementListener) {
        this.numberSpinner = numberSpinner;
        this.inDecrementListener = inDecrementListener;
        setValue(10.0);
    }

    @Override
    public void decrement(int steps) {
        SpinnerValueFactory<Double> valueFactory = numberSpinner.getValueFactory();
        if (valueFactory == null) {
            throw new IllegalStateException("Can't decrement Spinner with a null SpinnerValueFactory");
        }
        commitEditorText(valueFactory);
        Double oldValue = valueFactory.getValue();
        valueFactory.setValue((oldValue != null ? oldValue : 0) - steps);
        isIncremented = false;

        inDecrementListener.onInDecrement(valueFactory);
    }

    @Override
    public void increment(int steps) {
        SpinnerValueFactory<Double> valueFactory = numberSpinner.getValueFactory();
        if (valueFactory == null) {
            throw new IllegalStateException("Can't decrement Spinner with a null SpinnerValueFactory");
        }
        commitEditorText(valueFactory);
        Double oldValue = valueFactory.getValue();
        valueFactory.setValue(steps + (oldValue != null ? oldValue : 0));
        isIncremented = true;

        inDecrementListener.onInDecrement(valueFactory);
    }

    private void commitEditorText(SpinnerValueFactory<Double> valueFactory){
        String text = numberSpinner.getEditor().getText();
        if (valueFactory != null) {
            StringConverter<Double> converter = valueFactory.getConverter();
            if (converter == null) return;
            Double value = converter.fromString(text);
            valueFactory.setValue(value);
        }
    }

    static boolean isIncremented() {
        return isIncremented;
    }
}
