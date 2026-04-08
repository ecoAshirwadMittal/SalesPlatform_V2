# JSON Structure: JSON_ShipmentData_MultipleSKU

## Sample JSON

```json
{
  "shipment": {
    "id": "296",
    "businessUnit": "ECOATM",
    "tradingPartner": "22379",
    "type": "Outbound Shipment",
    "facility": "Louisville Facility",
    "createdBy": "tasneem.amina",
    "shipmentNumber": "296",
    "shipmentStatus": "Shipped",
    "shipVia": "FedEx Ground",
    "shipVendor": "FEDEX2",
    "actualShipDate": "2025-07-30T15:11:40-05:00",
    "billToAddress": {
      "name": "",
      "addressLine1": "",
      "addressLine2": "",
      "addressLine3": "",
      "addressLine4": "",
      "city": "",
      "stateProvinceCode": "",
      "postalCode": "",
      "countryCode": ""
    },
    "shipToAddress": {
      "name": "Receiving",
      "contactName": "Manager",
      "email": "sales@ahmcellular.us",
      "phone": "0000000000",
      "addressLine1": "2153 Bath Ave.",
      "addressLine2": "  ",
      "addressLine3": "",
      "addressLine4": "",
      "city": "Brooklyn",
      "stateProvinceCode": "NY",
      "postalCode": "11214",
      "countryCode": "US"
    },
    "shipFromAddress": {
      "attention": "Halea Bailey",
      "phone": "5555555555",
      "addressLine1": "7213 Global Dr",
      "city": "Louisville",
      "stateProvinceCode": "KY",
      "postalCode": "40258",
      "countryCode": "US"
    },
    "notes": null,
    "orders": {
      "order": {
        "orderNumber": "5002407",
        "customerOrderNumber": "PWS-2237925013",
        "orderType": "Sales Order",
        "orderStatus": "Complete",
        "shippingStatus": "20",
        "orderSource": "PWS"
      }
    },
    "lines": {
      "line": [
        {
          "lineNumber": "5002407-1",
          "lineStatus": "Complete",
          "customerLineNumber": "1",
          "shipmentLineNumber": "296--1",
          "itemNumber": "PWS000330855471-WMS1",
          "productCategory": "CellPhone",
          "orderPackQuantity": "2.0",
          "shippedPackQuantity": "1.0",
          "serialNumber": "98765432103215",
          "trackingNumber": "391596590792",
          "pack": {
            "type": "Each",
            "quantity": "1",
            "dimension": {
              "@nil": "true"
            },
            "upcs": {
              "@nil": "true"
            },
            "customMappings": {
              "@nil": "true"
            }
          },
          "container": {
            "lpn": "PWS_Order-250729_10",
            "number": "PWS_Order-250729_10",
            "trackingNumber": "391596590792",
            "status": "Shipped",
            "description": "PWS_Order-250729_10 is packed into PWS_Order-250729_10",
            "dimension": {
              "length": "5.0",
              "width": "7.0",
              "height": "3.0",
              "volume": "0.0",
              "units": "IN"
            },
            "weight": {
              "weight": "1.96",
              "units": "LB"
            },
            "totalCharges": "0.0",
            "referenceNumber1": "250730-1501-92",
            "depthUOM": "Inch",
            "type": "Shipping"
          },
          "inventoryAttribute1": "98765432103215"
        },
        {
          "lineNumber": "5002407-1",
          "lineStatus": "Complete",
          "customerLineNumber": "1",
          "shipmentLineNumber": "296--2",
          "itemNumber": "PWS000330855471-WMS1",
          "productCategory": "CellPhone",
          "orderPackQuantity": "2.0",
          "shippedPackQuantity": "1.0",
          "serialNumber": "98765432103216",
          "trackingNumber": "391596590792",
          "pack": {
            "type": "Each",
            "quantity": "1",
            "dimension": {
              "@nil": "true"
            },
            "upcs": {
              "@nil": "true"
            },
            "customMappings": {
              "@nil": "true"
            }
          },
          "container": {
            "lpn": "PWS_Order-250729_10",
            "number": "PWS_Order-250729_10",
            "trackingNumber": "391596590792",
            "status": "Shipped",
            "description": "PWS_Order-250729_10 is packed into PWS_Order-250729_10",
            "dimension": {
              "length": "5.0",
              "width": "7.0",
              "height": "3.0",
              "volume": "0.0",
              "units": "IN"
            },
            "weight": {
              "weight": "1.96",
              "units": "LB"
            },
            "totalCharges": "0.0",
            "referenceNumber1": "250730-1501-92",
            "depthUOM": "Inch",
            "type": "Shipping"
          },
          "inventoryAttribute1": "89568596996689"
        },
        {
          "lineNumber": "5002407-2",
          "lineStatus": "Complete",
          "customerLineNumber": "2",
          "shipmentLineNumber": "296--3",
          "itemNumber": "PWS014844808083",
          "productCategory": "CellPhone",
          "orderPackQuantity": "2.0",
          "shippedPackQuantity": "1.0",
          "serialNumber": "354872888261744",
          "trackingNumber": "391596590792",
          "pack": {
            "type": "Each",
            "quantity": "1",
            "dimension": {
              "@nil": "true"
            },
            "upcs": {
              "@nil": "true"
            },
            "customMappings": {
              "@nil": "true"
            }
          },
          "container": {
            "lpn": "PWS_Order-250729_10",
            "number": "PWS_Order-250729_10",
            "trackingNumber": "391596590792",
            "status": "Shipped",
            "description": "PWS_Order-250729_10 is packed into PWS_Order-250729_10",
            "dimension": {
              "length": "5.0",
              "width": "7.0",
              "height": "3.0",
              "volume": "0.0",
              "units": "IN"
            },
            "weight": {
              "weight": "1.96",
              "units": "LB"
            },
            "totalCharges": "0.0",
            "referenceNumber1": "250730-1501-92",
            "depthUOM": "Inch",
            "type": "Shipping"
          },
          "inventoryAttribute1": "354872888261744",
          "inventoryAttribute2": "pass"
        },
        {
          "lineNumber": "5002407-2",
          "lineStatus": "Complete",
          "customerLineNumber": "2",
          "shipmentLineNumber": "296--4",
          "itemNumber": "PWS014844808083",
          "productCategory": "CellPhone",
          "orderPackQuantity": "2.0",
          "shippedPackQuantity": "1.0",
          "serialNumber": "354029288914329",
          "trackingNumber": "391596590792",
          "pack": {
            "type": "Each",
            "quantity": "1",
            "dimension": {
              "@nil": "true"
            },
            "upcs": {
              "@nil": "true"
            },
            "customMappings": {
              "@nil": "true"
            }
          },
          "container": {
            "lpn": "PWS_Order-250729_10",
            "number": "PWS_Order-250729_10",
            "trackingNumber": "391596590792",
            "status": "Shipped",
            "description": "PWS_Order-250729_10 is packed into PWS_Order-250729_10",
            "dimension": {
              "length": "5.0",
              "width": "7.0",
              "height": "3.0",
              "volume": "0.0",
              "units": "IN"
            },
            "weight": {
              "weight": "1.96",
              "units": "LB"
            },
            "totalCharges": "0.0",
            "referenceNumber1": "250730-1501-92",
            "depthUOM": "Inch",
            "type": "Shipping"
          },
          "inventoryAttribute1": "354029288914329",
          "inventoryAttribute2": "pass"
        }
      ]
    },
    "trackingNumber": "391596590792",
    "trackingUrl": "https://www.fedex.com/fedextrack/?trknbr=391596590792",
    "shippingCost": "0.0",
    "publishedRate": "0.0",
    "extendedShippingCost": {
      "@nil": "true"
    },
    "weight": {
      "weight": "1.96",
      "units": "LB"
    },
    "saturdayDelivery": "false",
    "deliveryConfirmation": "0",
    "dropShip": "false",
    "residential": "false",
    "insuranceRequired": "false",
    "taxCost": "0.0",
    "codAmount": "0.0"
  }
}
```
