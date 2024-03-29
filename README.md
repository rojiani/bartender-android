# Bartender
![](docs/assets/logo.png)

***Work in Progress***

An Android app for finding cocktail recipes.

## Built With

* [Kotlin](https://kotlinlang.org/) 1.6.x
* [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines)
* [Kotlin Flow](https://developer.android.com/kotlin/flow)
* [Jetpack](https://developer.android.com/jetpack): Navigation, ViewModel, LiveData, Data Binding, Room
* [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection
* [Retrofit](https://square.github.io/retrofit/) HTTP client
    * Cocktail recipes from [TheCocktailDB API](https://www.thecocktaildb.com/api.php)
* [Moshi](https://github.com/square/moshi) for JSON 
* [Glide](https://bumptech.github.io/glide/) - image loading/caching
* [mockk](https://mockk.io/), [kotest](https://kotest.io/docs/assertions/assertions.html)
* Code Quality tools:
    * Static analysis:
        * [Detekt](https://detekt.github.io/detekt/) 
        * [Android Lint](https://developer.android.com/studio/write/lint)
    * Auto-formatting:
        * [Spotless](https://github.com/diffplug/spotless) with [ktlint](https://ktlint.github.io/) 
        for Kotlin code. Also formats Gradle scripts.
    * Git hooks:
        * pre-commit hook will run spotlessApply to format the code
        * pre-push hook will run detekt & spotlessCheck to catch static analysis or formatting issues
        before you push & they fail on CI
    * JaCoCo:
        * Currently disabled - not detecting all Kotlin source classes
* Uses some Domain-Driven Design (DDD) concepts to decouple network, database, & domain 
    layers. Why? Check out this great [Medium article](https://proandroiddev.com/the-real-repository-pattern-in-android-efba8662b754) 
    on DDD and the Repository pattern in Android.

### TheCocktailDB API

See [TheCocktailDB API](https://www.thecocktaildb.com/api.php) page for resources. This project contains a [Postman Collection](https://www.postman.com/collection/) (v. 2.1) [here](config/dev/postman-api/TheCocktailDB-API.postman_collection.json).

### Images

App Icon/NavHeader:
[Mojito Icon](https://www.flaticon.com/free-icon/mojito_2059994)
<div>Icons made by <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>


## Demo

<img src="docs/assets/bartender.gif" width="360" height="640"/>