signature Json = 
sig
  type substring = Substring.substring

 datatype number
    = Int of int
    | Float of real (* Not supported by this parser *)

 datatype json
    = Null
    | LBRACK  (* stack symbol only *)
    | LBRACE  (* stack symbol only *)
    | Boolean of bool
    | Number of number
    | String of string
    | List of json list
    | AList of (string * json) list;
 
  val fromSubstring  : substring -> json list * substring
  val fromString     : string -> json list * substring
  val fromFile       : string -> json list * substring
  val jsonFileName   : string -> string

(*
  val pp_json : 'a -> 'b -> json -> PolyML.pretty
  val print_json : json -> unit
  val install_pp_json : unit -> unit
*)
end
