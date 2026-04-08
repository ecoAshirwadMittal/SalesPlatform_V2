# Snippet: Snippet_BarChart_ProcessedTasksByQueue

## Widget Tree

- 📦 **DataView** [MF: TaskQueueHelpers.RetrieveChartContext]
  - 🧩 **Column chart** (ID: `com.mendix.widget.web.columnchart.ColumnChart`)
      ➤ **series**
          - dataSet: static
          - staticName: Completed
          - staticXAttribute: [Attr: TaskQueueHelpers.ProcessedQueueCount.QueueName]
          - staticYAttribute: [Attr: TaskQueueHelpers.ProcessedQueueCount.CompletedCount]
          - aggregationType: none
          - dataSet: static
          - staticName: Uncompleted
          - staticXAttribute: [Attr: TaskQueueHelpers.ProcessedQueueCount.QueueName]
          - staticYAttribute: [Attr: TaskQueueHelpers.ProcessedQueueCount.UncompletedCount]
          - aggregationType: none
      - xAxisLabel: Task queue
      - yAxisLabel: Count
      - gridLines: none
      - barmode: group
      - widthUnit: percentage
      - width: 100
      - heightUnit: percentageOfWidth
      - height: 75
