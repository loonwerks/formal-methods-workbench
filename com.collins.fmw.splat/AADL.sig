signature AADL =
sig

  type ty = AST.ty
  type exp = AST.exp
		 
  type decls = 
  (* pkgName *)  string * 
  (* enums *)    (string * string list) list *
  (* recds *)    (string * (string * ty) list) list *
  (* fns *)      (string * (string * ty) list * exp) list *
  (* filters *)  (string * (string * string * string) list * (string * exp)) list
								 
  val get_pkg_decls : Json.json -> decls
  val gen_hol: decls -> unit
				      
end
