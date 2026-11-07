plugins {
	alias(libs.plugins.stonecutter)
	alias(libs.plugins.mod.publish.plugin)
	alias(libs.plugins.loom).apply(false)
	alias(libs.plugins.neoforged.moddev).apply(false)
	alias(libs.plugins.jsonlang.postprocess).apply(false)
	alias(libs.plugins.legacyforge.moddev).apply(false)
	id("mod-root")
}

stonecutter active file(".sc_active_version")

stonecutter parameters {
	properties.tags(current.version, current.project.substringAfterLast('-'))
	constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
	swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
	swaps["mod_id"] = "\"${properties.get<String>("mod.id")}\";"
	swaps["mod_name"] = "\"${properties.get<String>("mod.name")}\";"
	swaps["mod_group"] = "\"${properties.get<String>("mod.group")}\";"
	swaps["minecraft"] = "\"${current.version}\";"
	constants["release"] = properties.get<String>("mod.id") != "modtemplate"
	replacements.string(current.parsed >= "1.21") {
		replace("dev.obscuria.archivist.api.v1.", "dev.obscuria.elixirum.compat.archivist.")
		replace("dev.obscuria.fragmentum.network.PayloadCodec", "dev.obscuria.elixirum.compat.network.PayloadCodec")
		replace("dev.obscuria.fragmentum.network.FragmentumNetworking", "dev.obscuria.elixirum.compat.network.FragmentumNetworking")
		replace("dev.obscuria.fragmentum.registry.", "dev.obscuria.fragmentum.content.registry.")
		replace("dev.obscuria.fragmentum.network.", "dev.obscuria.fragmentum.content.network.")
		replace("dev.obscuria.fragmentum.packs.", "dev.obscuria.fragmentum.content.packs.")
		replace("dev.obscuria.fragmentum.util.", "dev.obscuria.fragmentum.content.util.")
		replace("dev.obscuria.fragmentum.world.", "dev.obscuria.fragmentum.content.world.")
		replace("ResourceLocation::new", "ResourceLocation::parse")
		replace("new ResourceLocation(raw)", "ResourceLocation.parse(raw)")
		replace("new ResourceLocation(MODID, name)", "ResourceLocation.fromNamespaceAndPath(MODID, name)")
		replace("DataResult.PartialResult<", "DataResult.Error<")
		replace("net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket", "net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket")
		replace(".write(stack.getOrCreateTag(),", ".write(stack,")
		replace(".readOrDefault(stack.getTag(),", ".readOrDefault(stack,")
		replace(".read(stack.getTag())", ".read(stack)")
		for (aspect in listOf("POTION", "CORPUS", "ANIMA", "VENENUM", "MEDELA", "CRESCERE", "MUTATIO", "FORTUNA", "TENEBRAE")) {
			replace("Attribute ${aspect}_", "dev.obscuria.fragmentum.content.registry.DeferredAttribute ${aspect}_")
			for (kind in listOf("MASTERY", "RESISTANCE"))
				replace(".add(ElixirumAttributes.${aspect}_$kind)", ".add(ElixirumAttributes.${aspect}_$kind.holder())")
		}
		replace("Attribute mastery", "dev.obscuria.fragmentum.content.registry.DeferredAttribute mastery")
		replace("Attribute resistance", "dev.obscuria.fragmentum.content.registry.DeferredAttribute resistance")
		replace("private static Attribute register(", "private static dev.obscuria.fragmentum.content.registry.DeferredAttribute register(")
		replace("net.minecraft.world.inventory.CraftingContainer", "net.minecraft.world.item.crafting.CraftingInput")
		replace("CraftingContainer container", "CraftingInput container")
		replace("container.getItems()", "container.items()")
		replace("container.getContainerSize()", "container.size()")
		replace("public CompoundTag getUpdateTag()", "public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries)")
		replace("this.saveWithoutMetadata()", "this.saveWithoutMetadata(registries)")
		replace("saveAdditional(CompoundTag tag)", "saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries)")
		replace("super.saveAdditional(tag)", "super.saveAdditional(tag, registries)")
		replace("public void load(CompoundTag tag)", "protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries)")
		replace("super.load(tag)", "super.loadAdditional(tag, registries)")
		replace(".save(new CompoundTag())", ".saveOptional(registries)")
		replace("ItemStack.of(", "dev.obscuria.elixirum.compat.ItemStackMigration.read(registries, ")
		replace("RegistryAccess registryAccess)", "net.minecraft.core.HolderLookup.Provider registryAccess)")
		replace(".vertex(", ".addVertex(")
		for (red in listOf("r", "lr", "rr", "ir", "255", "this.rCol", "c1.red()", "c2.red()"))
			replace(".color($red,", ".setColor($red,")
		replace(".uv(", ".setUv(")
		replace(".uv2(", ".setLight(")
		replace(".overlayCoords(", ".setOverlay(")
		replace(".normal(", ".setNormal(")
		replace(".getFrameTime()", ".getTimer().getGameTimeDeltaPartialTick(true)")
		replace(".mobEffect())", ".effectHolder())")
		replace("textures.get(essence.effect().value())", "textures.get(essence.effect())")
		replace("new MobEffectInstance(mobEffect(),", "new MobEffectInstance(effectHolder(),")
		replace("return MobEffects.UNLUCK;", "return MobEffects.UNLUCK.value();")
		replace("StringUtil.formatTickDuration(duration())", "StringUtil.formatTickDuration(duration(), 20)")
		replace(".discover(effect.getEffect())", ".discover(effect.getEffect().value())")
		replace(".computeIfAbsent(instance.getEffect(),", ".computeIfAbsent(instance.getEffect().value(),")
		replace("instance.getEffect().isInstantenous()", "instance.getEffect().value().isInstantenous()")
		replace("instance.getEffect().applyInstantenousEffect(", "instance.getEffect().value().applyInstantenousEffect(")
		replace("ExtraCodecs.COMPONENT", "net.minecraft.network.chat.ComponentSerialization.CODEC")
		replace("ExtraCodecs.FLAT_COMPONENT", "net.minecraft.network.chat.ComponentSerialization.FLAT_CODEC")
		// Preserve the existing map-shaped Codex data rather than adding a new value envelope.
		replace("::codec, Function.identity())", "::codec, com.mojang.serialization.MapCodec::assumeMapUnsafe)")
		replace(".dispatch(type, Function.identity())", ".dispatch(type, com.mojang.serialization.MapCodec::assumeMapUnsafe)")
		replace("item.getRarity(DUMMY)", "item.getDefaultInstance().getRarity()")
		replace(".copy(Blocks.", ".ofFullCopy(Blocks.")
		replace("ItemStack stack, @Nullable Level level, List<Component>", "ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component>")
		for (name in listOf("essenceBlacklist", "essenceWhitelist", "ingredientBlacklist", "ingredientWhitelist"))
			replace(".defineList(\"$name\", new ArrayList<>())", ".defineList(\"$name\", new ArrayList<String>(), () -> \"minecraft:air\", value -> value instanceof String text && net.minecraft.resources.ResourceLocation.tryParse(text) != null)")
	}
}
