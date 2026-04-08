# Java Action: StartImportByTemplate

> Start the excel import, The parameter TemplateObject should be a valid parameter, this template is analysed and by this template configuration the filedocument (which should be an excel .xls file) The last parameter is the ImportObjectParameter, when an 'reference to imported object' is configured this parameter should containt the object to which all objects should refer to.This parameter is optional and can be left empty. The return value is irrelevant and will be always true

**Returns:** `Integer`

## Parameters

| Name | Type | Required |
|---|---|---|
| `TemplateObject` | ConcreteEntity | ✅ |
| `ImportExcelDoc` | ConcreteEntity | ✅ |
| `ImportObjectParameter` | ParameterizedEntity | ✅ |
