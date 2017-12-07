package com.thoughtworks.binding.website

import com.thoughtworks.binding.dom
import com.thoughtworks.binding.Binding.{ Constants, Var, Vars, BindingSeq }

import org.scalajs.dom.document

import scala.scalajs.js.annotation.JSExport

@JSExport
object Hello {

	@dom
  def render = {
    val logs = Seq("Hello", "Binding.scala")
    <div>
      { Constants(logs:_*) map { it => <div>{ it }</div>}  }
    </div>
  }

  @JSExport
  def main(): Unit = {
    dom.render(document.body, render)
  }

}
