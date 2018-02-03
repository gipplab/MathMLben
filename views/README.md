# MathMLben

[![Build Status](https://travis-ci.org/ag-gipp/GoUldI.svg?branch=master)](https://travis-ci.org/ag-gipp/GoUldI)
[![Maintainability](https://api.codeclimate.com/v1/badges/1a369c013f69caa8b3ac/maintainability)](https://codeclimate.com/github/ag-gipp/GoUldI/maintainability)

MathMLben is an available benchmark dataset to the evaluate tools for mathematical format conversion (LaTeX <-> MathML <-> CAS).
The Gold Standard comprises 305 mathematical formulae (1-100 extracted from the NTCIR 11 Math Wikipedia, 101-200 from the
NIST Digital Library of Mathematical Functions (DLMF), 201-305 from the NTCIR arXiv and NTCIR-12 Wikipedia datasets
Task).

GUI to makes changes to the data available: https://gouldi.wmflabs.org
with input fields for formula name and type (definition, equation, relation or general formula), original and corrected TeX, hyperlink to the original formula (source) and most importantly a semantic Tex field for annotations (DLMF macros, Wikidata QIDs).
The expression tree preview visualization is provided by VMEXT (https://github.com/ag-gipp/vmext).

Anotations of Wikidata items (QIDs) can be made via the macro \w{Q...} for a general mathematical expression, \wf{Q...} for a function or \wdef{Q...} at the beginning of the formula. Multiple annotation of the same token is dispensable. Furthermore you can create new macros at https://github.com/ag-gipp/GoUldI/blob/master/config/latexml/wikidata.sty.ltxml. Be careful that the quotation marks '' are balanced and survey the travis build status on https://travis-ci.org/ag-gipp/GoUldI/branches.

We gladly invite experts that are able to judge the correctness of the formula, its name and type as well as the semantic annotations and the expression tree. Controlling and correcting the Gold Standard by setting the Tree State and QID State flags or adapting some of the input fields if necessary is highly welcome and helps to enable and improve the conversion between the diverse formats of mathemathematical notation available today (LaTeX, MathML and various CAS).

If you estimate a problem with the annotation (uncertainty or ambiguity), the formula name and type, the expression tree or MathML to be in need of discussion, please create an issue on github: https://github.com/ag-gipp/GoUldI/issues.
