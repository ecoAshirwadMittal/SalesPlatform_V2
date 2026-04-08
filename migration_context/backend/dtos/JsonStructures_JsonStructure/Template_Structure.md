# JSON Structure: Template_Structure

## Sample JSON

```json
{
  "Name": "DefaultTemplate",
  "Description": "A basic template",
  "DocumentType": "Excel",
  "CSVSeparator": ",",
  "DateTimePresentation": "YYYY-MM-DD HH:MM:SS",
  "CustomDateFormat": "DD-MM-YYYY",
  "QuotationCharacter": "\"",
  "MxTemplate_InputObject": {
    "CompleteName": "",
    "Name": "",
    "Module": ""
  },
  "MxCellStyle": [
    {
      "Name": "DefaultStyle",
      "TextBold": false,
      "TextItalic": false,
      "TextUnderline": false,
      "TextAlignment": "left",
      "TextVerticalalignment": "top",
      "TextColor": "#000000",
      "TextHeight": 10,
      "BackgroundColor": "#FFFFFF",
      "TextRotation": 0,
      "WrapText": true,
      "BorderTop": 1,
      "BorderBottom": 1,
      "BorderLeft": 1,
      "BorderRight": 1,
      "BorderColor": "#000000",
      "Format": "General",
      "DecimalPlaces": 0,
      "DataFormatString": "@",
      "MxSheet_HeaderStyle": [
        {
          "Name": "Sheet1",
          "Sequence": 1
        }
      ],
      "MxSheet_DefaultStyle": [
        {
          "Name": "Sheet1",
          "Sequence": 1
        }
      ]
    }
  ],
  "MxSheet": [
    {
      "Sequence": 1,
      "Name": "Sheet1",
      "DataUsage": true,
      "Status": "Active",
      "DistinctData": false,
      "StartRow": 1,
      "ColumnWidthDefault": true,
      "ColumnWidthPixels": 80,
      "RowHeightDefault": true,
      "RowHeightPoint": 15,
      "FormLayout_GroupBy": false,
      "MxRowSettings_MxSheet": [
        {
          "RowIndex": 1,
          "DefaultHeight": true,
          "RowHeight": 15
        }
      ],
      "MxColumnSettings_MxSheet": [
        {
          "ColumnIndex": 1,
          "AutoSize": false,
          "ColumnWidth": 100
        }
      ],
      "MxReferenceHandling_MxSheet_RowObject": [
        {
          "Reference": "Sheet1",
          "JoinType": "Inner"
        }
      ],
      "MxSorting_MxSheet": [
        {
          "Sequence": 1,
          "Summary": "Sort by Name",
          "Attribute": "Name",
          "SortingDirection": "Ascending",
          "MxSorting_MxXPath": {
            "RetrieveType": "Attribute",
            "SubVisible": true,
            "Exported_ParentXPath_MxXPath": [
              {
                "JSONArray": ""
              }
            ],
            "MxXPath_MxObjectMember": {
              "CompleteName": ""
            },
            "MxXPath_MxObjectReference": {
              "CompleteName": "",
              "Name": "",
              "Module": ""
            },
            "MxXPath_MxObjectType": {
              "CompleteName": "",
              "Name": "",
              "Module": ""
            }
          }
        }
      ],
      "MxConstraint_MxSheet": [
        {
          "Sequence": 1,
          "Summary": "Check for positive values",
          "Attribute": "Value",
          "Constraint": "> 0",
          "AttributeType": "",
          "ConstraintText": "Logical",
          "ConstraintNumber": 0,
          "ConstraintDecimal": 11.11,
          "ConstraintDateTime": "",
          "ConstraintBoolean": true,
          "AndOr": "AND",
          "MxConstraint_MxXPath": {
            "RetrieveType": "Attribute",
            "SubVisible": true,
            "Exported_ParentXPath_MxXPath": [
              {
                "JSONArray": ""
              }
            ],
            "MxXPath_MxObjectMember": {
              "CompleteName": ""
            },
            "MxXPath_MxObjectReference": {
              "CompleteName": "",
              "Name": "",
              "Module": ""
            },
            "MxXPath_MxObjectType": {
              "CompleteName": "",
              "Name": "",
              "Module": ""
            }
          }
        }
      ],
      "MxSheet_RowObject": {
        "CompleteName": "",
        "Name": "",
        "Module": ""
      },
      "MxSheet_MxObjectReference": {
        "CompleteName": "",
        "Name": "",
        "Module": ""
      },
      "MxColumn": [
        {
          "Name": "Data1",
          "Status": "Available",
          "ColumnNumber": 1,
          "ObjectAttribute": "Value",
          "DataAggregate": false,
          "DataAggregateFunction": "",
          "ResultAggregate": false,
          "ResultAggregateFunction": "",
          "MxData_MxCellStyle": {
            "Name": "style name"
          },
          "MxXPath_MxData": {
            "RetrieveType": "Attribute",
            "SubVisible": true,
            "Exported_ParentXPath_MxXPath": [
              {
                "JSONArray": ""
              }
            ],
            "MxXPath_MxObjectMember": {
              "CompleteName": ""
            },
            "MxXPath_MxObjectReference": {
              "CompleteName": "",
              "Name": "",
              "Module": ""
            },
            "MxXPath_MxObjectType": {
              "CompleteName": "",
              "Name": "",
              "Module": ""
            }
          }
        }
      ],
      "MxStatic": [
        {
          "Name": "Data1",
          "Status": "Available",
          "ColumnPlace": 1,
          "RowPlace": 1,
          "StaticType": "Constant",
          "AggregateFunction": "Sum",
          "MxStatic_MxColumn": {
            "Name": "",
            "ColumnNumber": 1
          },
          "MxData_MxCellStyle": {
            "Name": "style name"
          },
          "MxStatic_MxObjectMember": {
            "CompleteName": ""
          },
          "MxXPath_MxData": {
            "RetrieveType": "Attribute",
            "SubVisible": true,
            "Exported_ParentXPath_MxXPath": [
              {
                "JSONArray": ""
              }
            ],
            "MxXPath_MxObjectMember": {
              "CompleteName": ""
            },
            "MxXPath_MxObjectReference": {
              "CompleteName": "",
              "Name": "",
              "Module": ""
            },
            "MxXPath_MxObjectType": {
              "CompleteName": "",
              "Name": "",
              "Module": ""
            }
          }
        }
      ]
    }
  ]
}
```
