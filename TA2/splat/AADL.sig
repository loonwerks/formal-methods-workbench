signature AADL =
sig

  type ty = AST.ty
  type exp = AST.exp
		 
 datatype tydec
    = EnumDec of string * string list
    | RecdDec of string * (string * ty) list
    | ArrayDec of string * ty * exp;

 datatype tmdec
    = ConstDec of string * ty * exp
    | FnDec of string * (string * ty) list * exp;

 datatype filter
    = FilterDec of string * (string * ty * string * string) list * (string * exp)

 type decls = 
  (* pkgName *)  string * 
  (* types *)    tydec list * 
  (* consts *)   tmdec list *
  (* filters *)  filter list
  ;
								 
  val scrape : Json.json -> decls
  val scrape_pkgs : Json.json -> decls list

  val mk_aadl_defs : string -> decls -> thm list * thm list * thm list

end
