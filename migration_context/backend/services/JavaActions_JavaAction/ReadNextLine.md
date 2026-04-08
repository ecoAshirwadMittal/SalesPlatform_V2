# Java Action: ReadNextLine

> Reads a next line from a CSV. This action should only be invoked from the microflow used by a ImportCSV action. If the action returns an empty object, the end of the file is reached. Take into account that the order of attributes filled of an entity will be ordered in alphabetical manner. An example usable entity layout would be: C01_Column1 C02_Column2 ... All attributes should be of the type String; thus specific parsing should be part of the microflow. This is implemented because declared primitives are returned in a different order than declared within the model.

**Returns:** `ParameterizedEntity`

## Parameters

| Name | Type | Required |
|---|---|---|
| `entity` | Unknown | ✅ |
