package dev.obscuria.elixirum.client.screen.section.compendium;

import dev.obscuria.elixirum.client.screen.container.ListContainer;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public enum ContentsType {
    INTRODUCTION(ContentsType::buildIntroduction),
    ESSENCES_AND_INGREDIENTS(ContentsType::buildEssencesAndIngredients),
    CREATING_ELIXIR(ContentsType::buildCreatingElixir),
    ALL_ESSENCES(ContentsType::buildAllEssences),
    DISCOVERING_ESSENCES(ContentsType::buildDiscoveringEssences),
    AFFIXES(ContentsType::buildAffixes),
    CONTRIBUTION(ContentsType::buildContribution);

    private final PageBuilder builder;

    ContentsType(PageBuilder builder) {
        this.builder = builder;
    }

    public void buildPage(ListContainer page) {
        this.builder.build(page);
    }

    public String getDescriptionId() {
        return "elixirum.compendium." + toString().toLowerCase();
    }

    public Component getDisplayName() {
        return Component.translatable(getDescriptionId());
    }

    private String getSubId(String name) {
        return getDescriptionId() + "." + name;
    }

    private static void buildIntroduction(ListContainer page) {
        page.addChild(new SubLogo());
        page.addChild(new SubText(Component.translatable(INTRODUCTION.getSubId("text_1"))));
        page.addChild(new SubNote(Component.translatable(INTRODUCTION.getSubId("note_1"))));
    }

    private static void buildEssencesAndIngredients(ListContainer page) {
        page.addChild(new SubText(Component.translatable(ESSENCES_AND_INGREDIENTS.getSubId("text_1"))));
        page.addChild(new SubNote(Component.translatable(ESSENCES_AND_INGREDIENTS.getSubId("note_1"))));
        page.addChild(new SubText(Component.translatable(ESSENCES_AND_INGREDIENTS.getSubId("text_2"))));
    }

    private static void buildCreatingElixir(ListContainer page) {
        page.addChild(new SubText(Component.translatable(CREATING_ELIXIR.getSubId("text_1"))));
        final var steps = page.addChild(new ListContainer().setSeparation(2));
        steps.addChild(new SubStepItem(Items.CAULDRON, Component.translatable(CREATING_ELIXIR.getSubId("step_1"))));
        steps.addChild(new SubStepItem(Items.WATER_BUCKET, Component.translatable(CREATING_ELIXIR.getSubId("step_2"))));
        steps.addChild(new SubStepItem(Items.APPLE, Component.translatable(CREATING_ELIXIR.getSubId("step_3"))));
        steps.addChild(new SubStepItem(Items.GLASS_BOTTLE, Component.translatable(CREATING_ELIXIR.getSubId("step_4"))));
        steps.addChild(new SubStepItem(Items.BUCKET, Component.translatable(CREATING_ELIXIR.getSubId("step_5"))));
        page.addChild(new SubText(Component.translatable(CREATING_ELIXIR.getSubId("text_2"))));
    }

    private static void buildContribution(ListContainer page) {
        page.addChild(new SubLogo());
        page.addChild(new SubText(Component.literal("Ars Elixirum was created in just three weeks for the CurseForge modjam. As it is a very complex mod, some planned content had to be postponed due to the deadline. However, the mod will be receiving frequent and substantial content updates soon, and your participation, feedback, and ideas can help shape the future of Ars Elixirum! Join our Discord server (button at the end of the page) to share your thoughts and stay updated on the latest developments.")));
        page.addChild(new SubText(Component.literal("Here are some features we are prioritizing for upcoming updates:")));
        final var steps = page.addChild(new ListContainer().setSeparation(2));
        steps.addChild(new SubStepItem(ElixirumItems.GLASS_CAULDRON.asItem(), Component.literal("Catastrophes and Reactions: The contents of your cauldron won't always be stable during brewing. Expect dangerous reactions that will force you to carefully plan your alchemical lab to create truly powerful elixirs!")));
        steps.addChild(new SubStepItem(Items.PAPER, Component.literal("Ancient Recipes: Unearth ancient elixir recipes as treasures, offering unique effects that cannot be achieved through other means.")));
        steps.addChild(new SubStepItem(Items.PAPER, Component.literal("Global Spells: Find fragments of ancient scrolls to brew spells that temporarily alter the entire world! ")));
        steps.addChild(new SubStepItem(Items.WRITABLE_BOOK, Component.literal("Personalized Recipe Books: Combine recipes from your collection into recipe book items that can be shared with other players.")));
        steps.addChild(new SubStepItem(ElixirumItems.ELIXIR.asItem(), Component.literal("More Elixir Effects for Every Situation: Expand your potion repertoire with a wider variety of effects.")));
        steps.addChild(new SubStepItem(ElixirumItems.WITCH_TOTEM_OF_UNDYING.asItem(), Component.literal("Versatile Elixir Applications: Explore new ways to utilize elixirs, tailored to different situations and environments.")));
        page.addChild(new SubText(Component.literal("We look forward to your feedback and contributions as we continue to develop Ars Elixirum.")));
        page.addChild(new DiscordButton());
    }

    private static void buildAllEssences(ListContainer page) {
        final var lookup = Optional.ofNullable(Minecraft.getInstance().level)
                .map(level -> level.holderLookup(ElixirumRegistries.ESSENCE))
                .orElse(null);
        if (lookup != null) {
            final var index = new AtomicInteger(1);
            final var light = new AtomicBoolean(true);
            final var list = page.addChild(new ListContainer());
            lookup.listElements().forEach(essence -> {
                final var actualIndex = index.getAndIncrement();
                final var actualLight = light.getAndSet(!light.get());
                list.addChild(new SubEssence(essence, actualIndex, actualLight));
            });
        }
    }

    private static void buildDiscoveringEssences(ListContainer page) {
        page.addChild(new SubText(Component.translatable(DISCOVERING_ESSENCES.getSubId("text_1"))));
        final var steps = page.addChild(new ListContainer().setSeparation(2));
        steps.addChild(new SubStepItem(Items.BLAZE_POWDER, Component.translatable(DISCOVERING_ESSENCES.getSubId("step_1"))));
        steps.addChild(new SubStepItem(Items.GLASS_BOTTLE, Component.translatable(DISCOVERING_ESSENCES.getSubId("step_2"))));
        steps.addChild(new SubStepItem(Items.APPLE, Component.translatable(DISCOVERING_ESSENCES.getSubId("step_3"))));
        page.addChild(new SubText(Component.translatable(DISCOVERING_ESSENCES.getSubId("text_2"))));
        page.addChild(new SubNote(Component.translatable(DISCOVERING_ESSENCES.getSubId("note_1"))));
    }

    private static void buildAffixes(ListContainer page) {
        page.addChild(new SubText(Component.translatable(AFFIXES.getSubId("text_1"))));
        page.addChild(new SubNote(Component.translatable(AFFIXES.getSubId("note_1"))));
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(INTRODUCTION.getDescriptionId(), "Introduction");
        consumer.accept(INTRODUCTION.getSubId("text_1"), "Ars Elixirum is the ultimate extension of potion crafting, applications, and diversity. Through exploration and experimentation, you can create your own recipes to craft Elixirs - powerful replacements for potions - and save and customize them.");
        consumer.accept(INTRODUCTION.getSubId("note_1"), "While you can still find ordinary potions as rare loot, crafting them the conventional way is no longer possible.");

        consumer.accept(ESSENCES_AND_INGREDIENTS.getDescriptionId(), "Essences & Ingredients");
        consumer.accept(ESSENCES_AND_INGREDIENTS.getSubId("text_1"), "Any item can be used as an ingredient for an elixir, provided it contains any essences. Essences are effects in their raw form. Each essence in an ingredient has a weight value, and the sum of the weight of identical essences in the finished elixir determines the amplifier and duration of the final effect.");
        consumer.accept(ESSENCES_AND_INGREDIENTS.getSubId("note_1"), "To determine if an item contains any essences without having to try to toss it into the cauldron, you can use the Alchemist Eye. If the item's description displays a section with alchemical properties, then that item can become an ingredient!");
        consumer.accept(ESSENCES_AND_INGREDIENTS.getSubId("text_2"), "Elixir Ingredient Tips:\n - You can only use up to 9 ingredients in an elixir.\n - Don't bother adding the same ingredient twice! It won't do anything.");

        consumer.accept(CREATING_ELIXIR.getDescriptionId(), "Creating Elixir");
        consumer.accept(CREATING_ELIXIR.getSubId("text_1"), "To craft your first elixir, you will only need a Glass Cauldron, a Bucket of Water, a few unique ingredients and a Glass Bottle.");
        consumer.accept(CREATING_ELIXIR.getSubId("step_1"), "Place the Glass Cauldron over a heat source (campfire, magma, etc.).");
        consumer.accept(CREATING_ELIXIR.getSubId("step_2"), "Fill the cauldron with water and wait for it to boil completely.");
        consumer.accept(CREATING_ELIXIR.getSubId("step_3"), "Add your ingredients one by one. The order of adding ingredients is important and affects the properties of the elixir, but since you haven't discovered the properties of the ingredients yet, you won't be able to get the most out of them, so just add them in a random order.");
        consumer.accept(CREATING_ELIXIR.getSubId("step_4"), "Scoop the elixir into an empty bottle and use it. You might get something useful or poisonous, if you're lucky enough to use a good set of ingredients.");
        consumer.accept(CREATING_ELIXIR.getSubId("step_5"), "If you want to empty the cauldron without creating an elixir, simply use the empty bucket on it.");
        consumer.accept(CREATING_ELIXIR.getSubId("text_2"), "So, you've crafted your first elixir, and most likely it has a brownish color and some weak or pale effects. This is just the beginning of your alchemist's journey, and to brew truly legendary elixirs, you need to discover the properties of the ingredients and develop the best sequence based on their properties. Unleash the true power of magical alchemy upon this world!");

        consumer.accept(ALL_ESSENCES.getDescriptionId(), "All Essences");

        consumer.accept(DISCOVERING_ESSENCES.getDescriptionId(), "Discovering Essences");
        consumer.accept(DISCOVERING_ESSENCES.getSubId("text_1"), "By default, when wearing the Alchemist Eye, you'll only be able to see if essences are present in an ingredient, not the exact list of essences and their weights. To thoroughly examine an ingredient's properties, you'll need a Brewing Stand, the ingredient to discover, Blaze Powder, and a few Glass Bottles.");
        consumer.accept(DISCOVERING_ESSENCES.getSubId("step_1"), "Place the Blaze Powder in the Brewing Stand as fuel.");
        consumer.accept(DISCOVERING_ESSENCES.getSubId("step_2"), "Place three Bottles, which will serve as containers for the essences.");
        consumer.accept(DISCOVERING_ESSENCES.getSubId("step_3"), "Finally, place the ingredient and wait for the brewing process to complete.");
        consumer.accept(DISCOVERING_ESSENCES.getSubId("text_2"), "Once brewing is finished, the ingredient will be broken down into its constituent essences. If the ingredient had more than 3 essences, you'll only receive 3 random ones. After collecting the essence bottles from the brewing stand, the ingredient's description will change to display the exact weight of those essences in the future – allowing you to make more informed decisions when choosing ingredients for your elixirs!");
        consumer.accept(DISCOVERING_ESSENCES.getSubId("note_1"), "If after the above process, the ingredient description still shows '???' at the end of the list, it means there are still essences in the ingredient that you haven't discovered, and you should repeat the process.");

        consumer.accept(AFFIXES.getDescriptionId(), "Affixes");
        consumer.accept(AFFIXES.getSubId("text_1"), "Affixes are powerful additional properties of ingredients. There are many different types of affixes that provide a percentage bonus to all essences, essences of a specific type, or essences of neighboring ingredients. Affixes that affect neighboring ingredients are the most common, so you should pay attention to them when choosing the order of ingredients for your elixir.");
        consumer.accept(AFFIXES.getSubId("note_1"), "You can see affixes in the ingredient description using Alchemist Eye, but only if you've already discovered all the essences of that ingredient.");

        consumer.accept(CONTRIBUTION.getDescriptionId(), "Contribution");
    }

    @FunctionalInterface
    interface PageBuilder {

        void build(ListContainer page);
    }
}
