# Java Action: EnumerationFromString

> Use this Java action as a template for your own String-to-Enumeration conversions. Studio Pro requires specifying the exact Enumeration to return in the definition of a Java action so we cannot provide a generic implementation. This implementation will throw a NoSuchElementException if an invalid toConvert parameter is given, so remember to handle this error gracefully.

**Returns:** `Enumeration`

## Parameters

| Name | Type | Required |
|---|---|---|
| `toConvert` | String | ✅ |
