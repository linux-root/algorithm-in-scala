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
    div(cls := "flex flex-col min-h-screen")(
      // Header
      div(cls := "w-full bg-gradient-to-r from-green-600 to-green-800 py-8 px-6 mb-6")(
        h1(
          cls := "text-4xl font-bold text-white text-center tracking-wide"
        )(t)
      ),
      // Main content with horizontal layout
      div(cls := "flex flex-row gap-6 px-6")(
        // BST Tree visualization - fixed size
        div(
          id := "tuesday",
          cls := "w-[1000px] h-[800px] flex-shrink-0 bg-white rounded-lg shadow-md p-6 transition-shadow duration-300 hover:shadow-lg"
        )(),
        // Right side container - remaining space
        div(cls := "flex flex-row flex-1 gap-6")(
          // Explanation section - 2/3 of remaining space
          div(cls := "w-2/3 bg-white rounded-lg shadow-md p-6")(
            explaination(state)
          ),
          // Control panel - 1/3 of remaining space
          div(cls := "w-1/3")(
            controlPanel(state)
          )
        )
      )
    )
  }

  def explaination(state: Model): Html[Msg] = {
    div()
  }

  def controlPanel(state: Model): Html[Msg] =
    val violation = state.violation
    val currentInputValue = state.input
    div(cls := "flex flex-col gap-4 bg-white rounded-lg shadow-md p-6 h-full")(
      // Violation status/resolve section
      div(
        cls := "w-full p-4 rounded-lg transition-colors " + 
          (if (violation.isDefined) "bg-orange-50" else "bg-gray-50")
      )(
        violation match {
          case Some(n) => 
            button(
              cls := "w-full px-4 py-3 bg-orange-500 text-white rounded-md hover:bg-orange-600 transition-colors", 
              onClick(Msg.ResolveViolation)
            )(s"Resolve ${n}(${n.node.value})")
          case None => 
            div(cls := "text-gray-600 text-center")("No violations found")
        }
      ),
      
      // Main controls section
      div(cls := "flex flex-col gap-4 w-full")(
        button(
          cls := "w-full px-4 py-3 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors",
          onClick(Msg.InsertRandom)
        )("Insert Random"),
        
        div(cls := "flex flex-col gap-3 w-full")(
          input(
            cls := "w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent",
            value := currentInputValue,
            onChange(Msg.UpdateInputValue(_))
          ),
          button(
            cls := "w-full px-4 py-3 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors",
            onClick(Msg.InsertInput)
          )("Insert")
        )
      )
    )
