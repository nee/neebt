This library enables you to work with NBT using *standard Kotlin types*.\
Note:
- NBT doesn't know `null`
- Compound = `Map<String, Any>`
- List = `List<E>`

# Basic operations
## Read
```kotlin
val dataInputStream: DataInputStream

// Read a byte
val nbt: Byte = dataInputStream.readNBT()

// Read a named compound
val (name, compound) = dataInputStream.readNamedNBTCompound()
val compound = dataInputStream.readNamedNBTCompound("name")
```
## Write
```kotlin
val dataOutputStream: DataOutputStream

// Write a byte
val byte: Byte
dataOutputStream.writeNBT(byte)

// Write a named compound
val name: String
val compound: Map<String, Any>
dataOutputStream.writeNamedNBTCompound(name, compound)
```

## Stringify
```kotlin
val nbt: Any
val output: String = nbt.toSNBT()
```
### Example
Input:
```kotlin
val nbt: Map<String, Any> = mapOf(
    "foo" to listOf("bar", "x"), 
    "answer" to 42.0, 
    "baz" to mapOf(
        "pi" to intArrayOf(3, 1, 4, 1)
    )
)
println(nbt.toSNBT())
```
Output:
```kotlin
{
    "foo": [
        "bar",
        "x"
    ],
    "answer": 42.0D,
    "baz": {
        "pi": [3, 1, 4, 1]
    }
}
```

# Advanced
## Compression
Currently not supported. See this [issue](https://github.com/nee/neebt/issues/2).
## Merging

### Map-only merging
You can use `NBT.mergeMaps(a, b)` to merge two compounds into one, deeply.
Lists will not be merged.

Algorithm:
```kotlin
val c = a
for (key in b) {
    if (key in a && a[key] is Map && b[key] is Map) {
        c[key] = mergeMaps(a[key], b[key])
    } else {
        c[key] = b[key]
    }
}
```

#### Demonstration
```kotlin
{                   {                       {            
  "foo": 0,           "foo": 10,              "foo": 10, 
  "bar": {            "bar": {                "bar": {   
    "baz": 1,           "baz": 11,              "baz": 11,
    "qux": 2    +                     =>        "qux": 2,         
                        "abc": 3                "abc": 3 
  },                  },                      },         
                      "xyz": 4,               "xyz": 4,
  "list": [           "list": [               "list": [
    "!",                ","                     ","
    "/"                 
  ]                   ]                       ]
}                   }                       }            
```

### List-only merging
You can use `NBT.mergeLists(a, b)` to merge two lists into one, deeply.
Compounds will not be merged.

Algorithm:
```kotlin
val c = a
for (index in b) {
    if (index in a && a[index] is List && b[index] is List) {
        c[index] = mergeLists(a[index], b[index])
    } else {
        c[index] = b[index]
    }
}
```

#### Demonstration
```kotlin
[            [                  [
  [            [                  [
    0,           10,                10,
    1,           11,                11,
    2            12,                12,
                 5                  5
  ],     +     ],        =>       ],
  [            [                  [
    {"x": 3},    {}                 {},
    {"y": 4}                        {"y": 4}
  ]            ],                 ],
               []                 []
]            ]                  ]
```

### List-concatenating map merging
You can use `NBT.mergeMapsAdding(a, b)` to merge two compounds into one, deeply.
Lists that are present in both `a` and `b` will be concatenated.

Algorithm:
```kotlin
val c = a
for (key in b) {
	if (key in a) {
		if (a[key] is Map && b[key] is Map) {
			c[key] = merge(a[key], b[key])
		}
		else if (a[key] is List && b[key] is List) {
			c[key] = a[key] + b[key]
		}
		else {
			c[key] = b[key]
		}
	} else {
		c[key] = b[key]
	}
}
```

#### Demonstration
```kotlin
{                   {                       {            
  "foo": 0,           "foo": 10,              "foo": 10, 
  "bar": {            "bar": {                "bar": {   
    "baz": 1,           "baz": 11,              "baz": 11,
    "qux": 2    +                     =>        "qux": 2,         
                        "abc": 3                "abc": 3 
  },                  },                      },         
                      "xyz": 4,               "xyz": 4,
  "list": [           "list": [               "list": [
    "!",                                        "!",
    "/"                                         "/",
                        ","                     ","
  ]                   ]                       ]
}                   }                       }            
```

### Map merging
You can use `NBT.merge(a, b)` to merge two compounds into one, deeply.
Lists will be merged too.

Algorithm:
```kotlin
val c = a
for (key in b) {
	if (key in a) {
		if (a[key] is Map && b[key] is Map) {
			c[key] = merge(a[key], b[key])
		}
		else if (a[key] is List && b[key] is List) {
			c[key] = merge(a[key], b[key])
		}
		else {
			c[key] = b[key]
		}
	} else {
		c[key] = b[key]
	}
}
```

#### Demonstration
```kotlin
{                   {                       {            
  "foo": 0,           "foo": 10,              "foo": 10, 
  "bar": {            "bar": {                "bar": {   
    "baz": 1,           "baz": 11,              "baz": 11,
    "qux": 2    +                     =>        "qux": 2,         
                        "abc": 3                "abc": 3 
  },                  },                      },         
                      "xyz": 4,               "xyz": 4,
  "list": [           "list": [               "list": [
	{                   {                       { 
        "x": "!"                                  "x": "!",
                          "y": ",",               "y": ",",
                          "z": "%"                "z": "%" 
	},                  }                       },
    { "y": "/" }                                { "y": "/" }
  ]                   ]                       ]
}                   }                       }            
```

### List merging
You can use `NBT.merge(a, b)` to merge two lists into one, deeply.
Compounds will be merged too.

Algorithm:
```kotlin
val c = a
for (index in b) {
	if (index in a) {
		if (a[index] is Map && b[index] is Map) {
			c[index] = merge(a[index], b[index])
		}
		else if (a[index] is List && b[index] is List) {
			c[index] = merge(a[index], b[index])
		}
		else {
			c[index] = b[index]
		}
	} else {
		c[index] = b[index]
	}
}
```

#### Demonstration
```kotlin
[            [                  [
  [            [                  [
    0,           10,                10,
    1,           11,                11,
    2            12,                12,
                 5                  5
  ],     +     ],        =>       ],
  [            [                  [
    {            {                  {
      "x": 3                          "x": 3,
                   "z": 7             "z": 7
    },           }                  },
    {                               {
      "y": 4                          "y": 4
    }                               }
  ]            ],                 ],
               []                 []
]            ]                  ]
```

## Paths

## Additional Information
Type map:
- `Byte`: TAG_Byte
- `Short`: TAG_Short
- `Int`: TAG_Int
- `Long`: TAG_Long
- `Float`: TAG_Float
- `Double`: TAG_Double
- `ByteArray`: TAG_Byte_Array
- `String`: Tag_String
- `List`: TAG_List, values must be of a single valid NBT type
- `Map`: TAG_Compound, keys must be strings, values must be valid NBT types
- `IntArray`: TAG_Int_Array
- `LongArray`: TAG_Long_Array

For more detail, see [NBT Specification](https://wiki.vg/NBT#Specification).