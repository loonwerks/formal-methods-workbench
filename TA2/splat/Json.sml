(*---------------------------------------------------------------------------*)
(* Type of Json expressions plus parser. Does not yet handle floating point  *)
(* numbers, or utf-8 strings.                                                *)
(*---------------------------------------------------------------------------*)

structure Json :> Json =
struct

open Lib Feedback;

type substring = Substring.substring

val ERR = Feedback.mk_HOL_ERR "Json";

datatype number
   = Int of int
   | Float of real (* Not totally sure about exact representation desired *)

datatype json
    = Null
    | LBRACK  (* stack symbol only *)
    | LBRACE  (* stack symbol only *)
    | Boolean of bool
    | Number of number     (* ints and floats *)
    | String of string     (* should be unicode strings, per JSON spec *)
    | List of json list
    | AList of (string * json) list;

(*---------------------------------------------------------------------------*)
(* Lexer                                                                     *)
(*---------------------------------------------------------------------------*)
		     
datatype lexeme
  = lbrace
  | rbrace 
  | lbrack
  | rbrack 
  | colon
  | comma
  | nullLit
  | boolLit of bool
  | numLit of number
  | stringLit of string

fun takeWhile P ss =
  let open Substring
      val (ls, ss') = splitl P ss
  in if isEmpty ls
      then NONE
      else SOME (string ls, ss')
  end

fun compose f NONE = NONE
  | compose f (SOME (x,y)) = f x y;

fun getNum ss =
 let fun toInt s =
       (if s = "" then NONE else 
        if String.sub(s,0) = #"-" then
           (case Int.fromString (String.extract(s,1,NONE))
             of SOME i => SOME (Int.~(i))
              |  NONE => NONE)
        else Int.fromString s
       )
      handle Overflow => NONE
 in
   compose 
      (fn s => fn ss' =>
        case toInt s 
         of SOME i => SOME(numLit (Int i),ss')
          |  NONE => NONE)
      (takeWhile (fn c => Char.isDigit c orelse c = #"-") ss)
 end;

fun getKeyword ss =
 compose (fn s => fn ss' =>
   case s
    of "null"  => SOME (nullLit,ss')
     | "true"  => SOME (boolLit true, ss')
     | "false" => SOME (boolLit false, ss')
     |  other  => NONE)
  (takeWhile Char.isAlpha ss);

fun getString strm list =
 let open Substring
 in case getc strm 
     of NONE => raise ERR "lex (in getString)" "end of input, looking for \""
      | SOME(#"\"",strm') => SOME(stringLit(String.implode(rev list)),strm')
      | SOME(#"\\",strm') => (* backslashed chars possible *)
          (case getc strm'
            of NONE => raise ERR "lex (in getString)" "unexpected end of input"
             | SOME(ch,strm'') => getString strm'' (ch :: #"\\"::list)
          )
      | SOME(ch,strm') =>  getString strm' (ch :: list)
 end;

fun lex strm = 
 let open Substring
 in 
   case getc strm
    of NONE => NONE
     | SOME (#"{",strm') => SOME(lbrace,strm')
     | SOME (#"}",strm') => SOME(rbrace,strm')
     | SOME (#"[",strm') => SOME(lbrack,strm')
     | SOME (#"]",strm') => SOME(rbrack,strm')
     | SOME (#",",strm') => SOME(comma,strm')
     | SOME (#":",strm') => SOME(colon,strm')
     | SOME (#"n",strm') => getKeyword strm  (* null *)
     | SOME (#"t",strm') => getKeyword strm  (* true *)
     | SOME (#"f",strm') => getKeyword strm  (* false *)
     | SOME (#"\"",strm') => getString strm' []
     | SOME (ch,strm') =>
         if Char.isSpace ch then
	     lex strm' 
         else
	     if Char.isDigit ch orelse ch = #"-" then
		 getNum strm
	     else raise ERR "lex"
		    ("unexpected character starts remaining input:\n"^
		     Substring.string strm)
 end

fun lexemes ss =
  (case lex ss of NONE => [] | SOME(l,ss') => l::lexemes ss')
  handle HOL_ERR e => (print (Feedback.format_ERR e); []);

(* 
lexemes (Substring.full "null [ \"foo\" : \"bar\" ]");
lexemes (Substring.full "{ \"foo\" : 12, \"bar\" : 13  }");
lexemes (Substring.full "[true,false, null, 123, -23, \"foo\"] ");
*)


(*---------------------------------------------------------------------------*)
(* Parsing                                                                   *)
(*---------------------------------------------------------------------------*)

fun PARSE_ERR s ss = 
 let open Substring
     val estring = String.concat
         ["Json parser failed!\n   ",s, 
          "\n   Remaining input: ", string ss, ".\n"]
 in 
   raise ERR "PARSE_ERR" estring
 end;

fun toList (LBRACK::t,ss) acc = (List acc::t,ss)
  | toList (h::t,ss) acc = toList (t,ss) (h::acc)
  | toList ([],ss) acc =
    raise PARSE_ERR "toList: empty stack when trying to build a compound" ss;

fun toAList (LBRACE::t,ss) acc = (AList acc::t,ss)
  | toAList (j::String s::t,ss) acc = toAList (t,ss) ((s,j)::acc)
  | toAList (_::_::_,ss) acc =
    raise PARSE_ERR "toAList: expected string literal in key-value pair" ss
  | toAList ([_],ss) acc =
    raise PARSE_ERR "toAList: unexpected key-value pair structure" ss
  | toAList ([],ss) acc =
    raise PARSE_ERR "toAList: empty stack when trying to build an object" ss;
					
(*---------------------------------------------------------------------------*)
(* The main parsing loop. Returns the final stack and the remaining input.   *)
(* The stack should be of length 1, and it will have a json element. The     *)
(* remaining input should be empty, or consist of whitespace.                *)
(*---------------------------------------------------------------------------*)

fun parse (stk,ss) = 
  case lex ss 
   of NONE => (rev stk, Substring.dropl Char.isSpace ss)
    | SOME (nullLit,ss')     => (Null::stk,ss')
    | SOME (boolLit b,ss')   => (Boolean b::stk,ss')
    | SOME (numLit i,ss')    => (Number i::stk,ss')
    | SOME (stringLit s,ss') => (String s::stk,ss')
    | SOME (lbrack,ss') => parse_list (LBRACK::stk,ss')
    | SOME (lbrace,ss') => parse_alist (LBRACE::stk,ss')
    | SOME other  => raise PARSE_ERR "unexpected lexeme" ss
and 
 parse_list (stk,ss) = (* list --> eps | elt (, elts)* *)
   case lex ss 
    of NONE => raise PARSE_ERR "parse_list: unexpected end of input" ss
     | SOME (rbrack,ss') => toList (stk,ss') []
     | SOME other => elts(stk,ss)
and
 parse_alist (stk,ss) = (* alist --> eps | strLit : val (, strLit : val)* *)
   case lex ss 
    of NONE => raise PARSE_ERR "parse_alist: unexpected end of input" ss
     | SOME (rbrace,ss') => toAList (stk,ss') []
     | SOME (stringLit _,_) => bindings (stk,ss)
     | other => raise PARSE_ERR "parse_alist: unexpected lexeme" ss
and
 elts (stk,ss) =
   let val (stk',ss') = parse (stk,ss)
   in case lex ss'
       of SOME(comma,ss'') => elts (stk',ss'')
        | SOME(rbrack,ss'') => toList (stk',ss'') []
        | SOME other => raise PARSE_ERR "parse_list: unexpected lexeme" ss'
        | NONE => raise PARSE_ERR "parse_list: unexpected end of input" ss'
   end
and
 bindings (stk,ss) =
  case lex ss
   of SOME (stringLit s,ss') =>
       (case lex ss'
         of SOME(colon, ss'') => 
	    let val (stk',ss3) = parse (String s::stk,ss'')
            in case lex ss3
	        of SOME(comma,ss4) => bindings (stk',ss4)
		 | SOME(rbrace,ss4) => toAList (stk',ss4) []
                 | other => raise PARSE_ERR "parse_alist: unexpected lexeme" ss3
	    end
	  | other => raise PARSE_ERR
			   "parse_alist: expect a colon after a string literal" ss'
       )
  | other => raise PARSE_ERR "parse_alist: expected a key-value pair" ss


(*
simple tests.

parse ([],Substring.full "[1, 23, 4]");
parse ([],Substring.full "{\"foo\" : 1, \"bar\" : 2}");
parse ([],Substring.full "{\"foo\" : [1, 23, 4], \"bar\" : 2}");
*)

(*---------------------------------------------------------------------------*)
(* Wrapped-up versions ready to use on a variety of types (substrings,       *)
(* strings, and files). These return (json list * substring)                 *)
(*---------------------------------------------------------------------------*)

fun fromSubstring ss = parse ([], ss);
 
val fromString = fromSubstring o Substring.full;

fun fromFile filename =
 let open TextIO Substring 
     val istrm = openIn filename
     val ss = full(inputAll istrm)
     val _ = closeIn istrm
 in
  fromSubstring ss
 end;

(*---------------------------------------------------------------------------*)
(* Called from an executable which wants to get <name>.json from the         *)
(* command line.                                                             *)
(*---------------------------------------------------------------------------*)

fun jsonFileName execName = 
 let fun printHelp() = print ("Usage: "^execName^" <name>.json\n")
     fun fail() = (printHelp(); MiscLib.fail())
 in case CommandLine.arguments()
     of [s] => (case String.tokens (equal #".") s
                 of [file,"json"] => s
                  | otherwise => fail())
      | otherwise => fail()
 end

     
end (* Json *)
