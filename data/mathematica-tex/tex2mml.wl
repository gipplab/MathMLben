(* ::Package:: *)

files=FileNames["*.tex", NotebookDirectory[]];
inputs={ReadString[#],#}&/@files;
mmls={ToExpression[#[[1]],TeXForm,Defer],StringReplace[#[[2]],"tex"->"mml.xml"]}&/@inputs
WriteString[#[[2]],ExportString[#[[1]],"MathML","Content"->True]]&/@mmls
