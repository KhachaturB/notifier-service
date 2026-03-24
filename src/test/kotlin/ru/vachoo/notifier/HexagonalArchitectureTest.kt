package ru.vachoo.notifier

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class HexagonalArchitectureTest {

  private val importedClasses = ClassFileImporter().importPackages("ru.vachoo.notifier..")

  @Test
  fun domainLayer_ShouldNotDependOnApplicationLayer() {
    val rule =
      noClasses()
        .that()
        .resideInAPackage("ru.vachoo.notifier.domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("ru.vachoo.notifier.application..")

    rule.check(importedClasses)
  }

  @Test
  fun domainLayer_ShouldNotDependOnAdapterLayer() {
    val rule =
      noClasses()
        .that()
        .resideInAPackage("ru.vachoo.notifier.domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("ru.vachoo.notifier.adapter..")

    rule.check(importedClasses)
  }

  @Test
  fun applicationLayer_ShouldNotDependOnAdapterLayer() {
    val rule =
      noClasses()
        .that()
        .resideInAPackage("ru.vachoo.notifier.application..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("ru.vachoo.notifier.adapter..")

    rule.check(importedClasses)
  }
}
