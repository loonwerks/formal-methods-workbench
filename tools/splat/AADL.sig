signature AADL =
sig
 include Abbrev

  type qid = string * string
  type ty = AST.ty
  type exp = AST.exp
  type tyEnv = (ty * ty) list
		 
 datatype tydec
    = EnumDec of qid * string list
    | RecdDec of qid * (string * ty) list
    | ArrayDec of qid * ty

 datatype tmdec
    = ConstDec of qid * ty * exp
    | FnDec of qid * (string * ty) list * ty * exp

 datatype filter
    = FilterDec of qid * (string * ty * string * string) list * (string * exp)

 type decls = 
  (* pkgName *)  string * 
  (* types *)    (tydec list * 
  (* consts *)    tmdec list *
  (* filters *)   filter list)
  ;
								 
  val scrape : Json.json -> decls
  val scrape_pkgs : Json.json -> decls list

  val mk_aadl_defs 
    : string -> tyEnv -> decls -> tyEnv * thm list * thm list * (string * thm) list

  val pkgs2hol 
    : string -> decls list -> tyEnv * thm list * thm list * (string * thm) list

end
