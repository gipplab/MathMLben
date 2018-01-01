(* ::Package:: *)

texString=$ScriptCommandLine[[2]]
tex=ToExpression[texString,TeXForm,Defer]
Print@ExportString[tex,"MathML","Content"->True]
