package com.nrinaudo.tabulate.scalaz

import org.scalacheck.Properties
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ScalazSuite extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  def checkAll(name: String, props: Properties): Unit = {
    for ((id, prop) ← props.properties)
        test(name + "." + id) {
          check(prop)
        }
  }
}
