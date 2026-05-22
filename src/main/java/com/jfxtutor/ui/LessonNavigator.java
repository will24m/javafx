package com.jfxtutor.ui;

import com.jfxtutor.data.curriculum.CurriculumLoader;
import com.jfxtutor.data.curriculum.Lesson;
import com.jfxtutor.data.progress.ProgressStore;
import com.jfxtutor.util.AppLog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Duration;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Left-side curriculum browser.
 *
 * The navigator loads every Lesson once, groups lessons by their tier, and
 * exposes the selected lesson through selectedLessonProperty(). MainView listens
 * to that property and updates the rest of the application.
 */
public class LessonNavigator extends VBox {

    private final TreeView<Lesson> tree;
    private final TextField search;
    private final ObjectProperty<Lesson> selectedLesson = new SimpleObjectProperty<>();
    private List<Lesson> allLessons = List.of();
    private ProgressStore progressStore;

    public LessonNavigator() {
        AppLog.info("navigator", "Creating lesson navigator and search field.");
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

        // The TreeView uses null values for tier/group rows and real Lesson
        // values for leaf rows. The custom cell renderer turns that convention
        // into friendly labels and tooltips.
        tree.setCellFactory(tv -> new TreeCell<Lesson>() {
            @Override
            protected void updateItem(Lesson lesson, boolean empty) {
                super.updateItem(lesson, empty);
                getStyleClass().removeAll("tier-cell", "lesson-cell");
                setTextOverrun(OverrunStyle.ELLIPSIS);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                    setTooltip(null);
                } else if (lesson == null) {
                    Object tag = getTreeItem() != null ? getTreeItem().getGraphic() : null;
                    String tier = tag instanceof Label lbl ? lbl.getText() : "";
                    int count = getTreeItem() == null ? 0 : getTreeItem().getChildren().size();
                    setText(capitalize(tier).toUpperCase(Locale.ROOT) + "   " + count);
                    setGraphic(null);
                    setTooltip(null);
                    getStyleClass().add("tier-cell");
                } else {
                    boolean done = progressStore != null
                            && progressStore.isLessonCompleted(lesson.meta.id);
                    String badge = done ? " ✓" : "";
                    String label = String.format("%03d   %s%s",
                            lesson.meta.order, lesson.meta.title, badge);
                    setText(label);
                    setGraphic(null);
                    Tooltip tip = new Tooltip(lesson.meta.title
                            + "\n" + lesson.meta.id
                            + "  ·  " + lesson.meta.estimatedMinutes + " min");
                    tip.setShowDelay(Duration.millis(400));
                    setTooltip(tip);
                    getStyleClass().add("lesson-cell");
                }
            }
        });

        // Only leaf rows represent actual lessons. Selecting a tier heading
        // should not change the current lesson, so we guard on getValue().
        tree.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null && sel.getValue() != null) {
                selectedLesson.set(sel.getValue());
            }
        });

        getChildren().addAll(header, searchBox, tree);
        populate();
    }

    private void populate() {
        AppLog.info("navigator", "Loading curriculum into the navigator tree.");
        this.allLessons = CurriculumLoader.loadAll();
        rebuildTree(allLessons, null);
        if (!allLessons.isEmpty()) {
            AppLog.info("navigator", "Selecting the first lesson so the app opens with content.");
            selectFirstLeaf();
        }
    }

    private void rebuildTree(List<Lesson> lessons, Lesson preserveSelection) {
        // Rebuild from scratch on search changes. The lesson list is small, and
        // rebuilding keeps filtering logic simpler than mutating TreeItems in place.
        AppLog.info("navigator", "Rebuilding curriculum tree with " + lessons.size() + " visible lesson(s).");
        TreeItem<Lesson> root = new TreeItem<>();
        Map<String, TreeItem<Lesson>> tiers = new LinkedHashMap<>();
        for (Lesson lesson : lessons) {
            String tier = lesson.meta.tier;
            // LinkedHashMap preserves first-seen tier order, which follows the
            // globally sorted curriculum order.
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
            AppLog.info("navigator", "Search cleared; restoring the full curriculum tree.");
            rebuildTree(allLessons, getSelectedLesson());
            return;
        }
        String needle = query.toLowerCase(Locale.ROOT).trim();
        AppLog.info("navigator", "Filtering lessons for search text: \"" + needle + "\".");
        List<Lesson> filtered = new ArrayList<>();
        for (Lesson lesson : allLessons) {
            if (lesson.meta.title.toLowerCase(Locale.ROOT).contains(needle)
                    || lesson.meta.id.toLowerCase(Locale.ROOT).contains(needle)
                    || lesson.meta.tier.toLowerCase(Locale.ROOT).contains(needle)) {
                filtered.add(lesson);
            }
        }
        rebuildTree(filtered, null);
        if (!filtered.isEmpty()) {
            AppLog.info("navigator", "Search matched " + filtered.size() + " lesson(s); selecting first match.");
            selectFirstLeaf();
        } else {
            AppLog.info("navigator", "Search returned no lesson matches.");
        }
    }

    private void selectFirstLeaf() {
        // The first visible leaf is the earliest lesson in the current tree
        // because allLessons is already sorted by LessonFrontmatter.order.
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

    /** Provides progress state so the cell factory can show completion badges. */
    public void setProgressStore(ProgressStore store) {
        this.progressStore = store;
        tree.refresh();
    }

    /** Re-renders all cells so newly earned completion badges appear immediately. */
    public void refreshBadges() {
        tree.refresh();
    }

    /** Returns the lesson with {@code id}, or {@code null} if not found. */
    public Lesson findById(String id) {
        if (id == null) return null;
        return allLessons.stream()
                .filter(l -> id.equals(l.meta.id))
                .findFirst().orElse(null);
    }

    /**
     * Selects {@code lesson} in the tree, expanding its tier if needed.
     * No-op if the lesson is not currently visible (e.g. filtered out).
     */
    public void selectLesson(Lesson lesson) {
        if (lesson == null) return;
        TreeItem<Lesson> root = tree.getRoot();
        if (root == null) return;
        for (TreeItem<Lesson> tier : root.getChildren()) {
            for (TreeItem<Lesson> leaf : tier.getChildren()) {
                if (leaf.getValue() != null
                        && leaf.getValue().meta.id.equals(lesson.meta.id)) {
                    tier.setExpanded(true);
                    tree.getSelectionModel().select(leaf);
                    int idx = tree.getRow(leaf);
                    if (idx >= 0) tree.scrollTo(idx);
                    return;
                }
            }
        }
    }

    public ObjectProperty<Lesson> selectedLessonProperty() { return selectedLesson; }
    public Lesson getSelectedLesson() { return selectedLesson.get(); }

    public int getLessonCount() { return allLessons.size(); }

    /** Focus the search field — invoked by the ⌘+F accelerator. */
    public void focusSearch() {
        search.requestFocus();
        search.selectAll();
    }
}
