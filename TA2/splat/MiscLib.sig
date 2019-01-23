signature MiscLib =
sig
   include Abbrev
   type substring = Substring.substring

   val unimplemented : string -> exn
   val optlist       : 'a option -> 'a list
   val listopt       : 'a list -> 'a option
   val bigU          : ''a list list -> ''a list
   val no_dups       : ''a list -> bool
   val dropPrefix    : 'a list -> 'b list -> 'b list
   val zip3          : 'a list -> 'b list -> 'c list -> ('a * 'b *'c) list
   val trancl        : (''a * ''a) list -> (''a * ''a list) list
   val TC            : (''a * ''a list) list -> (''a * ''a list) list
   val cliques_of    : (''a * ''a list) list -> (''a * ''a list) list list
   val front_last2 : 'a list -> 'a list * ('a * 'a)

   val silent : bool -> unit
   val apply_with_chatter : ('a -> 'b) -> 'a -> string -> string -> 'b
   val list : 'a -> 'a list

   val simpfrag_to_ssfrag : simpfrag.simpfrag -> simpLib.ssfrag
   val basic_ss      : simpLib.simpset
   val kstd_ss       : simpLib.simpset

   val dom : hol_type -> hol_type
   val rng : hol_type -> hol_type
   val empty_tyset   : hol_type HOLset.set
   val is_atom       : term -> bool
   val consts_of     : term -> term HOLset.set
   val subterm       : term -> term -> bool
   val mk_icomb      : term * term -> term
   val list_mk_icomb : term * term list -> term

   val qid_to_tm : string * string -> term
   val tm_to_qid : term -> string * string

   val fupd_to_proj : term -> term
   val proj_to_fupd : term -> term
   val FAPPLY_FUPDATE_CONV : conv -> conv
   val TRIV_LET_CONV : conv
   val mk_gproj : term -> term
   val mk_fnproj : term -> string -> term

   val gen_variant : (string -> string) -> string list -> string -> string
   val prefix_string_variant : string -> string list -> string -> string
   val suffix_string_variant : string -> string list -> string -> string
   val numeric_string_variant : string -> string list -> string -> string

   val variants : term list -> term list -> term list

   val mk_gensym : string -> string list 
                    -> {gensym: unit -> string, 
                        set_base: string list -> string list}
   val stdOut_print : string -> unit
   val stdErr_print : string -> unit
   val inputFile    : string -> string
   val stringToFile : string -> string -> unit
   val prettyStream : TextIO.outstream -> string * string -> PolyML.pretty -> unit
   val prettyFile   : string -> PolyML.pretty -> unit

   val succeed : unit -> 'a
   val fail : unit -> 'a

   val exp : int -> int -> int
   val bits_needed : int -> int

   val inv_image : ('a -> 'a -> 'b) -> ('c -> 'a) -> 'c -> 'c -> 'b
   val sort_on_int_key : (int * 'a) list -> (int * 'a) list
   val sort_on_string_key : (string * 'a) list -> (string * 'a) list
   val sort_on_qid_key : ((string * string) * 'a) list -> ((string * string) * 'a) list

   val mapfilter : ('a -> 'b) -> 'a list -> 'b list
end
