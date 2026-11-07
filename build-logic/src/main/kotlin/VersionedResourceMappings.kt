val versionedResourceMappings = listOf(
	ResourceMapping(
		source = "source/@MODID/recipe",
		rules = listOf(
			ResourceRule(predicate = ">=1.21", path = "data/@MODID/recipe"),
			ResourceRule(predicate = "else", path = "data/@MODID/recipes")
		)
	)
)
