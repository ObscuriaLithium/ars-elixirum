package dev.obscuria.elixirum.client.screen.section;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;

import java.util.function.Consumer;

public final class SectionCollection extends AbstractSection {

    public SectionCollection(int center, Consumer<AbstractSection> action) {
        super(center, Type.COLLECTION, action);
    }

    @Override
    public void initTab(ElixirumScreen screen) {}
}
