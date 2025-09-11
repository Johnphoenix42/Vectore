package appcustomcontrol;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Optional;

public class FontManager {

    public static FontPosture posture = FontPosture.REGULAR;
    public static FontWeight weight = FontWeight.LIGHT;
    double size;
    FontWeight fontWeight;
    FontPosture fontPosture;
    String family;

    FontManager(String family, FontWeight fontWeight, FontPosture fontPosture, Double size){
        this.family = family;
        this.fontWeight = fontWeight;
        this.fontPosture = fontPosture;
        this.size = size;
    }

    static Font recreateFont(Font baseFont, FontManager newFont) {
        Optional<Double> size = Optional.of(newFont.size);
        Optional<FontWeight> fontWeight = Optional.ofNullable(newFont.fontWeight);
        Optional<FontPosture> fontPosture = Optional.ofNullable(newFont.fontPosture);
        Optional<String> familyOptional = Optional.ofNullable(newFont.family);
        return Font.font(
                familyOptional.orElse(baseFont.getFamily()),
                fontWeight.orElse(FontManager.weight),
                fontPosture.orElse(FontManager.posture),
                size.filter(value -> value > 0).orElse(baseFont.getSize())
        );
    }

}
