structure PPfns :> PPfns = 
struct

open PolyML;

val emptyBlock = PrettyBlock(0,true,[],[]);
val emptyString = PrettyString"";
val emptyBreak = PrettyBreak(0,0);
val Space = PrettyBreak(1,0);
val Comma = PrettyString",";
val Semicolon = PrettyString";";
val Line_Break = PrettyBreak(999,0);
val Line_Break_2 = PrettyBlock(0,true,[],[Line_Break,Space,Line_Break]);
val openBrace = PrettyString "{";
val closeBrace = PrettyString "}";
val openParen = PrettyString "(";
val closeParen = PrettyString ")";
val openBracket = PrettyString "[";
val closeBracket = PrettyString "]";

fun iter_pp sep brk pp list = 
 case list
  of [] => []
   | [x] => [pp x]
   | (h::t) => pp h :: sep :: (brk @ iter_pp sep brk pp t);

fun pp_list_with_style style sep brk pp list
  = PrettyBlock(0,style,[],iter_pp sep brk pp list)

fun gen_pp_list sep brk pp list = pp_list_with_style true sep brk pp list;

fun pp_comma_list pp = gen_pp_list Comma [Space] pp;

fun pp_semicolon_list pp = gen_pp_list Semicolon [Space] pp;

val pp_list = pp_semicolon_list;

fun end_sep_list sep brk pp list = 
 case list
  of [] => []
   | [x] => [pp x,sep]
   | (h::t) => pp h :: sep :: brk :: end_sep_list sep brk pp t;

fun end_pp_list sep brk pp list = PrettyBlock(0,true,[],end_sep_list sep brk pp list);

fun spliceList ob [] = []
  | spliceList ob [x] = [x,ob]
  | spliceList ob (h::t) = h::ob::spliceList ob t

end