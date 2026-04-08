# Java Action: ExecuteOQLStatement

> This action executes a given OQL statement and accepts parameters. One can specify using the preserveParameters argument if parameters should be reset after invocation of this action. Statements can easily be developed using the DataSet and one can use the Mendix documentation (https://docs.mendix.com/refguide7/oql) as reference. For each column, the action expects an attribute in the result entity with the same name. If the result is the ID of an object, it expects an association with the same name (without the module prefix). Parameters given should be a list of OQL.Parameter, having at least the ParameterName and ParameterType set. ParameterNames follow the syntax $Name Example query (taken from OQL.IVK_PerformTests): SELECT P.id ExamplePersonResult_ExamplePerson, P.Name Name, P.Number Number, P.DateOfBirth DateOfBirth, P.Age Age, P.LongAge LongAge, P.HeightInFloat HeightInFloat, P.HeightInDecimal HeightInDecimal, P.Active Active, P.Gender Gender FROM OQL.ExamplePerson P WHERE P.Active = $Active AND P.Age = $Age AND P.DateOfBirth = $DateOfBirth AND P.Gender = $Gender AND P.HeightInDecimal = $HeightInDecimal AND P.HeightInFloat = $HeightInFloat AND P.LongAge = $LongAge AND P.Name = $Name AND P.Number = $Number

**Returns:** `List`

## Parameters

| Name | Type | Required |
|---|---|---|
| `statement` | String | ✅ |
| `returnEntity` | Unknown | ✅ |
| `amount` | Integer | ✅ |
| `offset` | Integer | ✅ |
| `preserveParameters` | Boolean | ✅ |
