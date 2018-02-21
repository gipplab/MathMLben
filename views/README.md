# MathMLben

[![Build Status](https://travis-ci.org/ag-gipp/MathMLben.svg?branch=master)](https://travis-ci.org/ag-gipp/MathMLben) [![Maintainability](https://api.codeclimate.com/v1/badges/1a369c013f69caa8b3ac/maintainability)](https://codeclimate.com/github/ag-gipp/MathMLben/maintainability)

MathMLben is a benchmark to the evaluate tools for mathematical format conversion (LaTeX <-> MathML <-> CAS).
The Gold Standard comprises 305 mathematical formulae:
* 1-100 extracted from the NTCIR 11 Math Wikipedia
* 101-200 from the NIST Digital Library of Mathematical Functions (DLMF)
* 201-305 from the NTCIR arXiv and NTCIR-12 Wikipedia datasets

An overview of the sources can be found at [dataset table](https://mathmlben.wmflabs.org/dataset).

A GUI to make changes to the data is available at [wmflabs](https://mathmlben.wmflabs.org) with the following input fields:
* formula name
* formula type (definition, equation, relation or general formula)
* original TeX and 
* corrected TeX,
* hyperlink to the original formula (source) and
* semantic Tex field for annotations (DLMF macros, Wikidata QIDs).

The expression tree preview visualization is provided by [VMEXT](https://vmext.formulasearchengine.com).

Anotations of Wikidata items (QIDs) can be made via 
* the TeX-macro `\w{Q...}` for a general mathematical expression,
* `\wf{Q...}` for a function or 
* `\wdef{Q...}` at the beginning of the formula. 

Multiple annotation of the same token is dispensable.
DLMF LaTeX macros (see the CICM lecture notes of Watt et al., DOI 10.1007/978-3-319-08434-3), e.g. `\EulerGamma@{z}` for the gamma function or `\JacobiP{\alpha}{\beta}{n}@{x}` for the Jacobi polynomial, are interpreted.

Furthermore you can create new macros at in the [latexml style file](https://github.com/ag-gipp/MathMLben/blob/master/config/latexml/wikidata.sty.ltxml).
Be careful that the quotation marks '' are balanced and survey the travis build status on https://travis-ci.org/ag-gipp/GoUldI/branches.

We gladly invite experts that are able to judge the correctness of the formula, its name and type as well as the semantic annotations and the expression tree.
Controlling and correcting the Gold Standard by setting the Tree State and QID State flags or adapting some of the input fields if necessary is highly welcome and helps to enable and improve the conversion between the diverse formats of mathematical notation available today (LaTeX, MathML and various CAS).

If you estimate a problem with the annotation (uncertainty or ambiguity), the formula name and type, the expression tree or MathML to be in need of discussion, please create an [issue](https://github.com/ag-gipp/MathMLBen/issues).
