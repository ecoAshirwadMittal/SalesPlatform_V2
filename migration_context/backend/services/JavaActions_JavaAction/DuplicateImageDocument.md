# Java Action: DuplicateImageDocument

> Clones the contents of one image document into another, and generates a thumbnail as well. - fileToClone : the source file - cloneTarget : an initialized file document, in which the file will be stored. Returns true if copied, returns file if the source had no contents, throws exception in any other case. Pre condition: HasContents of the 'fileToClone' need to be set to true, otherwise the action will not copy anything.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `fileToClone` | ConcreteEntity | ✅ |
| `cloneTarget` | ConcreteEntity | ✅ |
| `thumbWidth` | Integer | ✅ |
| `thumbHeight` | Integer | ✅ |
