package appcustomcontrol;

/**
 * This interface can be overridden by button that can wipe and reset the options toolbar with
 * their own specific controls.
 */
public interface OptionToolbarSettable {

    void setCurrentToolbarOptions( DrawableButtonTool tool );

}
