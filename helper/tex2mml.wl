(* ::Package:: *)

texString=$ScriptCommandLine[[2]]
tex=ToExpression["a^2=b^2",TeXForm,Defer]
Print@ExportString[tex,"MathML","Content"->True]
