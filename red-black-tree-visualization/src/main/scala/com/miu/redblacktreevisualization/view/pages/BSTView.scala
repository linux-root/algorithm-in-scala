package com.miu.redblacktreevisualization.view.pages

import tyrian.Html
import tyrian.Html.*
import com.miu.redblacktreevisualization.model.Msg
import com.miu.redblacktreevisualization.model.Msg.InsertRandom
import com.miu.redblacktreevisualization.core.BST
import com.miu.redblacktreevisualization.model.Model

object BSTView:

  def apply(state: Model): Html[Msg] = {
    val t = "Red Black Tree Visualization"
    div(cls := "flex flex-col")(
      h1()(t),
      controlPanel(state),
      div(
        id := "tuesday",
        cls := "bg-white rounded-lg shadow-md p-6 m-5 min-h-[600px] transition-shadow duration-300 hover:shadow-lg"
      )()
    )
  }

  def controlPanel(state: Model): Html[Msg] =
    val violation = state.violation
    val currentInputValue = state.input
    div(cls := "flex flex-col gap-4 p-6 bg-white rounded-lg shadow-sm m-5")(
      // Violation status/resolve section
      div(cls := "flex items-center p-4 rounded-lg " + (if (violation.isDefined) "bg-orange-50" else "bg-gray-50"))(
        violation match {
          case Some(n) => 
            button(
              cls := "px-4 py-2 bg-orange-500 text-white rounded-md hover:bg-orange-600 transition-colors", 
              onClick(Msg.ResolveViolation)
            )(s"Resolve ${n}(${n.node.value})")
          case None => 
            span(cls := "text-gray-600")("No violations found")
        }
      ),
      
      // Main controls section
      div(cls := "flex gap-4 items-center")(
        button(
          cls := "px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors",
          onClick(Msg.InsertRandom)
        )("Insert Random"),
        
        div(cls := "flex gap-2 items-center flex-1")(
          input(
            cls := "flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent",
            value := currentInputValue,
            onChange(Msg.UpdateInputValue(_))
          ),
          button(
            cls := "px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors",
            onClick(Msg.InsertInput)
          )("Insert")
        )
      )
    )
