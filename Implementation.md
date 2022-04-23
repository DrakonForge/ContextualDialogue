# Implementation Notes

This document covers some aspects of the technical implementation of this Contextual Dialogue.

## Table of Contents

  - [JSON Format](#json-format)
    - [Symbol JSON Format](#symbol-json-format)
  - [Tokenization and Parsing](#tokenization-and-parsing)
    - [Symbol Replacement](#symbol-replacement)

## JSON Format

This program creates a speech database using user-provided JSON files. Each **group** should have their own JSON file, which has the format described below. While it is heavily recommended to use [DrakonScript (link pending)]() to write these speechbanks, the JSON format is meant to still be human-interpretable while also maximizing parsing efficiency. It may be useful to know this format if the corresponding DrakonScript files are not available, or if you intend to replace the DrakonScript parser or the Contextual Dialogue implementation with your own version.

The group is named based on the name of the file, so `farmer.json` creates a speechbank for the group `farmer`. The contents of the file are as follows:

* [Object] The root tag.
  * [String] **parent**: The name of this speechbank's [parent speechbank](#parent-speechbanks).
  * [Array] **symbols**: A list of **[Symbols](#symbol-json-format)** defined for this group and all child groups.
  * [Object] **speechbank**: Data related to the speechbank content.
    * [Array] **\<category name\>**: A unique name given to the category.
      * [Object] A rule within this category.
        * [Array] **symbols**: A list of **[Symbols](#symbol-json-format)** defined only within this rule.
        * [Array] **presets**: A list of preset named rules that should be added to this rule's criteria.
        * [Array] **criteria**: A list of criteria associated with this rule.
          * [String] **type**: The type of this criterion.\*
          * [String] **table**: The context table associated with this criterion. Some criterion do not reference a context.
          * [String] **field**: The context field (name) associated with this criterion. Some criterion do not reference a context.
          * [Any] **value**: The value associated with this criterion. Some criterion do not have an associated value.
          * [Boolean] **inverse**: Whether this criterion is inverted. Some criterion cannot be inverted.
        * [Array] **actions**: A list of actions associated with this rule. Actions are performed **in order**.
          * [String] **op**: The operation to perform.\*\*
          * [Object] **context**:
            * [String] **table**: The context table associated with this action.
            * [String] **context**: The context field (name) associated with this action.
          * [Any] **value**: The value associated with this action.
        * [Array] **lines**: A list of possible speech line responses associated with this rule.
          * [String] A speech line.
        * [String] **lines**: A named rule that this rule should borrow its lines from.

\* **Possible values**: `equals`, `dummy`, `fail`, `range`, `min`, `max`, `exists`, `includes`, `empty`, `equals_dynamic`, `less_then_dynamic`, `less_equal_dynamic`, `greater_than_dynamic`, `greater_equal_dynamic`

\*\* **Possible values**: `add`, `mult`, `remove`, `invert`, `set_list`, `set_static`, `set_dynamic`

### Symbol JSON Format

* [Object] The root tag of the symbol.
  * [String] **name**: The name of the symbol.
  * [Any] **exp**: The symbol expression. This can be any of the following:
    * [Array] A list of symbol expressions.
    * [String] A string. If the string starts with `@`, it is considered to be another (previously defined) symbol.
    * [Boolean] A boolean.
    * [Number] A number.
    * [Object] A context.
      * [String] **table**: The context table.
      * [String] **context**: The context field.
    * [Object] A function.
      * [String] **function**: The function name.
      * [Array] **args**: A list of symbol expressions which are the arguments of the function.

## Tokenization and Parsing

This Contextual Dialogue implementation uses a bespoke **tokenizer** to parse speech lines. This follows efficient tokenization conventions of only needing to read each character in the string **once** without backtracking. This converts a speech line into a **token tree** with a single token (usually a `TokenGroup`) at the root. To generate the speech line, each token is evaluated recursively from the root, passing along the speech query to gain access to context and other information. This tokenization process can be visualized in a linear format using the `--debug` option of the DrakonScript command line tool. For example, the speech line `"@capitalize(@fruits), @fruits, @fruits, @fruits... you name it!"` in the example `fruit_vendor.drkn` is tokenized as the following (expanded for readability):

```
{Function capitalize args=[
  ["apples", "oranges", "mangoes", "pineapples", "watermelons", "avocadoes", "cherries", "grapes", "tangerines"]
]}
", "
["apples", "oranges", "mangoes", "pineapples", "watermelons", "avocadoes", "cherries", "grapes", "tangerines"]
", "
["apples", "oranges", "mangoes", "pineapples", "watermelons", "avocadoes", "cherries", "grapes", "tangerines"]
", "
["apples", "oranges", "mangoes", "pineapples", "watermelons", "avocadoes", "cherries", "grapes", "tangerines"]
"... you name it!"
```

You can see how the `@fruits` symbol is replaced during tokenization with the value of the symbol (a list of fruits). This works because the system also **parses symbol expressions** into the *same* kind of token tree, allowing for them to replace each other (see [Symbol Replacement](#symbol-replacement-technical) for more details). This speech line is then evaluated token by token, and the results are concatenated to produce the speech line.

## Symbol Replacement

Symbols are not actually stored within the speech database during runtime. Instead, during *compile-time* a process known as **symbol replacement** occurs. First, each symbol is parsed into tokens which are temporarily stored. After parsing each speech line, the parser recursively replaces any **symbol** tokens used in the speech line with the symbol's **tokens**. Then, the stored tokens are forgotten.

This works primarily because symbols are immutable, and thus the system can compute its token tree at compile-time and replace all instances of the symbol with this tree. Therefore, the system does *not* have to look up the symbol's value at runtime, which increases efficiency.

Every time the symbol is replaced, it is replaced with the **same** token tree. This means that using symbols is **memory-efficient**, and it is preferable to use rules in most cases. To illustrate this, compare the following rules in DrakonScript:

```
rule A() {
  lines = [
    "My favorite animal is a [\"dog\", \"cat\", \"fish\"]"
  ]
}

rule B() {
  @animal = ["dog", "cat", "fish"]
  lines = [
    "My favorite animal is a @animal"
  ]
}
```

In rule **A**, the list is declared within the speech line (with `\"` used to escape the quoted string). This is valid using this Contextual Dialogue implementation, and results in a random item from that list being chosen e.g. `My favorite animal is a cat`.

In rule **B**, symbol replacement ensures that the token tree of the speech line is **exactly the same** as the line in rule **A**. This means that during **runtime**, both lines have the *same* performance. However, during compile-time, rule **B** is *slightly* slower since it performs a lookup and replacement during **compile-time** to convert the token tree to be identical to rule **A**.

So why use symbols? Well, not having to escape strings is nice, though this is more of a result of DrakonScript's syntax. Compile-time overhead is less important than runtime performance for a game, so using either option is reasonable. However, symbols really become useful when they are **re-used**. Consider the following:

```
rule C() {
  lines = [
    "My favorite animal is a [\"dog\", \"cat\", \"fish\"]"
    "I just saw a [\"dog\", \"cat\", \"fish\"]"
  ]
}

rule D() {
  @animal = ["dog", "cat", "fish"]
  lines = [
    "My favorite animal is a @animal"
    "I just saw a @animal"
  ]
}
```

In rule **C**, both speech lines create *their own* versions of the same list, as they have no awareness that another identical list was already created elsewhere. This takes twice as much memory as is needed. In rule **D**, the *same* list is **re-used** in both speech lines. This has identical runtime performance, but is more memory efficient since it is not creating two copies of the same list.

In DrakonScript, symbols also have more flexible syntax to write expressions outside of a speech line, rather than within it. Though you can write expressions with the same meaning both inside and outside the speech line, it is generally more difficult to do this inside a speech line. For example, consider the symbol `@tomorrow = @today + 1` and the speech line `"Tomorrow is day @tomorrow"`, which would return something like `Tomorrow is day three`. If we try to eliminate the symbol, we might try a speech line like `"Tomorrow is day @today + 1"`. However, this is invalid syntax since `+` cannot be used in a speech line to add values (parentheses don't help either). Fortunately, `+` in DrakonScript is just an alias for the [`@add()`](#add) function so we could instead write this as `"Tomorrow is day @add(@today, 1)"`. Although this has the exact same meaning, the syntax is clunkier, especially as expressions get more complex.
