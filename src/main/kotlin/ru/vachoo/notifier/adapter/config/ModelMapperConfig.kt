package ru.vachoo.notifier.adapter.config

import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneOffset
import org.modelmapper.Converter
import org.modelmapper.ModelMapper
import org.modelmapper.spi.MappingContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {

  @Bean
  fun modelMapper(): ModelMapper {
    val mapper = ModelMapper()

    mapper.createTypeMap(OffsetTime::class.java, LocalTime::class.java).converter =
      offsetToLocalTimeConverterFabric()
    mapper.createTypeMap(LocalTime::class.java, OffsetTime::class.java).converter =
      localToOffsetTimeConverterFabric()

    return mapper
  }

  private fun offsetToLocalTimeConverterFabric(): Converter<OffsetTime, LocalTime> =
    Converter { context: MappingContext<OffsetTime, LocalTime> ->
      context.source?.toLocalTime()
    }

  private fun localToOffsetTimeConverterFabric(): Converter<LocalTime, OffsetTime> =
    Converter { context: MappingContext<LocalTime, OffsetTime> ->
      OffsetTime.of(context.source, ZoneOffset.UTC)
    }
}
