package com.jfxtutor.ui;

import com.jfxtutor.data.curriculum.CurriculumLoader;
import com.jfxtutor.data.curriculum.Lesson;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LessonNavigator extends VBox {

    private final TreeView<Lesson> tree;
    private final ObjectProperty<Lesson> selectedLesson = new SimpleObjectProperty<>();

    public LessonNavigator() {
        getStyleClass().add("lesson-navigator");
        setId("lessonNavigator");

        Label header = new Label("Curriculum");
        header.getStyleClass().add("nav-header");

        this.tree = new TreeView<>();
        tree.setShowRoot(false);
        tree.getStyleClass().add("lesson-tree");
        VBox.setVgrow(tree, Priority.ALWAYS);

        tree.setCellFactory(tv -> new TreeCell<Lesson>() {
            @Override
            protected void updateItem(Lesson lesson, boolean empty) {
                super.updateItem(lesson, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else if (lesson == null) {
                    // Tier header row — value is null, we stored tier name in the item's tag
                    Object tag = getTreeItem() != null ? getTreeItem().getGraphic() : null;
                    String tier = tag instanceof Label ? ((Label) tag).getText() : "";
                    setText(capitalize(tier));
                    setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #888;");
                } else {
                    setText(String.format("%03d — %s", lesson.meta.order, lesson.meta.title));
                    setStyle("");
                }
            }
        });

        tree.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null && sel.getValue() != null) {
                selectedLesson.set(sel.getValue());
            }
        });

        getChildren().addAll(header, tree);
        populate();
    }

    private void populate() {
        List<Lesson> lessons = CurriculumLoader.loadAll();

        TreeItem<Lesson> root = new TreeItem<>();

        Map<String, TreeItem<Lesson>> tiers = new LinkedHashMap<>();
        for (Lesson lesson : lessons) {
            String tier = lesson.meta.tier;
            tiers.computeIfAbsent(tier, t -> {
                // Use a Label as the graphic so the cell factory can read the tier name.
                TreeItem<Lesson> tierItem = new TreeItem<>(null, new Label(t));
                tierItem.setExpanded(true);
                root.getChildren().add(tierItem);
                return tierItem;
            });
            tiers.get(tier).getChildren().add(new TreeItem<>(lesson));
        }

        tree.setRoot(root);

        if (!lessons.isEmpty()) {
            TreeItem<Lesson> firstTier = root.getChildren().isEmpty() ? null : root.getChildren().get(0);
            if (firstTier != null && !firstTier.getChildren().isEmpty()) {
                tree.getSelectionModel().select(firstTier.getChildren().get(0));
            }
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public ObjectProperty<Lesson> selectedLessonProperty() { return selectedLesson; }
    public Lesson getSelectedLesson() { return selectedLesson.get(); }
}
