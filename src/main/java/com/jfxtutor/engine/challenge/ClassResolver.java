package com.jfxtutor.engine.challenge;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Maps short JavaFX class names used in assertion DSL strings to their fully-qualified
 * runtime classes.
 *
 * The hardcoded map covers every control/shape/layout referenced in the 100-lesson
 * curriculum. Fully-qualified names (e.g. {@code javafx.scene.shape.Rectangle}) are
 * passed through directly.
 */
public final class ClassResolver {

    private static final Map<String, String> SHORT_TO_FQN = new HashMap<>();

    static {
        // layouts
        reg("StackPane",     "javafx.scene.layout.StackPane");
        reg("VBox",          "javafx.scene.layout.VBox");
        reg("HBox",          "javafx.scene.layout.HBox");
        reg("BorderPane",    "javafx.scene.layout.BorderPane");
        reg("GridPane",      "javafx.scene.layout.GridPane");
        reg("AnchorPane",    "javafx.scene.layout.AnchorPane");
        reg("FlowPane",      "javafx.scene.layout.FlowPane");
        reg("TilePane",      "javafx.scene.layout.TilePane");
        reg("Pane",          "javafx.scene.layout.Pane");
        reg("Region",        "javafx.scene.layout.Region");
        reg("Group",         "javafx.scene.Group");
        // controls
        reg("Label",         "javafx.scene.control.Label");
        reg("Button",        "javafx.scene.control.Button");
        reg("ToggleButton",  "javafx.scene.control.ToggleButton");
        reg("RadioButton",   "javafx.scene.control.RadioButton");
        reg("CheckBox",      "javafx.scene.control.CheckBox");
        reg("TextField",     "javafx.scene.control.TextField");
        reg("TextArea",      "javafx.scene.control.TextArea");
        reg("PasswordField", "javafx.scene.control.PasswordField");
        reg("ComboBox",      "javafx.scene.control.ComboBox");
        reg("ChoiceBox",     "javafx.scene.control.ChoiceBox");
        reg("ListView",      "javafx.scene.control.ListView");
        reg("TreeView",      "javafx.scene.control.TreeView");
        reg("TableView",     "javafx.scene.control.TableView");
        reg("Slider",        "javafx.scene.control.Slider");
        reg("ProgressBar",   "javafx.scene.control.ProgressBar");
        reg("ProgressIndicator", "javafx.scene.control.ProgressIndicator");
        reg("Spinner",       "javafx.scene.control.Spinner");
        reg("DatePicker",    "javafx.scene.control.DatePicker");
        reg("ColorPicker",   "javafx.scene.control.ColorPicker");
        reg("Hyperlink",     "javafx.scene.control.Hyperlink");
        reg("Pagination",    "javafx.scene.control.Pagination");
        reg("Separator",     "javafx.scene.control.Separator");
        reg("TabPane",       "javafx.scene.control.TabPane");
        reg("Tab",           "javafx.scene.control.Tab");
        reg("Accordion",     "javafx.scene.control.Accordion");
        reg("TitledPane",    "javafx.scene.control.TitledPane");
        reg("ToolBar",       "javafx.scene.control.ToolBar");
        reg("MenuBar",       "javafx.scene.control.MenuBar");
        reg("SplitPane",     "javafx.scene.control.SplitPane");
        reg("ScrollPane",    "javafx.scene.control.ScrollPane");
        reg("WebView",       "javafx.scene.web.WebView");
        // shapes
        reg("Rectangle",     "javafx.scene.shape.Rectangle");
        reg("Circle",        "javafx.scene.shape.Circle");
        reg("Ellipse",       "javafx.scene.shape.Ellipse");
        reg("Line",          "javafx.scene.shape.Line");
        reg("Polygon",       "javafx.scene.shape.Polygon");
        reg("Polyline",      "javafx.scene.shape.Polyline");
        reg("Arc",           "javafx.scene.shape.Arc");
        reg("Path",          "javafx.scene.shape.Path");
        // media / canvas
        reg("Canvas",        "javafx.scene.canvas.Canvas");
        reg("ImageView",     "javafx.scene.image.ImageView");
        reg("MediaView",     "javafx.scene.media.MediaView");
        // text
        reg("Text",          "javafx.scene.text.Text");
        reg("TextFlow",      "javafx.scene.text.TextFlow");
    }

    private static void reg(String shortName, String fqn) {
        SHORT_TO_FQN.put(shortName, fqn);
    }

    private ClassResolver() {}

    /**
     * Resolves {@code name} to a JavaFX {@link Class}.
     *
     * @param name  short name (e.g. {@code "VBox"}) or fully-qualified name
     * @return the resolved class, or empty if unknown
     */
    public static Optional<Class<?>> resolve(String name) {
        String fqn = SHORT_TO_FQN.getOrDefault(name, name);
        try {
            return Optional.of(Class.forName(fqn));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns a suggestion message when {@code name} is not found but a close match exists.
     * Uses simple prefix matching; Levenshtein would over-engineer v1.
     */
    public static String didYouMean(String name) {
        String lower = name.toLowerCase();
        return SHORT_TO_FQN.keySet().stream()
                .filter(k -> k.toLowerCase().contains(lower) || lower.contains(k.toLowerCase()))
                .findFirst()
                .map(k -> "Did you mean '" + k + "'?")
                .orElse("");
    }
}
