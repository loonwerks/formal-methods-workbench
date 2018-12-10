signature AST =
sig

  type id = string
  type qid = string * string

  datatype bop 
    = And
    | ArithmeticRShift
    | BitAnd
    | BitOr
    | BitXOR
    | Divide
    | Equal
    | Exponent
    | Imp
    | Greater
    | GreaterEqual
    | Less
    | LessEqual
    | LogicalLShift
    | LogicalRShift
    | Plus
    | Minus
    | Modulo
    | Multiply
    | NotEqual
    | Or
    | CastWidth
    | RegexMatch

  datatype uop 
    = Not | BitNot | UMinus | ChrOp | OrdOp | Signed | Unsigned | Unbounded

  datatype numkind 
    = Nat of int option
    | Int of int option

  val defaultNumKind : numkind ref

  datatype lit 
    = IdConst of qid
    | BoolLit of bool
    | CharLit of char
    | StringLit of string
    | IntLit of {value: int, kind:numkind}
    | FloatLit of real
    | RegexLit of string

  datatype builtin 
    = BoolTy
    | CharTy
    | StringTy
    | IntTy of numkind 
    | FloatTy
    | RegexTy

  datatype quant = Exists | Forall

  datatype ty 
    = BaseTy of builtin
    | NamedTy of qid
    | RecdTy of qid * (id * ty) list
    | ArrayTy of ty * exp list
  and exp
    = VarExp of id
    | ConstExp of lit
    | Unop of uop * exp
    | Binop of bop * exp * exp
    | ArrayExp of exp list
    | ArrayIndex of exp * exp list
    | ConstrExp of qid * id * exp option
    | Fncall of qid * exp list
    | RecdExp of qid * (id * exp) list
    | RecdProj of exp * id
    | Quantified of quant * (id * ty) list * exp
  
  type vdec = id * ty

  datatype stmt 
    = Skip
    | Check of exp
    | Assign of exp * exp
    | Call of qid * exp list
    | IfThenElse of exp * stmt * stmt
    | Case of exp * (exp * stmt) list
    | Block of stmt list
    | For of vdec * exp * exp * stmt * stmt
    | While of exp * stmt

  datatype param = In of vdec | InOut of vdec | Out of vdec

  datatype decl 
    = NumTyDecl of numkind
    | TyAbbrevDecl of id * ty
    | RecdDecl of id * (id * ty) list
    | DatatypeDecl of id * (id * ty list) list
    | GraphDecl of id * ty * ty
    | VarDecl of vdec
    | ConstDecl of id * ty * exp
    | SizedDataDecl of id * ty * exp * exp option
    | SizedGraphDecl of id * ty * exp * exp
    | EfnDecl of id * param list * vdec option
    | FnDecl of id * param list * vdec option * vdec list * stmt list
    | SpecDecl of id * vdec list * stmt list
    | CommentDecl of string list

  type package = id * decl list

  val intTy : ty
  val uintTy : ty
  val mk_uintLit   : int -> exp
  val dest_uintLit : exp -> int option

  val defaultNumTy : unit -> ty

  val tyFrees : id list -> ty -> id list
  val expFrees : id list -> exp -> id list
  val stmtFrees : id list -> stmt -> id list
  val fnArrayDims : param list * vdec option -> id list
  val fndeclFrees : id * param list * vdec option * vdec list * stmt list -> id list

  val tyVars : ty -> id list
  val expVars : exp -> id list
  val stmtVars : stmt -> id list

  val is_VarExp : exp -> bool
  val dest_VarExp : exp -> id

  val id_of_vdec : vdec -> id
  val ty_of_vdec : vdec -> ty
  val dest_param : param -> vdec
  val dest_retval : vdec option -> vdec list
  val isIn       : param -> bool
  val isOut      : param -> bool
  val isInOnly   : param -> bool
  val isOutOnly  : param -> bool

  val eqTy  : ty * ty -> bool
  val eqExp : exp * exp -> bool

  type ('a,'b) subst = ('a,'b) Lib.subst
  val substTyTy   : (ty,ty)subst -> ty -> ty
  val substTyExp  : (ty,ty)subst -> exp -> exp
  val substTyParam: (ty,ty)subst -> param -> param
  val substTyStmt : (ty,ty)subst -> stmt -> stmt
  val substTyDecl : (ty,ty)subst -> decl -> decl

  val substTy   : (exp,exp)subst -> ty -> ty
  val substExp  : (exp,exp)subst -> exp -> exp
  val substStmt : (exp,exp)subst -> stmt -> stmt
  val substDecl : (exp,exp)subst -> decl -> decl

  val subst_LR_Stmt : (exp,exp)subst -> (exp,exp)subst -> stmt -> stmt

  val substQidTy   : (qid,qid)subst -> ty -> ty
  val substQidExp  : (qid,qid)subst -> exp -> exp
  val substQidStmt : (qid,qid)subst -> stmt -> stmt
  val substQidDecl : (qid,qid)subst -> decl -> decl
  val substQidPkg  : (qid,qid)subst -> package -> package

  type ids = id list
  val tyIds   : ty -> ids -> ids
  val expIds  : exp -> ids -> ids
  val stmtIds : stmt -> ids -> ids
  val declIds : decl -> ids -> ids
  val pkgIds  : package -> ids -> ids

  type qids = qid list
  val tyQids   : ty -> qids -> qids
  val expQids  : exp -> qids -> qids
  val stmtQids : stmt -> qids -> qids
  val declQids : decl -> qids -> qids
  val pkgQids  : package -> qids -> qids

  val renamePkg : string -> package -> package

  val namedTypes : ty list -> qid list

(*  val lift_ty_operator : (ty -> ty) -> package -> package *)
  val lift_exp_operator : (exp -> exp) -> package -> package
  val lift_stmt_operator : (stmt -> stmt) -> package -> package

  val exp_calls  : exp list -> qid list -> qid list
  val proc_calls : stmt list -> qid list -> qid list
  val fn_calls   : stmt list -> qid list -> qid list
  val all_calls  : stmt list -> qid list -> qid list

  val is_int       : ty -> bool
  val is_unbounded_uint : ty -> bool
  val is_uint_literal : exp -> bool
  val is_int_literal  : exp -> bool
  val is_signed    : ty -> bool
  val is_unsigned  : ty -> bool
  val is_bounded   : ty -> bool
  val flip_sign    : uop -> ty -> ty
  val drop_bound   : ty -> ty

  val pkg_varDecs  : package -> (string * vdec list) list

  val pkgNumTypes  : package -> numkind list

  val splitPkg     : package -> decl list * 
                                decl list * 
                                (id * param list * vdec option) list *
                                decl list * 
                                (id * vdec list * stmt list) list

  type fnsig = qid * (param list * vdec option)
  type tysig = qid * ty

  val fnsigs_of : package -> fnsig list
  val tysigs_of : package -> tysig list
  (* val ty_of : vdec list * (fnsig list * tysig list) -> exp -> ty *)

  type tyenv 
     = (qid -> ty)                    (* abbrEnv *)
     * (id -> ty)                     (* varEnv *)
     * (qid -> ty list * ty)          (* constEnv *)
     * ((qid * id) -> (ty list * ty)) (* constrEnv *)
     * (qid -> (id * ty) list)        (* recdEnv *)
     * qid list                       (* specEnv *) 

  val join_tyenv : tyenv -> tyenv -> tyenv
  val tyEnvs 
     : package -> (qid * ty) list
                  * (id * ty) list
                  * (qid * (ty list * ty)) list
                  * ((qid * id) * (ty list * ty)) list
                  * (qid * (id * ty) list) list
                  * qid list 

  val tcTy : tyenv -> ty -> unit
  val tcExp : tyenv -> exp -> ty
  val tcStmt : tyenv -> stmt -> unit
  val tcDecl : tyenv -> decl -> unit
  val typecheck : package -> package

  val tydecl_cliques : string -> decl list -> decl list list

  val qid_string : qid -> string
  val base_ty_name : ty -> string
  val pp_ty : int -> ty -> PolyML.pretty
  val pp_exp : int -> exp -> PolyML.pretty
  val pp_stmt : int -> stmt -> PolyML.pretty
  val pp_decl : int -> decl -> PolyML.pretty

end
