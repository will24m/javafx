package com.jfxtutor.ui;

import com.jfxtutor.data.curriculum.CurriculumLoader;
import com.jfxtutor.data.curriculum.Lesson;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LessonNavigator extends VBox {

    private final TreeView<Lesson> tree;
    private final TextField search;
    private final ObjectProperty<Lesson> selectedLesson = new SimpleObjectProperty<>();
    private List<Lesson> allLessons = List.of();

    public LessonNavigator() {
        getStyleClass().add("lesson-navigator");
        setId("lessonNavigator");

        Label header = new Label("Curriculum");
        header.getStyleClass().add("nav-header");

        this.search = new TextField();
        search.setPromptText("Search lessons…");
        search.getStyleClass().add("nav-search");
        VBox searchBox = new VBox(search);
        searchBox.setPadding(new Insets(0, 10, 8, 10));
        search.textProperty().addListener((obs, old, val) -> applyFilter(val));

        this.tree = new TreeView<>();
        tree.setShowRoot(false);
        tree.getStyleClass().add("lesson-tree");
        VBox.setVgrow(tree, Priority.ALWAYS);

        tree.setCellFactory(tv -> new TreeCell<Lesson>() {
            @Override
            protected void updateItem(Lesson lesson, boolean empty) {
                super.updateItem(lesson, empty);
                getStyleClass().removeAll("tier-cell", "lesson-cell");
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (lesson == null) {
                    Object tag = getTreeItem() != null ? getTreeItem().getGraphic() : null;
                    String tier = tag instanceof Label lbl ? lbl.getText() : "";
                    int count = getTreeItem() == null ? 0 : getTreeItem().getChildren().size();
                    setText(capitalize(tier).toUpperCase(Locale.ROOT) + "   " + count);
                    setGraphic(null);
                    getStyleClass().add("tier-cell");
                } else {
                    setText(String.format("%03d   %s", lesson.meta.order, lesson.meta.title));
                    setGraphic(null);
                    getStyleClass().add("lesson-cell");
                }
            }
        });

        tree.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null && sel.getValue() != null) {
                selectedLesson.set(sel.getValue());
            }
        });

        getChildren().addAll(header, searchBox, tree);
        populate();
    }

    private void populate() {
        this.allLessons = CurriculumLoader.loadAll();
        rebuildTree(allLessons, null);
        if (!allLessons.isEmpty()) {
            selectFirstLeaf();
        }
    }

    private void rebuildTree(List<Lesson> lessons, Lesson preserveSelection) {
        TreeItem<Lesson> root = new TreeItem<>();
        Map<String, TreeItem<Lesson>> tiers = new LinkedHashMap<>();
        for (Lesson lesson : lessons) {
            String tier = lesson.meta.tier;
            tiers.computeIfAbsent(tier, t -> {
                TreeItem<Lesson> tierItem = new TreeItem<>(null, new Label(t));
                tierItem.setExpanded(true);
                root.getChildren().add(tierItem);
                return tierItem;
            });
            tiers.get(tier).getChildren().add(new TreeItem<>(lesson));
        }
        tree.setRoot(root);

        if (preserveSelection != null) {
            for (TreeItem<Lesson> tier : root.getChildren()) {
                for (TreeItem<Lesson> leaf : tier.getChildren()) {
                    if (leaf.getValue() != null
                            && leaf.getValue().meta.id.equals(preserveSelection.meta.id)) {
                        tree.getSelectionModel().select(leaf);
                        return;
                    }
                }
            }
        }
    }

    private void applyFilter(String query) {
        if (query == null || query.isBlank()) {
            rebuildTree(allLessons, getSelectedLesson());
            return;
        }
        String needle = query.toLowerCase(Locale.ROOT).trim();
        List<Lesson> filtered = new ArrayList<>();
        for (Lesson lesson : allLessons) {
            if (lesson.meta.title.toLowerCase(Locale.ROOT).contains(needle)
                    || lesson.meta.id.toLowerCase(Locale.ROOT).contains(needle)
                    || lesson.meta.tier.toLowerCase(Locale.ROOT).contains(needle)) {
                filtered.add(lesson);
            }
        }
        rebuildTree(filtered, null);
        if (!filtered.isEmpty()) selectFirstLeaf();
    }

    private void selectFirstLeaf() {
        TreeItem<Lesson> root = tree.getRoot();
        if (root == null || root.getChildren().isEmpty()) return;
        TreeItem<Lesson> firstTier = root.getChildren().get(0);
        if (!firstTier.getChildren().isEmpty()) {
            tree.getSelectionModel().select(firstTier.getChildren().get(0));
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public ObjectProperty<Lesson> selectedLessonProperty() { return selectedLesson; }
    public Lesson getSelectedLesson() { return selectedLesson.get(); }

    public int getLessonCount() { return allLessons.size(); }
}
