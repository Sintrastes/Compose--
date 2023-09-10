# Compose--
Minimalistic declarative UI engine in Kotlin built on top of flow. Loosely based on [fudgets](https://hackage.haskell.org/package/fudgets).

# Idea

Compose-- ("Compose minus minus") is built a simple and elegant idea, with it's roots in the [fudgets](https://hackage.haskell.org/package/fudgets) library in Haskell -- arguably the earliest example of a functonal (as opposed to object-oriented or procedural) GUI framework. That idea is that user interfaces are just _stream processors_ that can be composed in different ways. As such, conceputally in Kotlin they can be thought of as the type `Flow<A> -> Flow<B>`. Doesen't get much simpler than that!

In reality, the type in Compose-- is slightly more involved, as in addition to the conceputal API of a UI viewed as `Flow<A> -> Flow<B>`, we also need to be able to specify how the view elements are laid out. Compose-- handles this with the `View.Builder` interface, which defines an API for combining multiple views into a single view. 
