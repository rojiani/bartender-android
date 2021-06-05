package com.nrojiani.bartender.data.domain

import io.kotest.matchers.shouldBe
import org.junit.Test

class IngredientTest {
    @Test
    fun url_encode_image_url() {
        val ginIngredient = Ingredient(
            id = "2",
            name = "Elderflower cordial",
            description = "test description",
            type = "cordial",
            alcoholic = false,
            abv = "0"
        )

        ginIngredient.imageUrl(ImageSize.SMALL)
            .shouldBe("https://www.thecocktaildb.com/images/ingredients/Elderflower%20cordial-small.png")

        ginIngredient.imageUrl(ImageSize.MEDIUM)
            .shouldBe("https://www.thecocktaildb.com/images/ingredients/Elderflower%20cordial-medium.png")

        ginIngredient.imageUrl(ImageSize.LARGE)
            .shouldBe("https://www.thecocktaildb.com/images/ingredients/Elderflower%20cordial.png")
    }
}
