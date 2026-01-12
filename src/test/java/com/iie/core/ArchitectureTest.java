package com.iie.core;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.iie.core", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule order_should_not_access_inventory_internal =
        noClasses()
            .that().resideInAPackage("..order..")
            .should().accessClassesThat().resideInAPackage("..inventory.internal..");

    @ArchTest
    static final ArchRule inventory_should_not_access_order_internal =
        noClasses()
            .that().resideInAPackage("..inventory..")
            .should().accessClassesThat().resideInAPackage("..order.internal..");

    @ArchTest
    static final ArchRule shared_should_not_contain_business_logic =
        noClasses()
            .that().resideInAPackage("..shared..")
            .should().dependOnClassesThat().resideInAPackage("..inventory..")
            .orShould().dependOnClassesThat().resideInAPackage("..order..");
}
