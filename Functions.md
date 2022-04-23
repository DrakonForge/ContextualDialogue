# List of Functions

This is a list of [functions (link pending)]() currently supported by this Contextual Dialogue implementation. Functions are described by the following fields:

* A description of the function.
* **Syntax**: The syntax of how the function is used.
* **Arguments**: Describes each argument in the Syntax field, including its **data type** (written in square brackets) and purpose.
* **Returns**: Describes the return value of the function, including its **data type** and purpose.
* **Examples**: A list of examples in the form `input statement -> output value` to show how the function works.
  * In some cases, symbols may be declared for use in the example.
  * In some cases, the form `input statement ~> output value` may be used to describe one or more *possible* (not guaranteed) outputs.

For a description of the available **data types**, see [here (link pending)]().

## Table of Contents

  - [String Manipulation](#string-manipulation)
    - [`decapitalize`](#decapitalize)
    - [`capitalize`](#capitalize)
    - [`upper`](#upper)
    - [`lower`](#lower)
    - [`concat`](#concat)
    - [`pluralize`](#pluralize)
  - [Gender](#gender)
    - [`subjective`](#subjective)
    - [`objective`](#objective)
    - [`possessive`](#possessive)
    - [`reflexive`](#reflexive)
    - [`gender`](#gender)
  - [Lists](#lists)
    - [`list_concat`](#list_concat)
    - [`count`](#count)
  - [Previous Choice Matching](#previous-choice-matching)
    - [`prev`](#prev)
    - [`prev_match`](#prev_match)
  - [Float Arithmetic](#float-arithmetic)
    - [`add`](#add)
    - [`sub`](#sub)
    - [`mult`](#mult)
    - [`div`](#div)
  - [Integer Arithmetic](#integer-arithmetic)
    - [`div_int`](#div_int)
    - [`mod`](#mod)
    - [`rand_int`](#rand_int)
    - [`to_int`](#to_int)
  - [Boolean Logic](#boolean-logic)
    - [`if_else`](#if_else)
    - [`not`](#not)
    - [`and`](#and)
    - [`or`](#or)

## String Manipulation

These functions manipulate or perform operations related to strings.

### `decapitalize`

De-capitalizes a string. 

* **Syntax**: `@decapitalize(s)`
* **Arguments**
  * [String] `s`: A string.
* **Returns**: [String] The string with its first letter lowercased.
* **Examples**:
  * `@decapitalize("Apple")` -> `"apple"`

### `capitalize`

Capitalizes a string.

* **Syntax**: `@capitalize(s)`
* **Arguments**
  * [String] `s`: A string.
* **Returns**: [String] The string with its first letter uppercased.
* **Examples**:
  * `@capitalize("apple")` -> `"Apple"`

### `upper`

Converts a string to all uppercase.

* **Syntax**: `@upper(s)`
* **Arguments**
  * [String] `s`: A string.
* **Returns**: [String] The string with all letters uppercased.
* **Examples**:
  * `@upper("apple")` -> `"APPLE"`

### `lower`

Converts a string to all lowercase.

* **Syntax**: `@lower(s)`
* **Arguments**
  * [String] `s`: A string.
* **Returns**: [String] The string with all letters lowercased.
* **Examples**:
  * `@lower("APPLE")` -> `"apple"`

### `concat`

Concatenates one or more string expressions into a single string.

* **Syntax**: `@concat(str1, str2, ...)`
* **Arguments**
  * [String] `str1`: The first string (required).
  * [String] `str2`, `...`: Additional optional strings.
* **Returns**: [String] The concatenated string.
* **Examples**:
  * `@concat("a") -> "a"`
  * `@concat(5, " apples")` -> `"five apples"`
  * `@concat("a", "b", "c")` -> `"abc"`

### `pluralize`

Returns the singular or plural form of a word based on the given number.

* **Syntax**: `@pluralize(num, singular, plural)`
* **Arguments**
  * [Integer] `num`: A number.
  * [String] `singular`: The text if `num` is exactly 1.
  * [String] `plural`: The text if `num` is not exactly 1.
* **Returns**: [String] Either `singular` or `plural` based on the value of `num`.
* **Examples**: 
  * `I see #num_rabbits @pluralize(#num_rabbits, "rabbit", "rabbits") today!` ~> `I see one rabbit today!` or `I see five rabbits today!`

## Gender

These operations provide limited support for gender-specific phrases.

### `subjective`

Returns a subjective pronoun based on the given gender.

* **Syntax**: `@subjective(gender)`
* **Arguments**
  * [String] `gender`: "male", "female", or "none"
* **Returns**: [String] "he", "she", or "they"
* **Examples**:
  * `@subjective("male")` -> `"he"`
  * `@subjective("female")` -> `"she"`
  * `@subjective("none")` -> `"they"`

### `objective`

Returns an objective pronoun based on the given gender.

* **Syntax**: `@objective(gender)`
* **Arguments**
  * [String] `gender`: "male", "female", or "none"
* **Returns**: [String] "him", "her", "them"
* **Examples**:
  * `@objective("male")` -> `"him"`
  * `@objective("female")` -> `"her"`
  * `@objective("none")` -> `"them"`

### `possessive`

Returns a possessive pronoun based on the given gender.

* **Syntax**: `@possessive(gender)`
* **Arguments**
  * [String] `gender`: "male", "female", or "none"
* **Returns**: [String] "his", "hers", "theirs"
* **Examples**:
  * `@possessive("male")` -> `"his"`
  * `@possessive("female")` -> `"hers"`
  * `@possessive("none")` -> `"theirs"`

### `reflexive`

Returns a reflexive pronoun based on the given gender.

* **Syntax**: `@reflexive(gender)`
* **Arguments**
  * [String] `gender`: "male", "female", or "none"
* **Returns**: [String] "himself", "herself", "themself"
* **Examples**:
  * `@reflexive("male")` -> `"himself"`
  * `@reflexive("female")` -> `"herself"`
  * `@reflexive("none")` -> `"themself"`

### `gender`

Returns a string based on the given gender.

* **Syntax**: `@gender(gender, male, female, neutral)`
* **Arguments**
  * [String] `gender`: "male", "female", or "none".
  * [String] `male`: The text if `gender` is "male".
  * [String] `female`: The text if `gender` is "female".
  * [String] `neutral`: The text if `gender` is "none".
* **Returns**: [String] Either `male`, `female`, or `neutral` based on the value of `gender`.
* **Examples**: 
  * `@gender("male", "boy", "girl", "child")` -> `"boy"`
  * `@gender("female", "boy", "girl", "child")` -> `"girl"`
  * `@gender("none", "boy", "girl", "child")` -> `"child"`

## Lists

These functions perform operations related to lists.

### `list_concat`

Combines one or more lists into a single list.

* **Syntax**: `@list_concat(list1, list2, ...)`
* **Arguments**
  * [List] `list1`: The first list (required).
  * [List] `list2`, `...`: Additional optional lists.
* **Returns**: [List] The concatenated list.
* **Examples**:
  * `@list_concat(["a"], ["b"])` -> `["a", "b"]`
  * `@list_concat(["a", "b", "c"], ["d", "e"], ["f"])` -> `["a", "b", "c", "d", "e", "f", "g"]`

### `count`

Counts the number of elements in a list.

Note that integers are automatically converted to their word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.

* **Syntax**: `@count(list)`
* **Arguments**
  * [List] `list`: A list.
* **Returns**: [Integer] The number of elements in the list.
* **Examples**:
  * `@count(["a", "b", "c"])` -> `three`
  * `@count([])` -> `zero`

## Previous Choice Matching

These functions allow writers to access **previously chosen** random selections from a list. This is important since list selection is generally done **without replacement**, so this provides a way to consistently pick the same option as before.

### `prev`

Returns the result of the *n*th **list choice** in the speech line. Fails if there has not been that many list choices yet.

Note the `~>` notation used in the examples. This means that the output is a *possible* result of the input.
While the list item selected might not be a string, it is always converted into its string representation. The value of the original data type cannot be extracted.

* **Syntax**: `@prev(n)`
* **Arguments**
  * [Integer] `n`: The number of the previous choice. `1` represents the first list choice made in the speech line.
* **Returns**: [String] The result of the previous list choice.
* **Examples**: Consider the symbol `@s = ["x", "y"]`.
  * `The reverse of @s, @s is @prev(2), @prev(1)` ~> `The reverse of x, y is y, x`
  * `@s is the same as @prev(1)` ~> `x is the same as x`
  * `@s is not the same as @prev(2)` -> Fails since a 2nd list choice was never made

### `prev_match`

Finds the list index of the *n*th **list choice** in the speech line. Then returns the item at the same (matching) index in a different list. Fails if there has not been that many list choices yet, or if the index is out of bounds in the different list. Best used between two lists that have the same length.

Note the `~>` notation used in the examples. This means that the output is a *possible* result of the input.
While the list item selected might not be a string, it is always converted into its string representation.

* **Syntax**: `@prev(n, list)`
* **Arguments**
  * [Integer] `n`: An integer representing the number of the previous choice. `1` represents the first list choice made in the speech line.
  * [List] `list`: The list to match the index to.
* **Returns**: [String] An item in `list` at the same list index as the *n*th list choice.
* **Examples**: 
  * Consider the symbols `@s = ["x", "y"], @n = ["input", "output"]`.
    * `@s is the @prev_match(1, @n) of a function.` ~> `x is the input of a function` or `y is the output of a function`
  * Consider the symbols `@a = ["carrots", "fruit"], @b = ["are", "is"]`
    * `@capitalize(@s) @prev_match(1, @b) the best food!` ~> `Carrots are the best food!` or `Fruit is the best food!`

## Float Arithmetic

These functions perform arithmetic on **floats**, otherwise known as decimal values.

### `add`

Adds two numbers.

Note that numbers are automatically converted to their (integer) word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.

* **Syntax**: `@add(a, b)`
* **Arguments**
  * [Number] `a`: A number.
  * [Number] `b`: A number.
* **Returns**: [Number] `a + b`.
* **Examples**:
  * Consider the symbols `@a = 5 and @b = 2`.
    * `@capitalize(@a) plus @b is @add(@a, @b).` -> `Five plus two is seven.`

### `sub`

Subtracts two numbers.

Note that numbers are automatically converted to their (integer) word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.

* **Syntax**: `@sub(a, b)`
* **Arguments**
  * [Number] `a`: A number.
  * [Number] `b`: A number.
* **Returns**: [Number] `a - b`.
* **Examples**:
  * Consider the symbols `@a = 5 and @b = 2`.
    * `@capitalize(@a) minus @b is @sub(@a, @b).` -> `Five minus two is three.`

### `mult`

Multiplies two numbers.

Note that numbers are automatically converted to their (integer) word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.

* **Syntax**: `@mult(a, b)`
* **Arguments**
  * [Number] `a`: A number.
  * [Number] `b`: A number.
* **Returns**: [Number] `a * b`.
* **Examples**:
  * Consider the symbols `@a = 5 and @b = 2`.
    * `@capitalize(@a) times @b is @mult(@a, @b).` -> `Five times two is ten.`

### `div`

Divides two numbers.

Note that numbers are automatically converted to their (integer) word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.
Fails on division by zero.

* **Syntax**: `@div(a, b)`
* **Arguments**
  * [Number] `a`: A number.
  * [Number] `b`: A number.
* **Returns**: [Number] `a / b`
* **Examples**: 
  * Consider the symbols `@a = 5 and @b = 2`.
    * `@capitalize(@a) divided by @b is @div(@a, @b).` -> `Five divided by two is two.`
    * `@mult(@div(@a, @b), 100)` -> `two hundred fifty`

## Integer Arithmetic

These functions perform arithmetic on **integers**, which are strictly whole numbers (negatives are allowed).

### `div_int`

Divides two numbers with integer division.

Note that integers are automatically converted to their word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.
Fails on division by zero.

* **Syntax**: `@div_int(a, b)`
* **Arguments**
  * [Integer] `a`: An integer.
  * [Integer] `b`: An integer.
* **Returns**: [Integer] `a / b`
* **Examples**: 
  * Consider the symbols `@a = 5 and @b = 2`.
    * `@capitalize(@a) divided by @b is @div_int(@a, @b).` -> `Five divided by two is two.`
    * `@mult(@div_int(@a, @b), 100)` -> `two hundred`

### `mod`

Returns the remainder after dividing two numbers with integer division.

Note that integers are automatically converted to their word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.
Fails on division by zero.

* **Syntax**: `@mod(a, b)`
* **Arguments**
  * [Integer] `a`: An integer.
  * [Integer] `b`: An integer.
* **Returns**: [Integer] `a % b`
* **Examples**: 
  * Consider the symbols `@a = 5 and @b = 2`.
    * `@capitalize(@a) mod @b is @mod(@a, @b).` -> `Five mod two is one.`

### `rand_int`

Returns a random number between 0 and a maximum integer. This is min-inclusive, max-exclusive so it can return 0, but not the maximum value.

Note that integers are automatically converted to their word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.
Note the `~>` notation used in the examples. This means that the output is a *possible* result of the input.

* **Syntax**: `@rand_int(max)`
* **Arguments**
  * [Integer] `max`: The maximum (exclusive) value that can be generated.
* **Returns**: [Integer] An integer in the range [0, max), equivalent to [0, max - 1].
* **Examples**: 
  * `@rand_int(3)` ~> `zero` or `one` or `two`

### `to_int`

Converts a number to an integer for use in integer-only functions. This simply removes the decimal portion of the number, so `1.75` becomes `1`. Integers and floats that are already whole numbers are unchanged.

Note that integers are automatically converted to their word form when printed; however, using this as a function argument or some other internal expression uses the numeric value instead.

* **Syntax**: `@to_int(num)`
* **Arguments**
  * [Number] `num`: A number.
* **Returns**: [Integer] The number converted to an integer.
* **Examples**: 
  * `@div_int(@to_int(7.5), @to_int(3.2))` -> `two`

## Boolean Logic

These functions perform operations related to **booleans**, which are either `true` or `false`.

### `if_else`

Reads a boolean. If it is true, returns one statement; otherwise, returns another.

Note that this system does not handle expressions like comparisons or equality checks, so this method is useful only for values that are already booleans.
Note the `~>` notation used in the examples. This means that the output is a *possible* result of the input.

* **Syntax**: `@if_else(condition, if_true, if_false)`
* **Arguments**
  * [Boolean] `condition`: The condition to check.
  * [String] `if_true`: The value if `condition` is true.
  * [String] `if_false`: The value if `condition` is false.
* **Returns**: [String] Either `if_true` or `if_false` based on the value of `condition`.
* **Examples**: 
  * `Time to @if_else(#is_hungry, "eat", "sleep")!` ~> `Time to eat!` or `Time to sleep!`

### `not`

Inverts a boolean value.

Note that this system does not handle expressions like comparisons or equality checks, so this method is useful only for values that are already booleans.
Note that booleans should never be printed directly, so the outputs below are not the actual print outputs.

* **Syntax**: `@not(flag)`
* **Arguments**
  * [Boolean] `flag`: The boolean to invert.
* **Returns**: [Boolean] The inverse of `flag`.
* **Examples**: 
  * `@not(true)` -> `false`
  * `@not(false)` -> `true`

### `and`

Checks if two boolean expressions are both true. This does not use short-circuit evaluation.

Note that this system does not handle expressions like comparisons, so this method is useful only for values that are already booleans.
Note that booleans should never be printed directly, so the outputs below are not the actual print outputs.

* **Syntax**: `@and(a, b)`
* **Arguments**
  * [Boolean] `a`: The first boolean to check.
  * [Boolean] `b`: The second boolean to check.
* **Returns**: [Boolean] `a && b`
* **Examples**: 
  * `@and(false, false)` -> `false`
  * `@and(false, true)` -> `false`
  * `@and(true, false)` -> `false`
  * `@and(true, true)` -> `true`

### `or`

Checks if either (or both) of two boolean expressions are true. This does not use short-circuit evaluation.

Note that this system does not handle expressions like comparisons, so this method is useful only for values that are already booleans.
Note that booleans should never be printed directly, so the outputs below are not the actual print outputs.

* **Syntax**: `@or(a, b)`
* **Arguments**
  * [Boolean] `a`: The first boolean to check.
  * [Boolean] `b`: The second boolean to check.
* **Returns**: [Boolean] `a || b`
* **Examples**: 
  * `@or(false, false)` -> `false`
  * `@or(false, true)` -> `true`
  * `@or(true, false)` -> `true`
  * `@or(true, true)` -> `true`
