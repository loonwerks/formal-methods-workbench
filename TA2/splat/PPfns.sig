signature PPfns = 
sig

 type pretty = PolyML.pretty

  val emptyString : pretty
  val emptyBlock : pretty
  val emptyBreak : pretty
  val Space : pretty
  val Comma : pretty
  val Semicolon : pretty
  val openBrace : pretty
  val closeBrace : pretty
  val openParen : pretty
  val closeParen : pretty
  val openBracket : pretty
  val closeBracket : pretty
  val Line_Break : pretty
  val Line_Break_2 : pretty

  val iter_pp : 'a -> 'a list -> ('b -> 'a) -> 'b list -> 'a list
  val end_sep_list : 'a -> 'a -> ('b -> 'a) -> 'b list -> 'a list
  val pp_list_with_style : bool -> pretty -> pretty list -> ('a -> pretty) -> 'a list -> pretty
  val gen_pp_list : pretty -> pretty list -> ('a -> pretty) -> 'a list -> pretty
  val end_pp_list : pretty -> pretty -> ('a -> pretty) -> 'a list -> pretty
  val pp_comma_list : ('a -> pretty) -> 'a list -> pretty
  val pp_semicolon_list : ('a -> pretty) -> 'a list -> pretty

  val spliceList : 'a -> 'a list -> 'a list
end
