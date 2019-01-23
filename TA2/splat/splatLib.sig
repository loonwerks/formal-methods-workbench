signature splatLib =
sig
 include Abbrev

  type regexp = Regexp_Type.regexp

  type ('a, 'b) fmap = ('a, 'b) Redblackmap.dict

  datatype width
    = BITWIDTH of int
    | BYTEWIDTH of int
  
  datatype endian = MSB | LSB
  
  datatype sign
     = SIGNED of endian 
     | UNSIGNED of endian
  
  datatype textrep = BIT | OCT | HEX | DEC;
  
  datatype format
    = BINARY of sign * width
    | TEXT of textrep * width
    | ENUM of hol_type

  datatype fieldval
    = Num of Arbint.int * format 
    | Interval of Arbint.int * Arbint.int * format
    | Bool of bool * format
    | Char of char * format
    | Enumset of string * term list * format
    | Raw of string * width
    | Padding of width
    | Packed of fieldval list;
  
  val format_of : fieldval -> format

  type enumMap = (string, (term * int) list) fmap
					       
  val fieldval_to_tree : enumMap -> fieldval -> Regexp_Type.tree
  
  type coding 
      = {enc : term, 
         dec : term,
         enc_def : thm, 
         dec_def : thm,
         dec_enc : thm}
  

  type codingMap = (format, coding) fmap

  val the_enumMap : unit -> enumMap
  val the_codingMap : unit -> codingMap

  val define_enum_encoding : hol_type -> unit

  type precord = {fields : (string * fieldval) list, pred : term}
  
  type decls = 
  (* pkgName *)  string * 
  (* enums *)    (string * string list) list *
  (* recds *)    (string * (string * AST.ty) list) list *
  (* fns *)      thm list

  val mk_correctness : decls -> thm -> term * thm * thm * term

  val charset_conv_ss : simpLib.ssfrag

  val IN_CHARSET_NUM_TAC : tactic

  val prove_constraints 
    : enumMap * codingMap 
        -> precord
        -> {coders : coding,
	    regexp : regexp,
	    correctness : thm}
end
