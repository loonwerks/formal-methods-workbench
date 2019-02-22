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
    | Char of char * format
    | Enumset of string * term list * format
    | Raw of string * width
    | Padding of width
    | Packed of fieldval list;
  
  val format_of : fieldval -> format

  type enumMap = (string, (term * int) list) fmap
  val the_enumMap : unit -> enumMap
  val insert_enum : string * (term * int) list -> unit
  val define_enum_encoding : hol_type -> unit
					       

  type coding 
      = {enc : term, 
         dec : term,
         enc_def : thm, 
         dec_def : thm,
         dec_enc : thm}
  

  type codingMap = (format, coding) fmap
  val the_codingMap : unit -> codingMap
  val insert_coding : format * coding -> unit

  val fieldval_to_tree : enumMap -> fieldval -> Regexp_Type.tree

  type precord = {fields : (string * fieldval) list, pred : term}
  
  type filter_info 
       = {name : string,
          spec : thm,
	  regexp : Regexp_Type.regexp,
          encode_def : thm, 
          decode_def : thm,
          inversion : term,
          correctness : term,
          implicit_constraints : thm option}

  val filter_correctness : thm -> filter_info

  val IN_CHARSET_NUM_TAC : tactic

  val prove_constraints 
    : enumMap * codingMap 
        -> precord
        -> {coders : coding,
	    regexp : regexp,
	    correctness : thm}
end
