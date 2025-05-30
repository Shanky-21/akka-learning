```
| Operation        | Type of Collection | Associativity      | In-place? | Returns New? | Description |
|------------------|---------------------|---------------------|-----------|----------------|-------------|
| `::`             | `List`              | Right (`:` ending)  | ❌        | ✅              | Prepends element to a list |
| `:+`             | `List`, `Vector`    | Left                | ❌        | ✅              | Appends element to the end |
| `+:`             | `Vector`, `Seq`     | Right               | ❌        | ✅              | Prepends element to the front |
| `++`             | All collections     | Left                | ❌        | ✅              | Concatenates two collections |
| `:::`            | `List`              | Right               | ❌        | ✅              | Concatenates two lists |
| `map`            | All collections     | Left                | ❌        | ✅              | Applies a function to each element |
| `filter`         | All collections     | Left                | ❌        | ✅              | Keeps elements matching predicate |
| `flatMap`        | All collections     | Left                | ❌        | ✅              | Maps and flattens nested structures |
| `reverse`        | All collections     | Left                | ❌        | ✅              | Reverses order of elements |
| `sorted`         | `Seq`, `List`, etc. | Left                | ❌        | ✅              | Sorts elements (based on `Ordering`) |
| `groupBy`        | All collections     | Left                | ❌        | ✅              | Groups elements into a `Map` |
| `zip`            | All collections     | Left                | ❌        | ✅              | Pairs elements from two collections |
| `foldLeft`       | All collections     | Left                | ❌        | ✅              | Folds from the left (associative) |
| `foldRight`      | All collections     | Right               | ❌        | ✅              | Folds from the right |
| `updated`        | `Vector`, `List`    | Left                | ❌        | ✅              | Returns new collection with one updated element |
| `patch`          | `Seq`, `List`       | Left                | ❌        | ✅              | Replaces a part of a sequence |
| `drop`/`take`    | All collections     | Left                | ❌        | ✅              | Removes/takes first n elements |
| `slice`          | All collections     | Left                | ❌        | ✅              | Extracts a sub-sequence |
| `distinct`       | All collections     | Left                | ❌        | ✅              | Removes duplicate elements |
| `mkString`       | All collections     | Left                | ❌        | ✅ (String)     | Joins elements into a string |
```