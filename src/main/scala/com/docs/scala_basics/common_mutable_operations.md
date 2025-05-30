
| Operation         | Collection Type      | Associativity | In-place? | Returns New? | Example Usage                                |
|------------------|----------------------|---------------|-----------|---------------|----------------------------------------------|
| `+=`             | `ArrayBuffer`, `Set` | Left          | ✅        | ❌            | `buf += 5`                                    |
| `++=`            | `ArrayBuffer`, `Set` | Left          | ✅        | ❌            | `buf ++= Seq(1, 2, 3)`                        |
| `-=`             | `Set`, `Map`         | Left          | ✅        | ❌            | `set -= 3`                                    |
| `--=`            | `Set`, `Map`         | Left          | ✅        | ❌            | `map --= Seq("a", "b")`                       |
| `clear()`        | All mutable          | Left          | ✅        | ❌            | `buf.clear()`                                 |
| `insert()`       | `ArrayBuffer`        | Left          | ✅        | ❌            | `buf.insert(1, 10)`                           |
| `update(index)`  | `ArrayBuffer`, `Map` | Left          | ✅        | ❌            | `buf(2) = 100`                                |
| `append()`       | `ArrayBuffer`        | Left          | ✅        | ❌            | `buf.append(7)`                               |
| `prepend()`      | `ListBuffer`         | Left          | ✅        | ❌            | `lstBuf.prepend(0)`                           |
| `remove(index)`  | `ArrayBuffer`        | Left          | ✅        | ❌            | `buf.remove(1)`                               |
| `trimStart(n)`   | `ArrayBuffer`        | Left          | ✅        | ❌            | `buf.trimStart(2)`                            |
| `trimEnd(n)`     | `ArrayBuffer`        | Left          | ✅        | ❌            | `buf.trimEnd(2)`                              |
| `clone()`        | All mutable          | Left          | ❌        | ✅            | `val copy = buf.clone()`                      |
| `mapInPlace()`   | `ArrayBuffer`        | Left          | ✅        | ❌            | `buf.mapInPlace(_ * 2)`                       |
