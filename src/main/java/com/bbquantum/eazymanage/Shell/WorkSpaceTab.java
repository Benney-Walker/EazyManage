package com.bbquantum.eazymanage.Shell;

import javafx.scene.Parent;

import java.util.function.Supplier;

public class WorkSpaceTab {

    private final String id;

    private final String title;

    private final Supplier<Parent> contentLoader;

    private final boolean closable;

    private Parent cachedContent;

    public WorkSpaceTab(String id, String title, Supplier<Parent> contentLoader, boolean closable) {
        this.id = id;
        this.title = title;
        this.contentLoader = contentLoader;
        this.closable = closable;
    }

    public WorkSpaceTab(String id, String title, Supplier<Parent> contentLoader) {
        this(id, title, contentLoader, true);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isClosable() {
        return closable;
    }

    /** Materialize the content on first call, return the cached node thereafter. */
    public Parent content() {
        if (cachedContent == null) cachedContent = contentLoader.get();
        return cachedContent;
    }

    /** Drop the cached content so the GC can reclaim it after the tab closes. */
    public void dispose() {
        cachedContent = null;
    }
}
