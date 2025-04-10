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
    div(cls := "flex flex-col gap-4")(
      h2(cls := "text-2xl font-semibold text-green-800 mb-4")("Wholeness of this lesson"),
      p(cls := "text-gray-700 leading-relaxed")(
        """Red-black trees provide a solution to the
          |problem of unacceptably slow worst case
          |performance of binary search trees. This is
          |accomplished by introducing a new element:
          |nodes of the tree are colored red or black,
          |adhering to the balance condition for red-black
          |trees. The balance condition is maintained""".stripMargin
      ),
      div(cls := "mt-4 text-sm text-gray-500 italic")(
        "Text credit: Professor Prem Nair"
      ),
      
      // Current violation section
      div(cls := "mt-8 pt-6 border-t border-gray-200")(
        h3(cls := "text-xl font-semibold text-green-700 mb-3")("Current Violation"),
        div(cls := "bg-gray-50 p-4 rounded-lg")(
          state.violation match {
            case Some(violation) => 
              div(cls := "space-y-3")(
                p(cls := "font-medium text-gray-800")(s"Type: ${violation.toString.split('(').head}"),
                p(cls := "text-gray-700")(
                  """This violation occurs when a red-black tree property is broken.
                    |The system has detected an issue that needs to be resolved to
                    |maintain the tree's balance and performance characteristics.""".stripMargin
                ),
                // Resolution explanation
                div(cls := "mt-4")(
                  h4(cls := "text-lg font-medium text-green-700 mb-2")("How to resolve:"),
                  p(cls := "bg-green-50 border-l-4 border-green-500 p-4 rounded-r-md text-gray-800 leading-relaxed")(
                    violation.resolveDetail
                  )
                ),
                div(cls := "mt-2 p-3 bg-orange-50 border-l-4 border-orange-500 text-orange-700")(
                  "Use the 'Resolve' button in the control panel to fix this violation and restore the tree properties."
                )
              )
            case None =>
              p(cls := "text-gray-600")(
                "No violations detected. The tree currently satisfies all red-black properties."
              )
          }
        )
      )
    )
  }

  def controlPanel(state: Model): Html[Msg] =
    val violation = state.violation
    val currentInputValue = state.input
    val hasViolation = violation.isDefined
    
    div(cls := "flex flex-col gap-4 bg-white rounded-lg shadow-md p-6 h-full")(
      // Violation status/resolve section
      div(
        cls := "w-full p-4 rounded-lg transition-colors " + 
          (if (hasViolation) "bg-orange-50" else "bg-gray-50")
      )(
        violation match {
          case Some(n) => 
            button(
              cls := "w-full px-4 py-3 bg-orange-500 text-white rounded-md hover:bg-orange-600 transition-colors", 
              onClick(Msg.ResolveViolation)
            )(s"Resolve violation")
          case None => 
            div(cls := "text-gray-600 text-center")("No violations found")
        }
      ),
      
      // Main controls section
      div(cls := "flex flex-col gap-4 w-full")(
        button(
          cls := s"w-full px-4 py-3 rounded-md transition-colors ${
            if (hasViolation) 
              "bg-gray-400 text-gray-200 cursor-not-allowed" 
            else 
              "bg-green-600 text-white hover:bg-green-700"
          }",
          onClick(Msg.InsertRandom),
          disabled(hasViolation)
        )("Insert Random"),
        
        div(cls := "flex flex-col gap-3 w-full")(
          input(
            cls := s"w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none ${
              if (hasViolation) 
                "bg-gray-100 text-gray-400" 
              else 
                "focus:ring-2 focus:ring-green-500 focus:border-transparent"
            }",
            value := currentInputValue,
            onChange(Msg.UpdateInputValue(_)),
            disabled(hasViolation)
          ),
          button(
            cls := s"w-full px-4 py-3 rounded-md transition-colors ${
              if (hasViolation) 
                "bg-gray-400 text-gray-200 cursor-not-allowed" 
              else 
                "bg-green-600 text-white hover:bg-green-700"
            }",
            onClick(Msg.InsertInput),
            disabled(hasViolation)
          )("Insert")
        )
      )
    )
