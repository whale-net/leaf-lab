package dev.whalenet.leaf_lab

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.logs.SdkLoggerProvider
import io.opentelemetry.sdk.logs.`export`.BatchLogRecordProcessor
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter

object OpenTelemetryConfig {
  
  def init(): OpenTelemetry = {
    val otlpEndpoint = "http://0.0.0.0:4317"
    
    val logRecordExporter = OtlpGrpcLogRecordExporter.builder()
      .setEndpoint(otlpEndpoint)
      .build()
    
    val logRecordProcessor = BatchLogRecordProcessor.builder(logRecordExporter)
      .build()
    
    val sdkLoggerProvider = SdkLoggerProvider.builder()
      .addLogRecordProcessor(logRecordProcessor)
      .build()
    
    val openTelemetry = OpenTelemetrySdk.builder()
      .setLoggerProvider(sdkLoggerProvider)
      .buildAndRegisterGlobal()
    
    // Add shutdown hook to ensure proper cleanup
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      sdkLoggerProvider.close()
    }))
    
    openTelemetry
  }
}