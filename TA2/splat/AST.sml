(*===========================================================================*)
(* Generate ASTs from s-expressions.                                         *)
(*===========================================================================*)

structure AST :> AST = 
struct

open Lib Feedback MiscLib PPfns;

val ERR = mk_HOL_ERR "AST";
fun unimplemented s = ERR s "unimplemented";

(*---------------------------------------------------------------------------*)
(* Types                                                                     *)
(*---------------------------------------------------------------------------*)

type id = string
type qid = string * string;

datatype uop = Not    | BitNot   | UMinus | ChrOp | OrdOp 
             | Signed | Unsigned | Unbounded;

datatype bop 
  = Plus | Minus | Multiply | Divide | Modulo | Exponent
  | Equal | NotEqual | Greater | Less | LessEqual | GreaterEqual 
  | Or | And | Imp | BitOr | BitAnd | BitXOR
  | LogicalLShift | LogicalRShift | ArithmeticRShift
  | CastWidth
  | RegexMatch

datatype numkind 
  = Nat of int option
  | Int of int option

datatype lit 
  = IdConst of qid
  | BoolLit of bool
  | CharLit of char
  | StringLit of string
  | FloatLit of real
  | IntLit of {value:int,kind:numkind}
  | RegexLit of string

datatype builtin
  = BoolTy
  | CharTy
  | StringTy
  | FloatTy
  | IntTy of numkind
  | RegexTy

datatype quant = Forall | Exists
  
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
  | RecdExp of qid * (id * exp) list
  | ArrayIndex of exp * exp list
  | RecdProj of exp * id
  | Fncall of qid * exp list
  | ConstrExp of qid * id * exp option
  | Quantified of quant * (id * ty) list * exp

type vdec = id * ty;

datatype stmt
  = Skip
  | Assign of exp * exp
  | IfThenElse of exp * stmt * stmt
  | Case of exp * (exp * stmt) list
  | Call of qid * exp list
  | While of exp * stmt
  | For of vdec * exp * exp * stmt * stmt
  | Block of stmt list
  | Check of exp

datatype param 
  = In of vdec
  | Out of vdec
  | InOut of vdec

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
;

(*---------------------------------------------------------------------------*)
(* Support functions                                                         *)
(*---------------------------------------------------------------------------*)

fun pp_qid ("",s) = s
  | pp_qid (s1,s2) = String.concat[s1,".",s2];

fun qid_string p = Lib.quote(pp_qid p);

fun mk_param "in" = In
  | mk_param "out" = Out
  | mk_param "inout" = InOut
  | mk_param x = raise ERR "mk_param" ("unexpected param name: "^x);

fun id_of_vdec (id,ty) = id
fun ty_of_vdec (id,ty) = ty

fun dest_param (In vdec) = vdec
  | dest_param (Out vdec) = vdec
  | dest_param (InOut vdec) = vdec;

fun dest_retval NONE = []
  | dest_retval (SOME x) = [x];

fun isInOnly (In _) = true | isInOnly otherwise = false
fun isOutOnly (Out _) = true | isOutOnly otherwise = false
fun isIn (Out _) = false | isIn otherwise = true
fun isOut (In _) = false | isOut otherwise = true;

val uintTy = BaseTy (IntTy (Nat NONE));
val intTy = BaseTy (IntTy (Int NONE));

val defaultNumKind = ref (Nat NONE);
fun defaultNumTy() = BaseTy(IntTy (!defaultNumKind));

fun mk_uintLit i = 
  if i < 0 then raise ERR "mk_uintLit" "negative"
  else ConstExp(IntLit{value=i, kind=Nat NONE});

fun dest_uintLit (ConstExp(IntLit{value=i, kind=Nat NONE})) = SOME i
  | dest_uintLit otherwise = NONE;

(*---------------------------------------------------------------------------*)
(* Lift a stmt -> stmt operation to a pkg->pkg operation                     *)
(*---------------------------------------------------------------------------*)

fun lift_stmt_operator (f:stmt->stmt) (name,decls) = 
 let fun lift decl =
       case decl 
        of FnDecl (id,args,retvalOpt,locals,stmts) =>
           FnDecl(id,args,retvalOpt,locals,List.map f stmts)
         | SpecDecl (id,locals,stmts) => SpecDecl(id,locals,List.map f stmts)
         | otherwise => decl
  in
    (name, map lift decls)
  end;

(*---------------------------------------------------------------------------*)
(* Lift an exp -> exp operation to a pkg->pkg operation                      *)
(*---------------------------------------------------------------------------*)

fun lift_exp_operator (f:exp->exp) = 
 let fun lift stmt =
       case stmt 
        of Skip => Skip
      | Assign(e1,e2) => Assign(f e1, f e2)
      | IfThenElse(exp,s1,s2) => IfThenElse(f exp, lift s1, lift s2)
      | Case(e,clauses) => 
         Case(f e, map (fn (exp,stmt) => (f exp, lift stmt)) clauses)
      | Call(qid,elist) => Call(qid,map f elist)
      | While(exp,stmt) => While(f exp, lift stmt)
      | For (id,e1,e2,istmt,body) => For(id, f e1, f e2, lift istmt, lift body)
      | Block stmts => Block (map lift stmts)
      | Check exp => Check (f exp)
    fun appDecl d = 
      case d 
       of ConstDecl(id,ty,e) => ConstDecl(id,ty,f e)
        | SizedDataDecl(id,ty,e1,e2) => SizedDataDecl(id,ty,f e1, Option.map f e2)
        | SizedGraphDecl(id,ty,e1,e2) => SizedGraphDecl(id,ty,f e1,f e2)
        | FnDecl(id,params,rvOpt,locals,stmts) =>
           FnDecl(id,params,rvOpt,locals,map lift stmts)
        | SpecDecl(id,vdecs,stmts) => SpecDecl(id,vdecs,map lift stmts)
        | otherwise => d
  in 
     fn (pkgName,decls) => (pkgName,map appDecl decls)
  end;

(*---------------------------------------------------------------------------*)
(* Lift a ty -> ty operation to a pkg->pkg operation                         *)
(*---------------------------------------------------------------------------*)
(*
fun lift_ty_operator (f:ty->ty) = 
 let fun liftE exp =
       case exp 
        of VarExp _ => exp
         | ConstExp _ => exp
         | Unop (uop,e) => UnOp(uop, liftE e)
         | Binop(bop,e1,e2) => Binop(bop,liftE e1,liftE e2)
         | ArrayExp elist => ArrayExp (map liftE elist)
         | ArrayIndex (e,elist) => ArrayIndex (liftE e,map liftE elist)
         | ConstrExp(qid,id,eOpt) => ConstrExp(qid,id,Option.map liftE eOpt)
         | Fncall(qid,elist) => Fncall(qid,map liftE elist)
         | RecdExp (qid,flds) => recdExp(qid,map (I##liftE) fields)
         | RecdProj(e,id) => RecdProj(liftE e,id)
         | Quantified(q,bvars,exp) => Quantified(q,map (I##f) bvars,liftE exp)
    fun liftS stmt = 
       case stmt 
        of Skip => Skip
      | Assign(e1,e2) => Assign(liftE e1, liftE e2)
      | IfThenElse(exp,s1,s2) => IfThenElse(liftE exp, liftS s1, liftS s2)
      | Case(e,clauses) => Case(liftE e, map (liftE##liftS) clauses)
      | Call(qid,elist) => Call(qid,map liftE elist)
      | While(exp,stmt) => While(liftE exp, liftS stmt)
      | For (id,e1,e2,istmt,body) => 
            For(id, liftE e1, liftE e2, liftS istmt, liftS body)
      | Block stmts => Block (map liftS stmts)
      | Check exp => Check (liftE exp)

 in
   lift_exp_operator lift
 end;
*)

(*---------------------------------------------------------------------------*)
(* Various operations on types, expressions, programs, and packages          *)
(*---------------------------------------------------------------------------*)

fun namedTypes [] = []
  | namedTypes (BaseTy _ :: t) = namedTypes t
  | namedTypes (NamedTy qid::t) = insert qid (namedTypes t)
  | namedTypes (RecdTy(qid,fields)::t) = insert qid (namedTypes (map snd fields@t))
  | namedTypes (ArrayTy(base,dims)::t) = namedTypes (base::t);

fun dest_VarExp (VarExp id) = id
  | dest_VarExp otherwise = raise ERR "dest_VarExp" "expected a variable"

val is_VarExp = Lib.can dest_VarExp;

val formalName = fst o dest_param;

(* -------------------------------------------------------------------------- *)
(* Find all numeric types in a ty/exp/stmt/decl/pkg                           *)
(* -------------------------------------------------------------------------- *)

fun tyNumTypes [] = []
  | tyNumTypes (BaseTy(IntTy numkind)::t) = insert numkind (tyNumTypes t)
  | tyNumTypes (BaseTy other::t) = tyNumTypes t
  | tyNumTypes (NamedTy qid::t) = tyNumTypes t
  | tyNumTypes (RecdTy(qid,fields)::t) = tyNumTypes (map snd fields @ t)
  | tyNumTypes (ArrayTy(base,dims)::t) = union (tyNumTypes (base::t))
                                               (expNumTypes dims)
and
    expNumTypes [] = []
  | expNumTypes (Quantified(quant,bvars,e)::t) = 
        union (tyNumTypes (map snd bvars)) (expNumTypes (e::t))
  | expNumTypes (VarExp id::t) = expNumTypes t
  | expNumTypes (ConstExp _::t) = expNumTypes t
  | expNumTypes (Unop (_,e)::t) = expNumTypes (e::t)
  | expNumTypes (Binop(_,e1,e2)::t) = expNumTypes (e1::e2::t)
  | expNumTypes (ArrayExp expl::t) = expNumTypes (expl@t)
  | expNumTypes (RecdExp (qid,fields)::t) = expNumTypes (map snd fields @ t)
  | expNumTypes (ArrayIndex(e,elist)::t) = expNumTypes (e::elist@t)
  | expNumTypes (RecdProj(e,id)::t) = expNumTypes (e::t)
  | expNumTypes (Fncall(qid,expl)::t) = expNumTypes (expl@t)
  | expNumTypes (ConstrExp(tyqid,id,opt)::t) = expNumTypes (optlist opt @ t)

fun stmtNumTypes [] = []
  | stmtNumTypes (stmt::t) = 
    case stmt 
     of Skip => stmtNumTypes t
      | Assign(e1,e2) => union (expNumTypes [e2]) (stmtNumTypes t)
      | Call(qid,elist) => union (expNumTypes elist) (stmtNumTypes t)
      | IfThenElse(e,s1,s2) => union (expNumTypes [e]) (stmtNumTypes (s1::s2::t))
      | Case(e,clauses) =>
          let val stmts = map snd clauses
          in union (expNumTypes [e]) (stmtNumTypes (stmts@t))
          end
      | While(e,s) => union (expNumTypes [e]) (stmtNumTypes (s::t))
      | For((id,ty),e1,e2,istmt,body) =>
           union (tyNumTypes [ty]) (stmtNumTypes (istmt::body::t))
      | Block stmts => stmtNumTypes (stmts@t)
      | Check e => union (expNumTypes [e]) (stmtNumTypes t)

fun declNumTypes [] = []
  | declNumTypes(decl::t) = 
    let val ntys = declNumTypes t
        val dntys = 
          case decl of 
          NumTyDecl nkind => [nkind]
        | TyAbbrevDecl (id,ty) => tyNumTypes [ty]
        | RecdDecl (id,fields) => tyNumTypes (map snd fields)
        | DatatypeDecl(id,constrs) => U (map (tyNumTypes o snd) constrs)
        | GraphDecl(id,nty,ety) => tyNumTypes [nty,ety]
        | VarDecl (id,ty) => tyNumTypes [ty]
        | ConstDecl(id,ty,exp) => union (tyNumTypes [ty]) (expNumTypes [exp])
        | SizedDataDecl(id,ty,e1,e2) => union (tyNumTypes [ty]) (expNumTypes (e1::optlist e2))
        | SizedGraphDecl(id,ty,e1,e2) => union (tyNumTypes [ty]) (expNumTypes [e1,e2])
        | EfnDecl(id,params,retvalOpt) =>
              tyNumTypes (map (snd o dest_param) params @ 
                          map snd (optlist retvalOpt))
        | FnDecl(id,params,retvalOpt,locals,stmts) =>
          let val vartypes =  
                map (snd o dest_param) params @ 
                map snd (optlist retvalOpt) @ 
                map snd locals 
             val var_ntys = tyNumTypes vartypes
             val stmt_ntys = stmtNumTypes stmts
          in union var_ntys stmt_ntys
          end
        | SpecDecl(id,vars,stmts) => 
          union (tyNumTypes (map snd vars)) (stmtNumTypes stmts)
        | CommentDecl _ => []
    in
      union dntys ntys            
    end

fun pkgNumTypes (pkgName,decls) = declNumTypes decls;

(* -------------------------------------------------------------------------- *)
(* Find all vars in a ty/exp/stmt                                             *)
(* -------------------------------------------------------------------------- *)

fun expVars (VarExp id) = [id]
  | expVars (ConstExp _) = []
  | expVars (Unop (_,e)) = expVars e
  | expVars (Binop(_,e1,e2)) = union (expVars e1) (expVars e2)
  | expVars (ArrayExp expl) = bigU (map expVars expl)
  | expVars (RecdExp (qid,fields)) = bigU (map (expVars o snd) fields)
  | expVars (ArrayIndex(e,elist)) = bigU (map expVars (e::elist))
  | expVars (RecdProj(e,id)) = expVars e
  | expVars (Fncall(qid,expl)) = bigU (map expVars expl)
  | expVars (ConstrExp(tyqid,id,opt)) = bigU(map expVars (optlist opt))
  | expVars (Quantified(quant,bvars,e)) = 
      bigU [map fst bvars, bigU (map (tyVars o snd) bvars), expVars e]
and tyVars (BaseTy _) = []
  | tyVars (NamedTy _) = []
  | tyVars (RecdTy(qid,fields)) = bigU(map (tyVars o snd) fields)
  | tyVars (ArrayTy(ty,dims)) = bigU(tyVars ty::map expVars dims);

fun stmtVars Skip = []
  | stmtVars (Assign(e1,e2)) = union (expVars e1) (expVars e2)
  | stmtVars (IfThenElse(e,s1,s2)) = union (expVars e) 
                                       (union (stmtVars s1) (stmtVars s2))
  | stmtVars (Case(e,clauses)) = 
     let fun clauseVars (pat,stmt) = union (expVars pat) (stmtVars stmt)
     in union (expVars e) (bigU (map clauseVars clauses))
     end
  | stmtVars (Call(qid,elist)) = bigU(map expVars elist)
  | stmtVars (While(e,s)) = union (expVars e) (stmtVars s)
  | stmtVars (For((id,ty),e1,e2,istmt,body)) = 
      bigU [[id], expVars e1, expVars e2, stmtVars istmt,stmtVars body]
  | stmtVars (Block stmts) = bigU (map stmtVars stmts)
  | stmtVars (Check e) = expVars e;

val stringSort = Listsort.sort String.compare;

val sorted_expVars = stringSort o expVars;

(*
fun declVars (NumTyDecl _) = []
  | declVars (TyAbbrevDecl(_,ty)) = tyVars ty
  | declVars (RecdDecl(_,fields)) = bigU (map (tyVars o snd) fields)
  | declVars (DatatypeDecl(id, constrList)) = 
  | declVars (VarDecl(id,ty)) = insert id (tyVars ty)
  | declVars (ConstDecl(id,ty,exp)) = insert id (union (tyVars ty) (expVars exp))
  | declVars (SizedDataDecl(id,ty,e1,NONE)) 
      = insert id (union (tyVars ty) (expVars e1))
  | declVars (SizedDataDecl(id,ty,e1,SOME e2)) 
      = insert id (union (tyVars ty) (union (expVars e1) (expVars e2)))
  | declVars (SizedGraphDecl(id,ty,e1,e2)) 
      = insert id (union (tyVars ty) (union (expVars e1) (expVars e2)))
  | declVars (SizedGraphDecl(id,ty,e1,e2)) = insert id (union (tyVars ty) 
                                                  (union (expVars e1) (expVars e2)))
  | declVars (EfnDecl(id,params,retvalOpt)) = 
      let val paramIds = map (fst o dest_param) 
                             (params @ map Out (optlist retvalOpt))
          val dimIds = fnArrayDims (params,retvalOpt)
          val localIds = map fst locals
      in stmtFrees (dimIds@paramIds@localIds) (Block stmts)
      end
  | declVars (FnDecl(id,params,retvalOpt,locals,stmts)) = 
      let val paramIds = map (fst o dest_param) 
                             (params @ map Out (optlist retvalOpt))
          val dimIds = fnArrayDims (params,retvalOpt)
          val localIds = map fst locals
      in stmtFrees (dimIds@paramIds@localIds) (Block stmts)
      end
  | declVars(SpecDecl(id,vlist,stmts)) = 
            union (bigU (map (tyVars o snd) vlist))
                  (stmtVars stmts)
  | declVars (CommentDecl _) = []
*)

(*---------------------------------------------------------------------------*)
(* Find all/only free variables in an ty/exp/stmt/function/package           *)
(*---------------------------------------------------------------------------*)

fun tyFrees scope ty = 
 case ty 
  of BaseTy _ => []
   | NamedTy _ => []
   | RecdTy(_,fields) => bigU(map (tyFrees scope o snd) fields)
   | ArrayTy(ty,dims) => bigU(tyFrees scope ty::map (expFrees scope) dims)
and expFrees scope exp = 
 case exp
  of VarExp id => if Lib.mem id scope then [] else [id]
   | ConstExp _ => []
   | Unop (_,e) => expFrees scope e
   | Binop(_,e1,e2) => union (expFrees scope e1) (expFrees scope e2)
   | ArrayExp expl => bigU (map (expFrees scope) expl)
   | RecdExp (qid,fields) => bigU (map (expFrees scope o snd) fields)
   | ArrayIndex(e,elist) => bigU (map (expFrees scope) (e::elist))
   | RecdProj(e,id) => expFrees scope e
   | Fncall(qid,expl) => bigU (map (expFrees scope) expl)
   | ConstrExp(tyqid,id,opt) => bigU(map (expFrees scope) (optlist opt))
   | Quantified(quant,bvars,e) => expFrees (map fst bvars @ scope) e

fun stmtFrees scope stmt = 
 case stmt 
  of Skip => []
   | Check e => expFrees scope e
   | Assign(e1,e2) => union (expFrees scope e1) (expFrees scope e2)
   | IfThenElse(e,s1,s2) => bigU [expFrees scope e, 
                                  stmtFrees scope s1, stmtFrees scope s2]
   | Call(qid,elist) => bigU(map (expFrees scope) elist)
   | While(e,s) => union (expFrees scope e) (stmtFrees scope s)
   | Block stmts => bigU (map (stmtFrees scope) stmts)
   | Case(e,rules) => 
      let fun ruleFrees (pat,stmt) = stmtFrees (expFrees [] pat @ scope) stmt
      in union (expFrees scope e) (bigU (map ruleFrees rules))
      end
   | For((id,ty),e1,e2,istmt,body) => 
       bigU[expFrees (id::scope) e1, expFrees (id::scope) e2, 
            stmtFrees (id::scope) istmt, stmtFrees (id::scope) body];

(* -------------------------------------------------------------------------- *)
(* Array dimensions mentioned in the formal parameters of a function.         *)
(* -------------------------------------------------------------------------- *)

fun fnArrayDims (formals,retvalOpt) = 
 let val all_param_tys = map (snd o dest_param) 
                             (formals @ map Out (optlist retvalOpt))
     val dimVarIds = U (map tyVars all_param_tys)
 in dimVarIds
 end

(* -------------------------------------------------------------------------- *)
(* Free vars of a FnDecl                                                      *)
(* -------------------------------------------------------------------------- *)

fun fndeclFrees (id,params,retvalOpt,locals,stmts) = 
 let val paramIds = map (fst o dest_param) 
                     (params @ map Out (optlist retvalOpt))
     val dimIds = fnArrayDims (params,retvalOpt)
     val localIds = map fst locals
  in stmtFrees (dimIds@paramIds@localIds) (Block stmts)
  end;

(*---------------------------------------------------------------------------*)
(* What functions are called in a list of expressions.                       *)
(*---------------------------------------------------------------------------*)

fun exp_calls [] acc = acc
  | exp_calls (VarExp _::t) acc = exp_calls t acc
  | exp_calls (ConstExp _::t) acc = exp_calls t acc
  | exp_calls (Unop(_,e)::t) acc = exp_calls (e::t) acc
  | exp_calls (Binop(_,e1,e2)::t) acc = exp_calls (e1::e2::t) acc
  | exp_calls (ArrayExp elist::t) acc = exp_calls (elist@t) acc
  | exp_calls (ArrayIndex(_,elist)::t) acc = exp_calls (elist@t) acc
  | exp_calls (ConstrExp (_,_,NONE)::t) acc = exp_calls t acc
  | exp_calls (ConstrExp (_,_,SOME e)::t) acc = exp_calls (e::t) acc
  | exp_calls (Fncall(qid,elist)::t) acc = exp_calls (elist@t) (insert qid acc)
  | exp_calls (RecdExp (qid,fields)::t) acc = exp_calls (map snd fields@t) acc
  | exp_calls (RecdProj(e,_)::t) acc = exp_calls (e::t) acc
  | exp_calls (Quantified(_,_,e)::t) acc = exp_calls (e::t) acc;

(*---------------------------------------------------------------------------*)
(* What procedures are called in a list of statements.                       *)
(*---------------------------------------------------------------------------*)

fun proc_calls [] acc = acc
  | proc_calls (Call(qid,_)::t) acc = proc_calls t (insert qid acc)
  | proc_calls (IfThenElse(e,s1,s2)::t) acc = proc_calls (s1::s2::t) acc
  | proc_calls (Case(e,clauses)::t) acc = proc_calls (map snd clauses@t) acc
  | proc_calls (While(e,s)::t) acc = proc_calls (s::t) acc
  | proc_calls (For(_,e1,e2,istmt,body)::t) acc = proc_calls (istmt::body::t) acc
  | proc_calls (Block slist::t) acc = proc_calls (slist@t) acc
  | proc_calls (_::t) acc = proc_calls t acc

(*---------------------------------------------------------------------------*)
(* What functions are called in a list of statements.                        *)
(*---------------------------------------------------------------------------*)

fun fn_calls stmts qids = 
 let fun exps [] acc = acc
       | exps (Skip::t) acc = exps t acc
       | exps (Assign(_,e)::t) acc = exps t (e::acc)
       | exps (IfThenElse(e,s1,s2)::t) acc = exps (s1::s2::t) (e::acc)
       | exps (Case(e,cls)::t) acc = exps (map snd cls@t) (e::acc)
       | exps (Call(qid,elist)::t) acc = exps t (elist@acc)
       | exps (While(e,s)::t) acc = exps (s::t) (e::acc)
       | exps (For(_,e1,e2,istmt,body)::t) acc = exps (istmt::body::t) 
                                                      (e1::e2::acc)
       | exps (Block slist::t) acc = exps (slist@t) acc
       | exps (Check e::t) acc = exps t (e::acc)
 in 
   exp_calls (exps stmts []) qids
 end;

(*---------------------------------------------------------------------------*)
(* All calls made in a list of statements.                                   *)
(*---------------------------------------------------------------------------*)

fun all_calls stmts qids = 
 let fun calls stmts (qA,eA) = 
      case stmts
       of []                     => exp_calls eA qA
        | Skip::t                => calls t (qA,eA)
        | Assign(_,e)::t         => calls t (qA,e::eA)
        | IfThenElse(e,s1,s2)::t => calls (s1::s2::t) (qA, e::eA)
        | Case(e,cls)::t      => calls (map snd cls@t) (qA, e::eA)
        | Call(qid,elist)::t  => calls t (insert qid qA, elist@eA)
        | While(e,s)::t       => calls (s::t) (qA,e::eA)
        | For(_,e1,e2,i,s)::t => calls (i::s::t) (qA,e1::e2::eA)
        | Block slist::t      => calls (slist@t) (qA,eA)
        | Check e::t          => calls t (qA,e::eA)
 in
  calls stmts (qids,[])
 end

(*---------------------------------------------------------------------------*)
(* Not alpha-convertibility, in spite of quantifiers. Needs fixing!          *)
(*---------------------------------------------------------------------------*)

fun eqOpt eqt NONE NONE = true
 |  eqOpt eqt (SOME a) (SOME b) = eqt a b
 |  eqOpt any other thing = false;

fun eqTy typair =
  case typair 
   of (BaseTy x1, BaseTy x2) => x1=x2
    | (NamedTy qid1,NamedTy qid2) => qid1=qid2
    | (RecdTy (qid1,fields1), RecdTy (qid2,fields2)) 
        => let val fields1' = sort_on_string_key fields1
               val fields2' = sort_on_string_key fields2
           in qid1=qid2 andalso Lib.all2 eqFieldTy fields1' fields2'
           end 
    | (ArrayTy (ty1,exps1), ArrayTy (ty2,exps2)) 
        => eqTy(ty1,ty2) andalso Lib.all2 (curry eqExp) exps1 exps2
    | (other,wise) => false
and eqExp epair = 
  case epair 
  of (VarExp i1, VarExp i2) => i1=i2
   | (ConstExp(IdConst i1), ConstExp(IdConst i2))     => i1=i2
   | (ConstExp(BoolLit b1), ConstExp(BoolLit b2))     => b1=b2
   | (ConstExp(IntLit i1), ConstExp(IntLit i2))       => i1=i2
   | (ConstExp(CharLit c1), ConstExp(CharLit c2))     => c1=c2
   | (ConstExp(StringLit s1), ConstExp(StringLit s2)) => s1=s2
   | (ConstExp(RegexLit r1), ConstExp(RegexLit r2))   => r1=r2
   | (ConstExp(FloatLit f1), ConstExp(FloatLit f2))   => Real.==(f1,f2)
   | (Unop(op1,e1),Unop(op2,e2)) => op1=op2 andalso eqExp(e1,e2)
   | (Binop(op1,d1,d2),Binop(op2,e1,e2))
        => op1=op2 andalso eqExp(d1,e2) andalso eqExp(d2,e2)
   | (ArrayExp elist1,ArrayExp elist2) => Lib.all2 (curry eqExp) elist1 elist2
   | (ArrayIndex(e1,elist1),ArrayIndex(e2,elist2)) 
       => eqExp(e1,e2) andalso Lib.all2 (curry eqExp) elist1 elist2
   | (ConstrExp(q1,i1,eOpt1),ConstrExp(q2,i2,eOpt2))
       => q1=q2 andalso i1=i2 andalso eqOpt (curry eqExp) eOpt1 eOpt2
   | (Fncall(q1,elist1),Fncall(q2,elist2)) 
       => q1=q2 andalso Lib.all2 (curry eqExp) elist1 elist2
   | (RecdExp (qid1,fields1),RecdExp(qid2,fields2)) => 
       let val fields1' = sort_on_string_key fields1
           val fields2' = sort_on_string_key fields2
       in qid1=qid2 andalso Lib.all2 eqField fields1' fields2'
       end
   | (RecdProj(e1,i1),RecdProj(e2,i2)) => i1=i2 andalso eqExp(e1,e2)
   | (Quantified(q1,bvars1,e1),Quantified(q2,bvars2,e2)) 
       => let fun eqBvar(s1,ty1) (s2,ty2) = s1=s2 andalso eqTy(ty1,ty2)
          in q1=q2 andalso eqExp(e1,e2) andalso
             Lib.all2 eqBvar bvars1 bvars2
          end
   | (other,wise) => false
and
  eqField (id1,e1) (id2,e2) = id1=id2 andalso eqExp(e1,e2)
and
  eqFieldTy (id1,ty1) (id2,ty2) = id1=id2 andalso eqTy(ty1,ty2)
;

(*---------------------------------------------------------------------------*)
(* Subst for terms inside types.                                             *)
(*---------------------------------------------------------------------------*)

type ('a,'b) subst = ('a,'b) Lib.subst

fun substTy theta ty =
 case ty 
  of ArrayTy(ty,elist) => ArrayTy (substTy theta ty, map (substExp theta) elist)
   | RecdTy(qid,flist) => RecdTy (qid,map (I##substTy theta) flist)
   | otherwise => ty
and 
 substExp theta (exp:exp) = 
 case subst_assoc (curry eqExp exp) theta
  of SOME exp' => exp'
   | NONE => case exp
     of VarExp _ => exp
      | ConstExp _ => exp
      | Unop(opr,e) => Unop(opr,substExp theta e)
      | Binop(opr,e1,e2) => Binop(opr,substExp theta e1,substExp theta e2)
      | ArrayExp elist => ArrayExp(map (substExp theta) elist)
      | ArrayIndex(e,elist) => ArrayIndex
                                 (substExp theta e, map (substExp theta) elist)
      | ConstrExp(qid,id,NONE) => exp
      | ConstrExp(qid,id,SOME e) => ConstrExp(qid,id,SOME (substExp theta e))
      | Fncall(qid,elist) => Fncall(qid,map (substExp theta) elist)
      | RecdExp (qid,fields) => RecdExp(qid,map (I##substExp theta) fields)
      | RecdProj(e,id) => RecdProj(substExp theta e,id)
      | Quantified(q,qvars,e) => Quantified(q,qvars,substExp theta e)
;

(*---------------------------------------------------------------------------*)
(* TODO: support renaming in case of variable capture.                       *)
(*---------------------------------------------------------------------------*)

fun substStmt theta stmt =
 let val dom_theta = U(map (expVars o #redex) theta)
     val rng_theta = U(map (expVars o #residue) theta)
 in case stmt 
    of Skip => Skip
     | Assign(e1,e2) => Assign (substExp theta e1,substExp theta e2)
     | IfThenElse(e,s1,s2) => 
       IfThenElse(substExp theta e, substStmt theta s1,substStmt theta s2)
     | Case(e,rules) => 
        let fun subst_rule(p,s) = 
             let val V = expVars p
             in if null(intersect V dom_theta) andalso
                   null (intersect V rng_theta)
                  then (p,substStmt theta s)
                  else (p,s)
             end
        in Case (substExp theta e,map subst_rule rules)
         end
     | Call(qid,args) => Call(qid,map (substExp theta) args)
     | Block stmts => Block(map (substStmt theta) stmts)
     | While(e,stmt) => While(substExp theta e, substStmt theta stmt)
     | For((id,ty),e1,e2,istmt,body) => 
        if mem id dom_theta then stmt
         else For((id,ty),substExp theta e1, substExp theta e2,
                  substStmt theta istmt,substStmt theta body)
     | Check e => Check (substExp theta e)
 end

fun subst_LR_Stmt ltheta rtheta =
 let val dom_rtheta = U(map (expVars o #redex) rtheta)
     val rng_rtheta = U(map (expVars o #residue) rtheta)
     fun subst stmt = 
      case stmt 
       of Skip => Skip
        | Assign(e1,e2) => Assign (substExp ltheta e1,substExp rtheta e2)
        | IfThenElse(e,s1,s2) => 
          IfThenElse(substExp rtheta e, subst s1,subst s2)
        | Case(e,rules) => 
           let fun subst_rule(p,s) = 
                let val V = expVars p
                in if null(intersect V dom_rtheta) andalso
                      null (intersect V rng_rtheta)
                     then (p,subst s)
                     else (p,s)
                end
           in Case (substExp rtheta e,map subst_rule rules)
            end
        | Call(qid,args) => Call(qid,map (substExp rtheta) args)
        | Block stmts => Block(map subst stmts)
        | While(e,stmt) => While(substExp rtheta e, subst stmt)
        | For((id,ty),e1,e2,istmt,body) => 
           if mem id dom_rtheta then stmt else 
           For((id,ty),substExp rtheta e1, substExp rtheta e2, subst istmt,subst body)
        | Check e => Check (substExp rtheta e)
 in 
  subst
 end
  
fun substVdec theta (id,ty) = (id,substTy theta ty);
  
fun substParam theta param = 
 case param
  of In vdec => In (substVdec theta vdec)
   | Out vdec => Out(substVdec theta vdec)
   | InOut vdec => InOut(substVdec theta vdec);

fun substConstr theta (id,argtys) = (id, map (substTy theta) argtys);

fun substDecl theta decl = 
 case decl 
  of NumTyDecl nkind => decl
   | TyAbbrevDecl(id,ty) => TyAbbrevDecl (id,substTy theta ty)
   | RecdDecl(id,flds)   => RecdDecl(id, map (I##substTy theta) flds)
   | DatatypeDecl(id,constrs) 
     => DatatypeDecl(id, map (substConstr theta) constrs)
   | GraphDecl(id,nty,ety) => GraphDecl(id,substTy theta nty,substTy theta ety)
   | VarDecl vdec => VarDecl(substVdec theta vdec)
   | ConstDecl(id,ty,exp) => ConstDecl(id,substTy theta ty,substExp theta exp)
   | SizedDataDecl(id,ty,e1,eopt) 
      => SizedDataDecl(id,substTy theta ty,substExp theta e1,
                       Option.map (substExp theta) eopt)
   | SizedGraphDecl(id,ty,e1,e2) 
      => SizedGraphDecl(id,substTy theta ty, substExp theta e1,substExp theta e2)
   | EfnDecl(id,params,retvalOpt) 
     => EfnDecl(id,map (substParam theta) params,
                   Option.map (substVdec theta) retvalOpt)
   | FnDecl(id,params,retvalOpt,locals,stmts) 
     => FnDecl(id,map (substParam theta) params,
               Option.map (substVdec theta) retvalOpt,
               map (substVdec theta) locals,
               map (substStmt theta) stmts)
   | SpecDecl(id,vdecs,stmts) 
     => SpecDecl(id, map (substVdec theta) vdecs,
                 map (substStmt theta) stmts)
   | CommentDecl _ => decl

(*---------------------------------------------------------------------------*)
(* Substitution of types for types                                           *)
(*---------------------------------------------------------------------------*)

fun substTyTy theta ty = 
  case subst_assoc (curry eqTy ty) theta
   of SOME ty1 => ty1
    | NONE => 
      let in 
       case ty 
        of ArrayTy(ty,elist) => ArrayTy (substTyTy theta ty, 
                                          map (substTyExp theta) elist)
         | RecdTy(qid,flist) => RecdTy (qid, map (I##substTyTy theta) flist)
         | otherwise => ty
      end
and substTyExp theta exp = 
  case exp
    of VarExp _ => exp
     | ConstExp _ => exp
     | Unop(opr,e) => Unop(opr,substTyExp theta e)
     | Binop(opr,e1,e2) => Binop(opr,substTyExp theta e1,substTyExp theta e2)
     | ArrayExp elist => ArrayExp(map (substTyExp theta) elist)
     | ArrayIndex(e,elist) => ArrayIndex(substTyExp theta e, 
                                         map (substTyExp theta) elist)
     | ConstrExp(qid,id,NONE) => exp
     | ConstrExp(qid,id,SOME e) => ConstrExp(qid,id,SOME (substTyExp theta e))
     | Fncall(qid,elist) => Fncall(qid,map (substTyExp theta) elist)
     | RecdExp(qid,fields) => RecdExp(qid,map (I##substTyExp theta) fields)
     | RecdProj(e,id) => RecdProj(substTyExp theta e,id)
     | Quantified(quant,bvars,e) 
       => Quantified(quant,map (I##substTyTy theta) bvars, substTyExp theta e)

fun substTyVdec theta (id,ty) = (id,substTyTy theta ty);
fun substTyParam theta param = 
 case param
  of In vdec  => In (substTyVdec theta vdec)
   | Out vdec  => Out(substTyVdec theta vdec)
   | InOut vdec => InOut(substTyVdec theta vdec);

fun substTyConstr theta (id,argtys) = (id, map (substTyTy theta) argtys);

fun substTyStmt theta stmt =
 case stmt 
  of Skip => Skip
   | Assign(e1,e2) => Assign (substTyExp theta e1,substTyExp theta e2)
   | IfThenElse(e,s1,s2) => 
        IfThenElse(substTyExp theta e,
                   substTyStmt theta s1,substTyStmt theta s2)
   | Case(e,rules) => 
      let fun subst_rule(p,s) = (substTyExp theta p, substTyStmt theta s)
      in Case (substTyExp theta e,map subst_rule rules)
      end
   | Call(qid,args) => Call(qid,map (substTyExp theta) args)
   | Block stmts    => Block(map (substTyStmt theta) stmts)
   | While(e,stmt)  => While(substTyExp theta e, substTyStmt theta stmt)
   | For(v,e1,e2,istmt,body) 
     => For(substTyVdec theta v,
            substTyExp theta e1, 
            substTyExp theta e2,
            substTyStmt theta istmt,
            substTyStmt theta body)
   | Check e => Check (substTyExp theta e)


fun substTyDecl theta decl = 
 case decl 
  of NumTyDecl nkind => decl
   | TyAbbrevDecl(id,ty) 
     => TyAbbrevDecl (id,substTyTy theta ty)
   | RecdDecl(id,flds)
     => RecdDecl(id, map (substTyVdec theta) flds)
   | DatatypeDecl(id,constrs) 
     => DatatypeDecl(id, map (substTyConstr theta) constrs)
   | GraphDecl(id,nty,ety) 
     => GraphDecl(id,substTyTy theta nty,substTyTy theta ety)
   | VarDecl vdec 
     => VarDecl(substTyVdec theta vdec)
   | ConstDecl(id,ty,exp) 
     => ConstDecl(id,substTyTy theta ty,substTyExp theta exp)
   | SizedDataDecl(id,ty,e1,eopt) 
     => SizedDataDecl(id,substTyTy theta ty,
                      substTyExp theta e1,Option.map (substTyExp theta) eopt)
   | SizedGraphDecl(id,ty,e1,e2) 
     => SizedGraphDecl(id,substTyTy theta ty,
                       substTyExp theta e1,substTyExp theta e2)
   | EfnDecl(id,params,retvalOpt) 
     => EfnDecl(id,map (substTyParam theta) params,
                   Option.map (substTyVdec theta) retvalOpt)
   | FnDecl(id,params,retvalOpt,locals,stmts) 
     => FnDecl(id,map (substTyParam theta) params,
               Option.map (substTyVdec theta) retvalOpt,
               map (substTyVdec theta) locals,
               map (substTyStmt theta) stmts)
   | SpecDecl(id,vdecs,stmts) 
     => SpecDecl(id, map (substTyVdec theta) vdecs,
                 map (substTyStmt theta) stmts)
   | CommentDecl _ => decl

(*---------------------------------------------------------------------------*)
(* Replace a qid throughout a ty/exp/stmt/decl/pkg                           *)
(*---------------------------------------------------------------------------*)

fun alistFn alist x = 
 case subst_assoc (equal x) alist
  of SOME y => y
   | NONE => x;

fun substQidTy theta ty =
   case ty 
    of NamedTy qid => NamedTy(alistFn theta qid)
     | RecdTy(qid,flist) => RecdTy (alistFn theta qid,map (I##substQidTy theta) flist)
     | ArrayTy(ty,elist) => ArrayTy (substQidTy theta ty, 
                                     map (substQidExp theta) elist)
     | otherwise => ty
and substQidExp theta (exp:exp) = 
   case exp
    of VarExp _ => exp
     | ConstExp (IdConst qid) => ConstExp(IdConst (alistFn theta qid))
     | ConstExp _ => exp
     | Unop(opr,e) => Unop(opr,substQidExp theta e)
     | Binop(opr,e1,e2) => Binop(opr,substQidExp theta e1,substQidExp theta e2)
     | ArrayExp elist => ArrayExp(map (substQidExp theta) elist)
     | ArrayIndex(e,elist) => ArrayIndex
                  (substQidExp theta e, map (substQidExp theta) elist)
     | ConstrExp(qid,id,NONE) => ConstrExp(alistFn theta (qid), id, NONE)
     | ConstrExp(qid,id,SOME e) => ConstrExp(alistFn theta (qid),
                                            id,SOME (substQidExp theta e))
     | Fncall(qid,elist) => Fncall(alistFn theta(qid),
                                   map (substQidExp theta) elist)
     | RecdExp(qid,fields) => RecdExp(alistFn theta(qid),
                                      map (I##substQidExp theta) fields)
     | RecdProj(e,id) => RecdProj(substQidExp theta e,id)
     | Quantified(q,qvars,e) => Quantified(q,qvars,substQidExp theta e)

fun substQidStmt theta stmt =
 case stmt 
  of Skip => Skip
   | Assign(e1,e2) => Assign (substQidExp theta e1,substQidExp theta e2)
   | IfThenElse(e,s1,s2) => 
     IfThenElse(substQidExp theta e, 
                substQidStmt theta s1,substQidStmt theta s2)
   | Case(e,rules) => Case (substQidExp theta e,
                            map (I##substQidStmt theta) rules)
   | Call(qid,args) => Call(alistFn theta(qid),map (substQidExp theta) args)
   | Block stmts => Block(map (substQidStmt theta) stmts)
   | While(e,stmt) => While(substQidExp theta e, substQidStmt theta stmt)
   | For((id,ty),e1,e2,istmt,body) => 
       For((id,ty),substQidExp theta e1, substQidExp theta e2,
           substQidStmt theta istmt,substQidStmt theta body)
   | Check e => Check (substQidExp theta e)
;
  
fun substQidVdec theta (id,ty) = (id,substQidTy theta ty);
  
fun substQidParam theta param = 
 case param
  of In vdec => In (substQidVdec theta vdec)
   | Out vdec => Out(substQidVdec theta vdec)
   | InOut vdec => InOut(substQidVdec theta vdec);

fun substQidConstr theta (id,argtys) = (id, map (substQidTy theta) argtys);

fun substQidDecl theta decl = 
 case decl 
  of NumTyDecl nkind => decl
   | TyAbbrevDecl(id,ty) => TyAbbrevDecl (id,substQidTy theta ty)
   | RecdDecl(id,flds)   => RecdDecl(id, map (I##substQidTy theta) flds)
   | DatatypeDecl(id,constrs) 
     => DatatypeDecl(id, map (substQidConstr theta) constrs)
   | GraphDecl(id,nty,ety) => GraphDecl(id,substQidTy theta nty,
                                           substQidTy theta ety)
   | VarDecl vdec => VarDecl(substQidVdec theta vdec)
   | ConstDecl(id,ty,exp) => ConstDecl(id,substQidTy theta ty,
                                          substQidExp theta exp)
   | SizedDataDecl(id,ty,e1,eopt) 
      => SizedDataDecl(id,substQidTy theta ty,
                       substQidExp theta e1,Option.map (substQidExp theta) eopt)
   | SizedGraphDecl(id,ty,e1,e2) 
      => SizedGraphDecl(id,substQidTy theta ty,
                        substQidExp theta e1,substQidExp theta e2)
   | EfnDecl(id,params,retvalOpt) 
     => EfnDecl(id,map (substQidParam theta) params,
                   Option.map (substQidVdec theta) retvalOpt)
   | FnDecl(id,params,retvalOpt,locals,stmts) 
     => FnDecl(id,map (substQidParam theta) params,
               Option.map (substQidVdec theta) retvalOpt,
               map (substQidVdec theta) locals,
               map (substQidStmt theta) stmts)
   | SpecDecl(id,vdecs,stmts) 
     => SpecDecl(id, map (substQidVdec theta) vdecs,
                 map (substQidStmt theta) stmts)
   | CommentDecl _ => decl

fun substQidPkg theta (pkgName,decls) = (pkgName, map (substQidDecl theta) decls);

(*---------------------------------------------------------------------------*)
(* Ids in a package                                                          *)
(*---------------------------------------------------------------------------*)

type ids = id list;

fun tyIds ty set =
   case ty 
    of BaseTy _ => set
     | NamedTy (_,id) => insert id set
     | RecdTy((_,id),flist) => itlist (tyIds o snd) flist (insert id set)
     | ArrayTy(ty,elist) => tyIds ty (itlist expIds elist set)
and expIds exp set =
   case exp
    of VarExp id => insert id set
     | ConstExp (IdConst (_,id)) => insert id set
     | ConstExp _ => set
     | Unop(opr,e) => expIds e set
     | Binop(opr,e1,e2) => expIds e2 (expIds e1 set)
     | ArrayExp elist => itlist expIds elist set
     | ArrayIndex(e,elist) => itlist expIds (e::elist) set
     | ConstrExp((_,id1),id2,NONE) => insert id1 (insert id2 set)
     | ConstrExp((_,id1),id2,SOME e) => expIds e (insert id1 (insert id2 set))
     | Fncall((_,id),elist) => itlist expIds elist (insert id set)
     | RecdExp((_,id),fields) => itlist expIds (map snd fields) (insert id set)
     | RecdProj(e,id) => expIds e (insert id set)
     | Quantified(q,qvars,e) => expIds e (itlist tyIds (map snd qvars) set)

fun stmtIds stmt set =
 case stmt 
  of Skip => set
   | Assign(e1,e2) => expIds e2 (expIds e1 set)
   | IfThenElse(e,s1,s2) => stmtIds s2 (stmtIds s1 (expIds e set))
   | Case(e,rules) => itlist (fn (e,s) => fn t => stmtIds s (expIds e t)) 
                             rules (expIds e set)
   | Call((_,id),args) => itlist expIds args (insert id set)
   | Block stmts => itlist stmtIds stmts set
   | While(e,stmt) => stmtIds stmt (expIds e set)
   | For((id,ty),e1,e2,istmt,body) => 
      stmtIds body
       (stmtIds istmt
          (expIds e2 (expIds e1 (tyIds ty (insert id set)))))
   | Check e => expIds e set
;
  
fun constrIds (cname,tys) set = itlist tyIds tys (insert cname set);
    
fun declIds decl set = 
 case decl 
  of NumTyDecl nkind => set
   | RecdDecl(id,flds) => itlist tyIds (map snd flds) (insert id set)
   | TyAbbrevDecl(id,ty) => tyIds ty (insert id set)
   | DatatypeDecl(id,constrs) => itlist constrIds constrs (insert id set)
   | GraphDecl(id,nty,ety) => tyIds ety (tyIds nty (insert id set))
   | VarDecl (id,ty) => tyIds ty (insert id set)
   | ConstDecl(id,ty,exp) => expIds exp (tyIds ty (insert id set))
   | SizedDataDecl(id,ty,e1,NONE) 
      => expIds e1 (tyIds ty (insert id set))
   | SizedDataDecl(id,ty,e1,SOME e2) 
      => expIds e2 (expIds e1 (tyIds ty (insert id set)))
   | SizedGraphDecl(id,ty,e1,e2) 
      => expIds e2 (expIds e1 (tyIds ty (insert id set)))
   | EfnDecl(id,params,retvalOpt) => 
      let val tys = map (snd o dest_param) params @ 
                    map snd (optlist retvalOpt)
      in itlist tyIds tys (insert id set)
      end
   | FnDecl(id,params,retvalOpt,locals,stmts) => 
      let val tys = map (snd o dest_param) params @ 
                    map snd (optlist retvalOpt) @ 
                    map snd locals
      in stmtIds (Block stmts) (itlist tyIds tys (insert id set))
      end
   | SpecDecl(id,vdecs,stmts) => 
        stmtIds (Block stmts) (itlist tyIds (map snd vdecs) (insert id set))
   | CommentDecl _ => set;

fun pkgIds (pkgName,decls) set = itlist declIds decls (insert pkgName set);


(* -------------------------------------------------------------------------- *)
(* Qids in a package.                                                         *)
(* -------------------------------------------------------------------------- *)

type qids = qid list;

fun tyQids ty set =
   case ty 
    of BaseTy _ => set
     | NamedTy qid => insert qid set
     | RecdTy(qid,flist) => itlist (tyQids o snd) flist set
     | ArrayTy(ty,elist) => tyQids ty (itlist expQids elist set)
and expQids exp set =
   case exp
    of VarExp _ => set
     | ConstExp (IdConst qid) => insert qid set
     | ConstExp _ => set
     | Unop(opr,e) => expQids e set
     | Binop(opr,e1,e2) => expQids e2 (expQids e1 set)
     | ArrayExp elist => itlist expQids elist set
     | ArrayIndex(e,elist) => itlist expQids (e::elist) set
     | ConstrExp(qid,id,NONE) => insert qid set
     | ConstrExp(qid,id,SOME e) => expQids e (insert qid set)
     | Fncall(qid,elist) => itlist expQids elist (insert qid set)
     | RecdExp(qid,fields) => itlist expQids (map snd fields) (insert qid set)
     | RecdProj(e,id) => expQids e set
     | Quantified(q,qvars,e) => expQids e (itlist tyQids (map snd qvars) set)

fun stmtQids stmt set =
 case stmt 
  of Skip => set
   | Assign(e1,e2) => expQids e2 (expQids e1 set)
   | IfThenElse(e,s1,s2) => stmtQids s2 (stmtQids s1 (expQids e set))
   | Case(e,rules) => itlist (fn (e,s) => fn t => stmtQids s (expQids e t)) 
                             rules (expQids e set)
   | Call(qid,args) => itlist expQids args (insert qid set)
   | Block stmts => itlist stmtQids stmts set
   | While(e,stmt) => stmtQids stmt (expQids e set)
   | For((id,ty),e1,e2,istmt,body) => 
      stmtQids body
       (stmtQids istmt
          (expQids e2 (expQids e1 (tyQids ty set))))
   | Check e => expQids e set
;
  
fun constrQids (cname,tys) set = itlist tyQids tys set;
    
fun declQids decl set = 
 case decl 
  of NumTyDecl nkind => set
   | RecdDecl(id,flds) => itlist tyQids (map snd flds) set
   | TyAbbrevDecl(id,ty) => tyQids ty set
   | DatatypeDecl(id,constrs) => itlist constrQids constrs set
   | GraphDecl(id,nty,ety) => tyQids nty (tyQids ety set)
   | VarDecl (id,ty) => tyQids ty set
   | ConstDecl(id,ty,exp) => expQids exp (tyQids ty set)
   | SizedDataDecl(id,ty,e1,eopt) => 
       (case eopt 
         of NONE => expQids e1 (tyQids ty set)
          | SOME e2 => expQids e2 (expQids e1 (tyQids ty set)))
   | SizedGraphDecl(id,ty,e1,e2) => expQids e2 (expQids e1 (tyQids ty set))
   | EfnDecl(id,params,retvalOpt) => 
      let val tys = map (snd o dest_param) params @ 
                    map snd (optlist retvalOpt)
      in itlist tyQids tys set
      end
   | FnDecl(id,params,retvalOpt,locals,stmts) => 
      let val tys = map (snd o dest_param) params @ 
                    map snd (optlist retvalOpt) @ 
                    map snd locals
      in stmtQids (Block stmts) (itlist tyQids tys set)
      end
   | SpecDecl(id,vdecs,stmts) => 
        stmtQids (Block stmts) (itlist tyQids (map snd vdecs) set)
   | CommentDecl _ => set;

fun pkgQids (pkgName,decls) set = itlist declQids decls set;

(* -------------------------------------------------------------------------- *)
(* Rename a package.                                                          *)
(* -------------------------------------------------------------------------- *)

fun renamePkg s (pkg as (pkgName,decls)) =
  let val qids = pkgQids pkg []
      val relevant = filter (equal pkgName o fst) qids
      val replacements = map (fn (a,b) => (s,b)) relevant
      val theta = map2 (fn x => fn y => {redex = x, residue = y}) 
                       relevant replacements
  in
   (s, map (substQidDecl theta) decls)
  end;

(* -------------------------------------------------------------------------- *)
(* Find out the variables in the program, organized by function. This is used *)
(* to declare the state record.                                               *)
(* -------------------------------------------------------------------------- *)

fun expDecs e = []; 

fun stmtDecs Skip = []
  | stmtDecs (Assign _) = []
  | stmtDecs (Call(qid,expl)) = []
  | stmtDecs (Check e) = []
  | stmtDecs (IfThenElse(_,s1,s2)) = List.concat [stmtDecs s1,stmtDecs s2]
  | stmtDecs (Case(e,cases)) = 
      let fun caseId(e,stmt) = stmtDecs stmt
      in List.concat (map caseId cases)
      end
  | stmtDecs (While(e,stmt)) = stmtDecs stmt
  | stmtDecs (For((id,ty),e1,e2,istmt,body)) = 
        (id,ty)::List.concat [stmtDecs istmt, stmtDecs body]
  | stmtDecs (Block stmts) = List.concat (map stmtDecs stmts);

fun eq_vDec ((id1,ty1):vdec) ((id2,ty2):vdec) = id1=id2 andalso eqTy(ty1,ty2);

fun tyDecs ty = 
 case ty 
  of BaseTy _ => []
   | NamedTy _ => []
   | RecdTy (qid,fields) => 
      let val ftys = map snd fields
      in itlist (fn ty => fn set => op_union eq_vDec (tyDecs ty) set) ftys []
      end
   | ArrayTy (ty,dims) => 
      let val tydecs = tyDecs ty
          val varExps = List.concat (map expVars dims)
          fun nat_vdec id = (id, BaseTy(IntTy(Nat NONE)))
          val dim_decs = map nat_vdec varExps
      in tydecs @ dim_decs
      end

fun vdecDecs (id,ty) = (id,ty)::tyDecs ty;

val paramDecs = vdecDecs o dest_param;

val mk_vdec_set = op_mk_set eq_vDec;

fun pkg_varDecs (name,decls) = 
 let fun varDecs decl = 
      case decl 
       of FnDecl(id,args,optret,locals,stmts) =>
          let val pdecs = List.concat(map paramDecs args)
              val odecs = List.concat (map vdecDecs (optlist optret))
              val ldecs = List.concat (map vdecDecs locals)
              val sdecs = stmtDecs (Block stmts)
              val alldecs = mk_vdec_set (pdecs @ odecs @ ldecs @ sdecs)
          in
            (id,alldecs)
          end
       | EfnDecl(id,args,optret) => 
          let val pdecs = List.concat(map paramDecs args)
              val odecs = List.concat (map vdecDecs (optlist optret))
              val alldecs = mk_vdec_set pdecs
          in
            (id,alldecs)
          end
       | SpecDecl(id,locals,stmts) =>
          let val ldecs = List.concat(map vdecDecs locals)
              val sdecs = stmtDecs (Block stmts)
              val alldecs = mk_vdec_set (ldecs @ sdecs)
          in
            (id,alldecs)
          end
       | VarDecl v => ("",[v])  (* probably need to agglomerate all VarDecls *)
       | otherwise => raise ERR "varDecs" "unexpected decl"
 in
   mapfilter varDecs decls
 end;

fun splitPkg (pkgName,decls) = 
 let val types = mapfilter (fn (x as RecdDecl _) => x
                             | (x as DatatypeDecl _) => x
                             | (x as GraphDecl _) => x) decls
     val vdecls = mapfilter (fn (x as VarDecl _) => x
                              | (x as SizedDataDecl _) => x
                              | (x as SizedGraphDecl _) => x) decls
     val edecls = mapfilter (fn EfnDecl x => x) decls
     val fdecls = mapfilter (fn (x as ConstDecl _) => x
                              | (x as FnDecl _) => x) decls
     val sdecls = mapfilter (fn SpecDecl x => x) decls
 in 
  (types,vdecls,edecls,fdecls,sdecls)
 end


(*---------------------------------------------------------------------------*)
(* Matching (simple) for types.                                              *)
(*---------------------------------------------------------------------------*)
(*
val MATCH_TY_ERR = ERR "matchTy"
val MATCH_EXP_ERR = ERR "matchExp"

fun matchTy fixed typair =
  case typair 
   of (BaseTy x1, BaseTy x2) 
       => if x1=x2 then [] 
            else raise MATCH_TY_ERR "disagreement at base types"
    | (NamedTy qid1,NamedTy qid2) 
       => if qid1=qid2 then [] 
            else raise MATCH_TY_ERR "disagreement at named types"
    | (RecdTy (qid1,flds1), RecdTy (qid2,flds2)) 
       => if qid1 <> qid2 then 
            raise MATCH_TY_ERR "disagreement at record types"
          else
          let val fields1 = sort_on_string_key flds1
              val fields2 = sort_on_string_key flds2
              fun matchFieldTy (s1,ty1) (s2,ty2) = 
                if s1=s2 then matchTy fixed (ty1,ty2)
                 else raise MATCH_TY_ERR ("disagreement at record field "^s1)
              val fldpairs = zip fields1 fields2 
                     handle e => raise MATCH_TY_ERR 
                                 "disagreement in number of record fields"
              val thetans = map matchFieldTy fldpairs
              fun norm [] = []
                | norm ([]::t) = norm t
                | norm (bindings::t) = 
                   let val t' = norm t
                       fun add_bindings (v,e) list = 
                          case assoc1 v list
                            of NONE => (v,e)::list
                             | SOME (_,e') => if e = e' then list
                                 else raise MATCH_TY_ERR 
                                  ("disagreement in binding for variable "^fst v)
                   in itlist add_binding bindings t'
                   end
          in norm thetans
          end
    | (ArrayTy (ty1,exps1), ArrayTy (ty2,exps2)) 
        => matchTy(ty1,ty2) andalso Lib.all2 (curry matchExp) exps1 exps2
    | (other,wise) => false
and matchExp fixed epair = 
  case epair 
  of (VarExp i1, VarExp i2) => i1=i2
   | (ConstExp(IdConst i1), ConstExp(IdConst i2))     => i1=i2
   | (ConstExp(BoolLit b1), ConstExp(BoolLit b2))     => b1=b2
   | (ConstExp(IntLit i1), ConstExp(IntLit i2))       => i1=i2
   | (ConstExp(CharLit c1), ConstExp(CharLit c2))     => c1=c2
   | (ConstExp(StringLit s1), ConstExp(StringLit s2)) => s1=s2
   | (ConstExp(FloatLit f1), ConstExp(FloatLit f2))   => Real.==(f1,f2)
   | (Unop(op1,e1),Unop(op2,e2)) => op1=op2 andalso matchExp(e1,e2)
   | (Binop(op1,d1,d2),Binop(op2,e1,e2))
        => op1=op2 andalso matchExp(d1,e2) andalso matchExp(d2,e2)
   | (ArrayExp elist1,ArrayExp elist2) => Lib.all2 (curry matchExp) elist1 elist2
   | (ArrayIndex(e1,elist1),ArrayIndex(e2,elist2)) 
       => matchExp(e1,e2) andalso Lib.all2 (curry matchExp) elist1 elist2
   | (ConstrExp(q1,i1,eOpt1),ConstrExp(q2,i2,eOpt2))
       => q1=q2 andalso i1=i2 andalso eqOpt (curry matchExp) eOpt1 eOpt2
   | (Fncall(q1,elist1),Fncall(q2,elist2)) 
       => q1=q2 andalso Lib.all2 (curry matchExp) elist1 elist2
   | (RecdExp (qid1,fields1),RecdExp(qid2,fields2)) => 
       let fun eqField (s1,e1) (s2,e2) = s1=s2 andalso matchExp(e1,e2)
       in qid1=qid2 andalso Lib.all2 eqField fields1 fields2
       end
   | (RecdProj(e1,i1),RecdProj(e2,i2)) => i1=i2 andalso matchExp(e1,e2)
   | (Quantified(q1,bvars1,e1),Quantified(q2,bvars2,e2)) 
       => let fun eqBvar(s1,ty1) (s2,ty2) = s1=s2 andalso matchTy(ty1,ty2)
          in q1=q2 andalso matchExp(e1,e2) andalso
             Lib.all2 eqBvar bvars1 bvars2
          end
   | (other,wise) => false
*)


(*---------------------------------------------------------------------------*)
(* Headers for all constants and functions in a package.                     *)
(*---------------------------------------------------------------------------*)

type fnsig = qid * (param list * vdec option)

fun fnsigs_of (pkgName,decls) = 
 let fun header_of (EfnDecl(id,params,retOpt)) = ((pkgName,id),(params,retOpt))
       | header_of (FnDecl(id,params,retOpt,_,_)) = ((pkgName,id),(params,retOpt))
       | header_of (ConstDecl(id,ty,exp)) = ((pkgName,id),([],SOME("",ty)))
       | header_of otherwise = raise ERR "" ""
 in Lib.mapfilter header_of decls
 end;

(*---------------------------------------------------------------------------*)
(* Headers for all type abbrevs in a package.                                *)
(*---------------------------------------------------------------------------*)

type tysig = qid * ty

fun tysigs_of (pkgName,decls) = 
 let fun header_of (TyAbbrevDecl (id,ty)) = ((pkgName,id),ty)
       | header_of (RecdDecl (id,flds)) = ((pkgName,id), NamedTy(pkgName,id))
       | header_of otherwise = raise ERR "" ""
 in Lib.mapfilter header_of decls
 end;

fun resolve_named_ty qid tysigs = 
 let fun chase qid n = 
   if n < 0 then 
    raise ERR "resolve_named_ty" "possible cycle in type abbrevs"
    else case assoc1 qid tysigs
          of SOME (_,NamedTy qid') => chase qid' (n-1)
           | SOME (_, ty) => ty
           | NONE => raise ERR "resolve_named_ty" 
                     ("unknown type abbrev: "^qid_string qid)
           
 in chase qid 10
 end;

(* -------------------------------------------------------------------------- *)
(* Coercing the sign of a number object                                       *)
(* -------------------------------------------------------------------------- *)

fun is_int (BaseTy(IntTy _)) = true
  | is_int otherwise = false

fun is_unbounded_uint (BaseTy(IntTy(Nat NONE))) = true
  | is_unbounded_uint otherwise = false

fun exp (n,e) = 
  if n<0 orelse e<0 then raise ERR "exp" "negative input"
  else if e = 0 then 1
  else n * exp(n,e-1);

fun is_uint_literal (ConstExp(IntLit{kind=Nat NONE,value})) = true
  | is_uint_literal (ConstExp(IntLit{kind=Nat(SOME width),value})) = 
      0 <= value andalso value < exp(2,width)
  | is_uint_literal otherwise = false;

fun is_int_literal (ConstExp(IntLit _)) = true
  | is_int_literal otherwise = false;

fun is_signed (BaseTy(IntTy(Int _))) = true
  | is_signed otherwise = false

fun is_unsigned (BaseTy(IntTy(Nat _))) = true
  | is_unsigned otherwise = false

fun is_bounded (BaseTy(IntTy(Int(SOME _)))) = true
  | is_bounded (BaseTy(IntTy(Nat(SOME _)))) = true
  | is_bounded otherwise = false

fun flip_sign Signed (BaseTy(IntTy(Nat x))) = BaseTy(IntTy(Int x))
  | flip_sign Signed (ty as BaseTy(IntTy(Int _))) = ty
  | flip_sign Unsigned (BaseTy(IntTy(Int x))) = BaseTy(IntTy(Nat x))
  | flip_sign Unsigned (ty as BaseTy(IntTy(Nat _))) = ty
  | flip_sign uop otherwise = raise ERR "ty_of" "flip_sign: expected a number"

fun drop_bound ty = 
 case ty 
  of BaseTy(IntTy(Nat NONE)) => ty
   | BaseTy(IntTy(Int NONE)) => ty
   | BaseTy(IntTy(Nat (SOME _))) => BaseTy(IntTy(Nat NONE))
   | BaseTy(IntTy(Int (SOME _))) => BaseTy(IntTy(Int NONE))
   | otherwise => raise ERR "ty_of" "drop_bound: expected a numeric type";

(*---------------------------------------------------------------------------*)
(* Typecheck a package. This is duplicated in the frontend. We have to       *)
(* visit every sub{type,term,stmt,decl} in the package.                      *)
(*---------------------------------------------------------------------------*)

val TC_ERR = ERR "typecheck";

val unitTy = NamedTy("","unit")  (* range type of a procedure *)

type tyenv 
      = (qid -> ty)                   (* abbrEnv *)
         * (id -> ty)                 (* varEnv *)
         * (qid -> ty list * ty)      (* constEnv *)
         * (qid * id -> ty list * ty) (* constrEnv *)
         * (qid -> (id * ty) list)    (* recdEnv *)
         * qid list                   (* specEnv *) 
;

fun join_tyenv (abbrEnv1,varEnv1,constEnv1,constrEnv1,recdEnv1,specEnv1)
               (abbrEnv2,varEnv2,constEnv2,constrEnv2,recdEnv2,specEnv2) = 
 let fun abbrEnv x   = (abbrEnv1 x   handle _ => abbrEnv2 x)
     fun varEnv x    = (varEnv1 x    handle _ => varEnv2 x)
     fun constEnv x  = (constEnv1 x  handle _ => constEnv2 x)
     fun constrEnv x = (constrEnv1 x handle _ => constrEnv2 x)
     fun recdEnv x   = (recdEnv1 x   handle _ => recdEnv2 x)
     val specEnv     = specEnv1@specEnv2
 in 
   (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv)
 end

(*---------------------------------------------------------------------------*)
(* Type abbreviation expansion in types and terms                            *)
(*---------------------------------------------------------------------------*)

fun applyAbbrev (abbrEnv,recdEnv) qid = 
(* check recdEnv first, because all recd decls are dummy stored in abbrEnv *)
 RecdTy(qid,recdEnv qid) handle HOL_ERR _ 
 => 
 abbrEnv qid handle HOL_ERR _ 
 => 
 raise TC_ERR ("expandTy : undefined type: "^qid_string qid);

fun qid_map E qid = 
  case applyAbbrev E qid 
   of NamedTy qid' => qid'
    | otherwise => raise TC_ERR ("qid_map : undefined type: "^qid_string qid);

fun expand1Ty E ty =
 case ty
  of BaseTy _ => ty
   | NamedTy(qid) => applyAbbrev E qid
   | RecdTy(qid,flist) => RecdTy (qid, map (I##expand1Ty E) flist)
   | ArrayTy(ty,dlist) => ArrayTy (expand1Ty E ty, map (expand1TyExp E) dlist)
 and expand1TyExp E exp = 
   case exp
    of VarExp _ => exp
     | ConstExp _ => exp
     | Unop(opr,e) => Unop(opr,expand1TyExp E e)
     | Binop(opr,e1,e2) => Binop(opr,expand1TyExp E e1,expand1TyExp E e2)
     | ArrayExp elist => ArrayExp(map (expand1TyExp E) elist)
     | ArrayIndex(e,elist) => 
          ArrayIndex(expand1TyExp E e, map (expand1TyExp E) elist)
     | ConstrExp(qid,id,NONE) => ConstrExp(qid_map E qid,id,NONE)
     | ConstrExp(qid,id,SOME e) => 
          ConstrExp(qid_map E qid, id,SOME (expand1TyExp E e))
     | Fncall(qid,elist) => Fncall(qid,map (expand1TyExp E) elist)
     | RecdExp(qid,fields) => 
          RecdExp(qid_map E qid, map (I##expand1TyExp E) fields)
     | RecdProj(e,id) => RecdProj(expand1TyExp E e,id)
     | Quantified(quant,bvars,e) => 
         Quantified(quant,map (I##expand1Ty E) bvars, expand1TyExp E e)
;

fun expandTyN E n ty = 
  if n <= 0 then ty else 
   let val ty' = expand1Ty E ty
   in if eqTy(ty,ty') then ty else expandTyN E (n-1) ty'
   end;
fun expandTyExpN E n exp = 
  if n <= 0 then exp else 
   let val exp' = expand1TyExp E exp
   in if eqExp(exp,exp') then exp else expandTyExpN E (n-1) exp'
   end;

val TYABBREV_EXPANSION_BOUND = 17;

fun expandTy E ty = expandTyN E TYABBREV_EXPANSION_BOUND ty;
fun expandTyExp E exp = expandTyExpN E TYABBREV_EXPANSION_BOUND exp;

(*---------------------------------------------------------------------------*)
(* Check equality of two types in a type abbrev. environment.                *)
(*---------------------------------------------------------------------------*)

fun eqTyEnv E ty1 ty2 = 
  eqTy(ty1,ty2) orelse 
  eqTy(expandTy E ty1,expandTy E ty2);

fun eqExpEnv E e1 e2 =
  eqExp(e1,e2) orelse 
  eqExp(expandTyExp E e1,expandTyExp E e2);

fun CALL_ERR s = TC_ERR ("Procedure call: "^s)

fun checkDims E [] = ()  (* array dims all match *)
  | checkDims E ((c1 as ConstExp _, c2 as ConstExp _)::t) = 
     if is_uint_literal c1 
          andalso is_uint_literal c2
          andalso eqExp(c1,c2) 
      then checkDims E t
      else raise TC_ERR 
             "array dimensions (both constants) disagree or are not uints"
  | checkDims E ((v as VarExp _, e)::t) =
      if not (is_VarExp e) andalso not(is_uint_literal e) 
        then raise TC_ERR 
                       "formal array dimension is not a uint or a variable"
        else (case get_first (fn (a,b) => if eqExpEnv E a v 
                                                   then SOME b else NONE) t
               of NONE => checkDims E t
                | SOME e1 => 
                    if eqExpEnv E e e1 then checkDims E t
                     else raise CALL_ERR (String.concat
                         ["dimension of some array argument does not match ",
                          "that of the corresponding formal parameter"]))
  | checkDims E otherwise = raise CALL_ERR 
                       "array dimensions must be variables or uint constants"

fun matchTys E [] bindings = checkDims E bindings
  | matchTys E ((pat,ob)::t) bindings = 
     case (pat,ob)
      of (ArrayTy (ty1,exps1), ArrayTy (ty2,exps2)) 
         => matchTys E ((ty1,ty2)::t) ((zip exps1 exps2)@bindings)
       | (RecdTy (qid1,fields1), RecdTy (qid2,fields2)) 
         => if qid1 <> qid2
              then raise TC_ERR "Type mismatch in function call args (record types with different qids)."
              else 
               let val (f1_ids, f1_tys) = unzip (sort_on_string_key fields1)
                   val (f2_ids,f2_tys) = unzip (sort_on_string_key fields2)
               in 
                 if f1_ids = f2_ids
                   then matchTys E (zip f1_tys f2_tys @ t) bindings
                   else raise TC_ERR "matchTys at RecdTy"
               end
       | otherwise => 
            if eqTyEnv E pat ob then ()
             else raise TC_ERR "Type mismatch in function call args ."

fun check_pred opstr ty pred = if pred ty then ty else raise TC_ERR opstr;

fun tcTy (env as (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv):tyenv) ty = 
 let in 
    case ty
     of BaseTy _ => ()
      | NamedTy qid => (* enforced by IDE, also checked here *)
         (case total abbrEnv qid 
           of SOME _ => ()
            | NONE => 
          case total recdEnv qid
           of SOME _ => ()
            | NONE => raise TC_ERR 
                       ("type "^Lib.quote(qid_string qid)^" has not been declared")
         )
      | RecdTy (qid,fields) => List.app (tcTy env o snd) fields
      | ArrayTy (basety,dimExps) => 
         let fun expectUnsigned ty = check_pred "ArrayDim" ty is_unsigned
             val _ = tcTy env basety
             val dimTys = List.map (expectUnsigned o tcExp env) dimExps
         in 
           ()
         end
 end
 and 
  tcExp (env as (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv):tyenv) exp :ty = 
   let fun check opstr ty ety = check_pred opstr ty (eqTyEnv (abbrEnv,recdEnv) ety)
   in
    case exp 
     of VarExp id => 
        (case total varEnv id 
          of SOME ty => ty
           | NONE => raise TC_ERR 
                       ("variable "^Lib.quote id^" has not been declared"))
      | ConstExp (IdConst qid) => 
         (case total constEnv qid
          of SOME ([],rngty) => rngty
           | SOME otherwise => raise TC_ERR "nullary constant has function type"
           | NONE => raise TC_ERR 
                       ("constant "^qid_string qid^" has not been declared"))

      | ConstExp (BoolLit _) => BaseTy BoolTy
      | ConstExp (CharLit _) => BaseTy CharTy
      | ConstExp (StringLit _) => BaseTy StringTy
      | ConstExp (RegexLit _) => BaseTy RegexTy
      | ConstExp (IntLit{kind, ...}) => BaseTy (IntTy kind)
      | ConstExp (FloatLit _) => BaseTy FloatTy
      | Unop(Not,e) => check "Not" (tcExp env e) (BaseTy BoolTy)
      | Unop(BitNot,e) => 
          check_pred "BitNot: expected argument to have bounded integer type"
                   (tcExp env e)
                   is_bounded
      | Unop(UMinus,e) => 
         let val estr = String.concat 
                ["Unary Minus: expected argument to be an int ",
                 "(either fixed width or unbounded)"]
         in check_pred estr (tcExp env e) is_signed
         end
      | Unop(ChrOp,e) => 
         let in 
             check "chr" (tcExp env e) 
                         (BaseTy (IntTy (Nat(SOME 8))));
             BaseTy CharTy
         end
      | Unop(OrdOp,e) => 
         let in 
             check "ord" (tcExp env e) (BaseTy CharTy);
             BaseTy (IntTy (Nat(SOME 8)))
         end
      | Unop(Signed,e) => 
         let val ty = tcExp env e
         in check_pred "Signed: expected argument to be an int of some kind"
                       ty is_int;
            flip_sign Signed ty
         end
      | Unop(Unsigned,e) => 
         let val ty = tcExp env e
         in check_pred "Unsigned: expected argument to be an int of some kind"
                    ty is_int;
            flip_sign Unsigned ty
         end
      | Unop(Unbounded,e) => 
         let val ty = tcExp env e
         in check_pred "Unbounded: expected argument to be an int of some kind"
                       ty is_int;
            drop_bound ty
         end
      | Binop(CastWidth,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
            check_pred "CastWidth: expected int in first argument" 
                       ty1 is_int;
            check_pred "CastWidth: expected uint literal in second argument" 
                       ty2 is_unbounded_uint;
            case dest_uintLit e2
             of SOME n => BaseTy(IntTy(Nat (SOME n)))
              | NONE => raise TC_ERR"CastWidth: non-literal width "
          end
      | Binop(And,e1,e2) => 
          let in 
            check "And" (tcExp env e1) (BaseTy BoolTy);
            check "And" (tcExp env e2) (BaseTy BoolTy);
            BaseTy BoolTy
          end
      | Binop(Imp,e1,e2) => 
          let in 
            check "Imp" (tcExp env e1) (BaseTy BoolTy);
            check "Imp" (tcExp env e2) (BaseTy BoolTy);
            BaseTy BoolTy
          end
      | Binop(ArithmeticRShift,e1,e2) => 
           let val ty1 = tcExp env e1
               val ty2 = tcExp env e2
           in 
              check_pred "ArithmeticRShift" ty1 is_bounded;
              check_pred "ArithmeticRShift" ty2 is_unbounded_uint;
              ty1
          end
      | Binop(BitAnd,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "BitAnd" ty1 is_bounded;
              check "BitAnd" ty1 ty2;
              ty2
          end
      | Binop(BitOr,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in
              check_pred "BitOr" ty1 is_bounded;
              check "BitOr" ty1 ty2;
              ty2
          end
      | Binop(BitXOR,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "BitXOR" ty1 is_bounded;
              check "BitXOR" ty1 ty2;
              ty2
          end
      | Binop(Equal,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check "Equal" ty1 ty2; 
              BaseTy BoolTy
          end
      | Binop(Exponent,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "Exponent" ty1 is_int;
              check_pred "Exponent" ty2 is_unbounded_uint;
              ty1
          end
      | Binop(Greater,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "Greater" ty1 is_int;
              check "Greater" ty1 ty2;
              BaseTy BoolTy
          end
      | Binop(GreaterEqual,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "GreaterEqual" ty1 is_int;
              check "GreaterEqual" ty1 ty2;
              BaseTy BoolTy
          end
      | Binop(Divide,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "Divide" ty1 is_int;
              check "Divide" ty1 ty2;
              ty1
          end
      | Binop(Less,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "Less" ty1 is_int;
              check "Less" ty1 ty2;
              BaseTy BoolTy
          end
      | Binop(LessEqual,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "LessEqual" ty1 is_int;
              check "LessEqual" ty1 ty2;
              BaseTy BoolTy
          end
      | Binop(LogicalLShift,e1,e2) => 
           let val ty1 = tcExp env e1
               val ty2 = tcExp env e2
           in 
               check_pred "LogicalLShift" ty1 is_bounded;
               check_pred "LogicalLShift" ty2 is_unbounded_uint;
               ty1
          end
      | Binop(LogicalRShift,e1,e2) => 
           let val ty1 = tcExp env e1
               val ty2 = tcExp env e2
           in 
               check_pred "LogicalRShift" ty1 is_bounded;
               check_pred "LogicalRShift" ty2 is_unbounded_uint;
               ty1
          end
      | Binop(Minus,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "Minus" ty1 is_int;
              check "Minus" ty1 ty2;
              ty2
          end
      | Binop(Modulo,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "Modulo" ty1 is_int;
              check "Modulo" ty1 ty2;
              ty1
          end
      | Binop(Multiply,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "Multiply" ty1 is_int;
              check "Multiply" ty1 ty2;
              ty1
          end
      | Binop(NotEqual,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check "NotEqual" ty1 ty2;
              BaseTy BoolTy
          end
      | Binop(Or,e1,e2) => 
          let in 
             check "Or" (tcExp env e1) (BaseTy BoolTy);
             check "Or" (tcExp env e2) (BaseTy BoolTy);
             BaseTy BoolTy
          end
      | Binop(Plus,e1,e2) => 
          let val ty1 = tcExp env e1
              val ty2 = tcExp env e2
          in 
              check_pred "Plus" ty1 is_int;
              check "Plus" ty1 ty2;
              ty1
          end
      | Binop(RegexMatch,e1,e2) => 
          let in 
            check "RegexMatch" (tcExp env e1) (BaseTy RegexTy);
            check "RegexMatch" (tcExp env e2) (BaseTy StringTy);
            BaseTy BoolTy
          end
      | ArrayExp elist => 
         let fun crunchArrayTy (ArrayTy(b,dims)) = (b,dims)
               | crunchArrayTy ty = (ty,[])
         in 
          case map (tcExp env) elist
            of [] => raise TC_ERR "zero-length array expression"
             | h::t => 
                let val dim = mk_uintLit (length elist)
                    val (b,dims) = crunchArrayTy h
                in 
                  if List.all (fn ty => eqTy(h,ty)) t
                   then ArrayTy(b, dim::dims)
                  else raise TC_ERR 
                      "not all elements in array expression have same type"
                end
         end
      | ArrayIndex (A,indices) =>
          let val itys = map (tcExp env) indices
              val _ = List.map (fn ty => check "ArrayIndex" ty uintTy) itys
          in case expandTy (abbrEnv,recdEnv) (tcExp env A)
              of ArrayTy(bty,dims) => 
                  if Lib.all2 (eqTyEnv (abbrEnv,recdEnv)) itys (map (tcExp env) dims)
                  then bty
                  else raise TC_ERR "Array index: type disagreement on dimensions"
               | otherwise => raise TC_ERR 
                   "Array part of array index expression doesn't have array type"
          end
      | ConstrExp (qid,c,argOpt) =>
          let val argTy = optlist(Option.map (tcExp env) argOpt)
          in 
           case total constrEnv (qid,c) 
            of NONE =>  raise TC_ERR ("unknown type: "^qid_string qid)
             | SOME([],rngty) => 
                if null argTy then rngty else
                raise TC_ERR (String.concat 
                  ["constructor: ",qid_string qid,
                   " expects no arguments, but has been given some"])
             | SOME([domty],rngty) =>
                 if length argTy = 1 then 
                    (if eqTyEnv (abbrEnv,recdEnv) domty (hd argTy) 
                     then rngty 
                     else raise TC_ERR (String.concat 
                      ["constructor: ",qid_string qid^"."^c,
                      " has been given an argument of the wrong type"]))
                else
                raise TC_ERR (String.concat 
                  ["constructor: ",qid_string qid,
                   " has been given the wrong number of arguments"])
             | SOME(domtys,rngty) => raise TC_ERR 
               (String.concat ["constructor: ",qid_string qid,
                               " has more than one argument"])
          end 
      | Fncall (qid,elist) =>  (* Similar to typecheck of Call stmt *)
         let val tys = map (tcExp env) elist
         in case total constEnv qid 
             of NONE => raise TC_ERR ("unknown function: "^qid_string qid)
              | SOME(domtys,rngty) => (* Need to instantiate rngty *)
                  (matchTys (abbrEnv,recdEnv) (zip domtys tys) []; rngty) handle e => 
                  raise TC_ERR ("function: "^qid_string qid^" misapplied")
         end 
      | RecdExp(qid,fields) => 
          let val (ids,exps) = unzip (sort_on_string_key fields)
              val field_tys = List.map (tcExp env) exps
              fun fieldEq (id1,ty1) (id2,ty2) = id1=id2 andalso eqTy(ty1,ty2)
          in case total recdEnv qid 
              of NONE => raise TC_ERR ("unknown record: "^qid_string qid)
               | SOME efields => 
                  let val efields' = sort_on_string_key efields
                  in if Lib.all2 fieldEq (zip ids field_tys) efields'
                     then NamedTy qid  (* all record types have been lifted and named *)
                     else raise TC_ERR ("record expression constructed by: "
                                        ^qid_string qid^" disagrees with record declaration")
                  end
          end
      | RecdProj (e,id) =>
         let fun unknown_field_err tyqid id = 
               TC_ERR (String.concat 
                 ["unknown field: ", Lib.quote id, " in ", qid_string tyqid, " record"])
         in 
         case tcExp env e
          of NamedTy tyqid => 
             (case total recdEnv tyqid
               of NONE => raise TC_ERR (String.concat 
                              ["undeclared record: ", qid_string tyqid])
                | SOME fields => (assoc id fields handle HOL_ERR _ 
                                  => raise unknown_field_err tyqid id)
             )
           | RecdTy (tyqid,fields) =>
               (assoc id fields handle HOL_ERR _ 
                 => raise unknown_field_err tyqid id)
           | otherwise => raise TC_ERR 
                (String.concat ["record projection <exp>.", id, 
                               ": expected <exp> to have a record type"])
         end
      | Quantified(quant,bvars,e) => 
          let fun varEnv' id = (assoc id bvars handle HOL_ERR _ => varEnv id)
              val env' = (abbrEnv,varEnv',constEnv,constrEnv,recdEnv,specEnv)
          in 
             List.app (tcTy env) (map snd bvars);
             check "Quantified"
                   (tcExp env' e)
                   (BaseTy BoolTy)
          end
   end

fun tcStmt E stmt = 
 let val (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv) = E
 in
  case stmt 
   of Skip => ()
    | Check exp => 
       let val ty = tcExp E exp
       in if eqTy(ty,BaseTy BoolTy) 
          then ()
          else raise TC_ERR "Check statement: expected boolean expression"
       end
    | Assign(e1,e2) => 
        let val lty = tcExp E e1
            val rty = tcExp E e2
        in
         if eqTyEnv (abbrEnv,recdEnv) lty rty
          then ()
          else raise TC_ERR 
               "Assignment statement: types of lhs and rhs differ"
        end
    | Call (("","target-return"),[e]) => 
       let val _ = tcExp E e
       in ()
       end
    | Call (("Std","print"),[e]) => 
       let val ty = tcExp E e
       in if eqTy(ty,BaseTy StringTy) 
          then ()
          else raise TC_ERR "print statement: expected string argument"
       end
    | Call (qid,exps) => 
       let val tys = List.map (tcExp E) exps
           val (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv) = E
       in case total constEnv qid
            of NONE =>  raise TC_ERR ("unknown function: "^qid_string qid)
             | SOME(domtys,rngty) => matchTys (abbrEnv,recdEnv) (zip domtys tys) []
                handle e => raise TC_ERR ("function: "^qid_string qid^
                                          " misapplied")
       end 
    | IfThenElse (exp,s1,s2) =>
        let val ty = tcExp E exp
        in if eqTyEnv (abbrEnv,recdEnv) ty (BaseTy BoolTy)
            then (tcStmt E s1; tcStmt E s2)
            else raise TC_ERR "If-Then-Else: test expression is not boolean"
        end
    | Case (exp,rules) => (* patterns have the form : qid.C(id) *)
       let val (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv) = E
           fun pat_id_type (ConstrExp(qid,c,expOpt)) = 
               (case total constrEnv (qid,c)
                 of NONE => raise TC_ERR "Case statement: unknown constructor"
                  | SOME (domtys,rngty) =>
                     (case expOpt
                       of NONE => if null domtys then NONE
                                  else raise TC_ERR (String.concat
                                  ["Case statement: constructor ",c, 
                                   " from type ", qid_string qid,
                                   " expects a non-zero number of arguments"])
                        | SOME (VarExp id) => 
                            if length domtys = 1
                             then SOME(id,hd domtys)
                             else raise TC_ERR (String.concat
                                  ["Case statement: constructor ",c,
                                   " does not expect an argument"])
                        | SOME otherExp => raise TC_ERR (String.concat
                           ["Case statement: expected a constructor applied ",
                            "to a variable"]))
               )
             | pat_id_type otherwise = raise TC_ERR 
                 "Case statement: expected a constructor pattern"
           fun tcRule (p,s) = 
            let val patvarOpt = pat_id_type p
                fun varEnv' id = 
                   (case patvarOpt
                    of NONE => varEnv id
                     | SOME (id1,ty) => if id=id1 then ty else varEnv id
                   )
                val E' = (abbrEnv,varEnv',constEnv,constrEnv,recdEnv,specEnv)
            in tcStmt E' s
            end
       in 
          tcExp E exp;
          List.app tcRule rules
       end
    | Block stmts => List.app (tcStmt E) stmts
    | For ((id,ty),e1,e2,istmt,body) =>
        let val (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv) = E
            fun varEnv' (x) = if x=id then ty else varEnv x
            val E' = (abbrEnv,varEnv',constEnv,constrEnv,recdEnv,specEnv)
        in 
           tcExp E e1;  (* check it's a number? *)
           tcExp E' e2; (* check it's a bool *)
           tcStmt E' istmt;
           tcStmt E' body
        end
    | While (exp,stmt) => 
        let in
          tcExp E exp;
          tcStmt E stmt
        end
 end;

(*---------------------------------------------------------------------------*)
(* Given a type environment, check the declarations of the package. Note     *)
(* this code assumes that the type env. has already been gleaned from the    *)
(* package, e.g. by pkgTyEnv. It also assumes that other checks have been    *)
(* made, such as that definitions are made before use. All this code does is *)
(* check application of operators/functions/procedures to arguments for type *)
(* correctness.                                                              *)
(*---------------------------------------------------------------------------*)

fun mk_dimVar id = (id,defaultNumTy());

fun decl_info decl =
 case decl
  of NumTyDecl _ => "NumTyDecl"
   | TyAbbrevDecl(id,ty) => ("TyAbbrevDecl of : "^id)
   | RecdDecl(id,_)      => ("RecdDecl of : "^id)
   | DatatypeDecl(id,_)  => ("DatatypeDecl of : "^id)
   | GraphDecl(id,_,_)   => ("GraphDecl of : "^id)
   | VarDecl(id,_)       => ("VarDecl of : "^id)
   | ConstDecl(id,_,_)   => ("ConstDecl of : "^id)
   | SizedDataDecl(id,_,_,_)  => ("SizedDataDecl of : "^id)
   | SizedGraphDecl(id,_,_,_) => ("SizedGraphDecl of : "^id)
   | EfnDecl(id,_,_)     => ("EfnDecl of : "^id)
   | FnDecl(id,_,_,_,_)  => ("FnDecl of : "^id)
   | SpecDecl(id,_,_)    => ("SpecDecl of : "^id)
   | CommentDecl _       => "CommentDecl"


fun tcDecl E decl = 
 let val (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv) = E
 in 
 case decl
  of NumTyDecl nkind => ()
   | CommentDecl _ => ()
   | TyAbbrevDecl(id,ty) => tcTy E ty
   | RecdDecl(id,flds) => List.app (tcTy E o snd) flds
   | DatatypeDecl (id,constrs) =>
      let fun check_constr (id,tylist) = List.app (tcTy E) tylist
       in 
           List.app check_constr constrs
       end
   | VarDecl(id,ty) => tcTy E ty
   | ConstDecl(id,ty,exp) =>
       let val _ = tcTy E ty
       in if eqTyEnv (abbrEnv,recdEnv) ty (tcExp E exp) 
           then ()
           else raise TC_ERR (String.concat
                      ["declaration of constant: ",Lib.quote id,
                       " disagreement between specified type and",
                       " type of expression"])
       end
   | SizedDataDecl(id,ty,e1,eopt) =>
       let val _ = tcTy E ty
       in if eqTyEnv (abbrEnv,recdEnv) uintTy (tcExp E e1) 
           then ()
           else raise TC_ERR (String.concat
                      ["declaration of bounded size global variable: ",Lib.quote id,
                       " expected size bound to be of type uint"])
           ;
          (case eopt
            of NONE => ()
             | SOME e2 => 
                 if eqTyEnv (abbrEnv,recdEnv) ty (tcExp E e2) 
                 then ()
                 else raise TC_ERR (String.concat
                      ["declaration of bounded size global variable: ",Lib.quote id,
                       " disagreement between specified type and type of initializer expression"]))
       end
   | SizedGraphDecl(id,ty,e1,e2) =>
       let val _ = tcTy E ty
           val ty1 = tcExp E e1
           val ty2 = tcExp E e2
       in if eqTyEnv (abbrEnv,recdEnv) uintTy ty1 andalso
             eqTyEnv (abbrEnv,recdEnv) uintTy ty2
           then ()
           else raise TC_ERR (String.concat
                      ["declaration of graph global variable: ",Lib.quote id,
                       " expected size bounds to be of type uint"])
       end
   | GraphDecl(id,nty,ety) =>
       let in 
           tcTy E nty
         ; tcTy E ety
       end
   | EfnDecl(id,params,retvalOpt) =>
       let val paramVars = map dest_param params 
           val fnVars = paramVars @ optlist retvalOpt
           val _ = assert no_dups (map fst fnVars)
       in
          List.app (tcTy E) (map snd fnVars)
       end
   | FnDecl(id,params,retvalOpt,locals,stmts) =>
       let val paramVars = map dest_param params 
           val dimVarIds = fnArrayDims(params,retvalOpt)
           val dimVars = map mk_dimVar dimVarIds
           val formalVars = paramVars @ optlist retvalOpt @ dimVars

           (* Add array dimvars to env in order to check param types *)
           val (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv) = E
           fun varEnv1 (id) = (assoc id dimVars handle _ => varEnv id)
           val E1 = (abbrEnv,varEnv1,constEnv,constrEnv,recdEnv,specEnv)
           val _ = List.app (tcTy E1) (map snd formalVars)

           fun checkLocals () = ()
           (* let fun eqVdec (s1,ty1) (s2,ty2) = 
                 s1=s2 andalso eqTyEnv (abbrEnv,recdEnv) ty1 ty2
             in
               case Lib.op_intersect eqVdec locals formalVars
                of [] => ()
                 | vlist => raise TC_ERR (String.concat 
                               ["local variables ",
                                String.concatWith "," (map fst vlist),
                                " mask formal parameter(s)"])
             end
           *)
           val _ = checkLocals()
           val localDimVarIds = U (map (tyVars o snd) locals)
           val localDimVars = map mk_dimVar localDimVarIds
           val fnScope = locals@localDimVars@formalVars
           fun varEnv' (id) = (assoc id fnScope handle _ => varEnv id)
           val E' = (abbrEnv,varEnv',constEnv,constrEnv,recdEnv,specEnv)
       in 
          List.app (tcStmt E') stmts
       end
   | SpecDecl(id,vdecs,stmts) => 
       let val vdecDimVarIds = U (map (tyVars o snd) vdecs)
           val vdecDimVars = map mk_dimVar vdecDimVarIds
           val specVars = vdecDimVars@vdecs
           val (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv) = E
           fun varEnv' (id) = (C assoc specVars id handle _ => varEnv id)
           val E' = (abbrEnv,varEnv',constEnv,constrEnv,recdEnv,specEnv)
           val _ = List.app (tcTy E') (map snd vdecs)
       in 
          List.app (tcStmt E') stmts
       end
 end
 handle e => raise wrap_exn "tcDecl" (decl_info decl) e;

(*---------------------------------------------------------------------------*)
(* Create type environment for the package.                                  *)
(*---------------------------------------------------------------------------*)

fun tydecls pkgName decls = 
 mapfilter 
   (fn (RecdDecl (id,_))    => ((pkgName,id),NamedTy(pkgName,id))
     | (DatatypeDecl(id,_)) => ((pkgName,id),NamedTy(pkgName,id))
     | (GraphDecl (id,_,_)) => ((pkgName,id),NamedTy(pkgName,id))
     | (TyAbbrevDecl (id,ty)) => ((pkgName,id),ty)
     | other => raise ERR "" "")
 decls;

(* fun var_decls decls = mapfilter (fn VarDecl x => x) decls; *)
fun constr_decls decls = mapfilter (fn (DatatypeDecl x) => x) decls;
fun recd_decls decls = mapfilter (fn (RecdDecl x) => x) decls;

fun const_decls decls =
 let fun dest decl = 
      case decl 
       of EfnDecl(id,params,retValOpt) =>
           (id,(map (snd o dest_param) params,
                case retValOpt
                 of NONE => unitTy
                  | SOME(_,ty) => ty))
        | FnDecl(id,params,retValOpt,_,_) =>
           (id,(map (snd o dest_param) params,
                case retValOpt
                 of NONE => unitTy
                  | SOME(_,ty) => ty))
        | ConstDecl(id,ty,e) => (id,([],ty))
        | otherwise => raise ERR "const_decls" ""
 in 
   mapfilter dest decls
 end

val specdecls = mapfilter (fn (SpecDecl (id,_,_)) => id);

val vardecls = (* assumes all globals are declared in current package *)
  let fun dest_var (VarDecl (id,ty)) = (id,ty)
        | dest_var (SizedDataDecl(id,ty,e1,e2)) = (id,ty)
        | dest_var (SizedGraphDecl(id,ty,e1,e2)) = (id,ty)
        | dest_var other = raise ERR "vardecls" "dest_var"
  in
    mapfilter dest_var
  end;

fun tyEnvs (pkgName,decls) = 
 let val alist = tydecls pkgName decls
     val alistFn = C assoc alist
     fun failEnv qid = raise ERR "tyEnvs" "failEnv"
     val abbrEnv = map (I##expandTy (alistFn,failEnv)) alist (* expand type abbreviations *)
     val _ = assert no_dups (map fst abbrEnv)
     val recds = recd_decls decls
     val recdEnv = map (fn (id,fieldtys) => ((pkgName,id),fieldtys)) recds
     val consts = const_decls decls
     val constEnv = map (fn (id,x) => ((pkgName,id),x)) consts
     val constrs = constr_decls decls
     fun constr_type qid (id,tys) = ((qid,id),(tys,NamedTy(qid)))
     val constrEnv = 
        flatten (map (fn (id,constrs) =>
                       let val qid = (pkgName,id)
                       in map (constr_type qid) constrs
                       end) 
                     constrs)
     val varEnv = vardecls decls
     val specIds = specdecls decls
     val _ = assert no_dups specIds
     val specEnv = map (fn id => (pkgName,id)) specIds
 in 
    (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv)
 end

fun typecheck (pkg as (pkgName,decls)) = 
 let val (abbrEnv,varEnv,constEnv,constrEnv,recdEnv,specEnv) = tyEnvs pkg
     val E : tyenv = 
        (C assoc abbrEnv,
         C assoc varEnv,
         C assoc constEnv,
         C assoc constrEnv,
         C assoc recdEnv,
         specEnv)
 in
   List.app (tcDecl E) decls;
   pkg
 end
 handle e => raise wrap_exn "Passes" "typecheck" e;


(*

val ipkg = parsePkg "test/prio.sexp";

val pkg = Passes.apply_type_abbrevs ipkg;
val (pkgName,decls) = pkg;
fun fdecl_assoc s ((dec as FnDecl(id,params,ret,locals,stmts))::t) = 
    if s = id then dec else fdecl_assoc s t
  | fdecl_assoc s (other::t) = fdecl_assoc s t;

val decl = fdecl_assoc "extract" decls;  (* el 16 decls; *)
val E = pkgTyEnv pkg
tcDecl pkgName E decl;
List.app (tcDecl pkgName E) decls;
val [decl1,decl2,decl3,decl4,decl5,decl6,decl7,decl8,decl9,decl10,decl11] = decls;

val [c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, 
     c11, c12, c13, c14, c15, c16, c17, c18, c19, c20] = fdecl_cohorts;
*)

(*---------------------------------------------------------------------------*)
(* Compute type declaration cliques                                          *)
(*---------------------------------------------------------------------------*)

fun tydecl_cliques pkgName tydecls = 
 let fun graph_of (RecdDecl(id,flds)) = 
            ((pkgName,id),namedTypes(map snd flds))
       | graph_of (DatatypeDecl(id,constrs)) = 
            ((pkgName,id),namedTypes(List.concat(map snd constrs)))
       | graph_of otherwise = raise ERR "tydecl_cliques" 
                              "expected a record or datatype decl"
     val graph = Lib.mapfilter graph_of tydecls
     val tcgraph = MiscLib.TC graph
     val cliques = MiscLib.cliques_of tcgraph
     val tyclique_qids = map (map fst) cliques
     fun is_decl_of (_,tyName) (RecdDecl(id,_)) = tyName=id
       | is_decl_of (_,tyName) (DatatypeDecl(id,_)) = tyName=id
       | is_decl_of other wise = false
     fun decl_of qid = Lib.first (is_decl_of qid) tydecls
     val decl_cliques = map (map decl_of) tyclique_qids
 in 
    decl_cliques
 end;


(*---------------------------------------------------------------------------*)
(* Prettyprinters                                                            *)
(*---------------------------------------------------------------------------*)

fun base_ty_name (BaseTy BoolTy)   = "bool"
  | base_ty_name (BaseTy CharTy)   = "char"
  | base_ty_name (BaseTy StringTy) = "string"
  | base_ty_name (BaseTy RegexTy)  = "regex"
  | base_ty_name (BaseTy FloatTy)  = "float"
  | base_ty_name (BaseTy (IntTy (Nat NONE))) = "uint"
  | base_ty_name (BaseTy (IntTy (Int NONE))) = "sint"
  | base_ty_name (BaseTy (IntTy (Nat(SOME w)))) = "uint"^Int.toString w
  | base_ty_name (BaseTy (IntTy (Int(SOME w)))) = "sint"^Int.toString w
  | base_ty_name (NamedTy args) = pp_qid args
  | base_ty_name other = raise ERR "base_ty_name" "not a base type"

fun pp_ty depth ty = 
 let open PolyML
 in if depth = 0 then PrettyString "<ty>"
   else
   case ty
    of BaseTy _ => PrettyString (base_ty_name ty)
     | NamedTy _ => PrettyString (base_ty_name ty)
     | RecdTy(qid,fields) =>
         PrettyBlock(2,true,[],
           [PrettyString (pp_qid qid), PrettyBreak(0,0),
            PrettyString "[", 
            pp_comma_list (pp_ty_field (depth-1)) fields, 
            PrettyString "]"])
     | ArrayTy(eltype,dims) =>
         PrettyBlock(2,true,[],
            [pp_ty (depth-1) eltype, PrettyBreak (1,0),
             PrettyString "[", pp_comma_list (pp_exp (depth-1)) dims, 
             PrettyString "]"])
 end
 and pp_exp depth exp = 
  let open PolyML
  in if depth = 0 then PrettyString "<exp>"
    else
    case exp 
     of VarExp id => PrettyString id
      | ConstExp (IdConst qid) => PrettyString (pp_qid qid)
      | ConstExp (BoolLit b) => PrettyString (Bool.toString b)
      | ConstExp (CharLit c) => PrettyString ("'#"^Char.toString c^"'")
      | ConstExp (StringLit s) => PrettyString (Lib.quote(String.toString s))
      | ConstExp (IntLit{kind, value}) => 
          let val istr = Int.toString value
          in case kind
              of Nat NONE => PrettyString (istr^"uint")
               | Nat (SOME w) => PrettyString (istr^"uint"^Int.toString w)
               | Int NONE => PrettyString (istr^"sint")
               | Int (SOME w) => PrettyString (istr^"sint"^Int.toString w)
          end
      | ConstExp (FloatLit r) => PrettyString (Real.toString r)
      | ConstExp (RegexLit r) => PrettyString ("`"^r^"`")
      | Unop(Not,e) => PrettyBlock(2,true,[],
           [PrettyString"not",PrettyBreak(0,1),
            PrettyString"(",pp_exp (depth-1) e,PrettyString")"])
      | Unop(BitNot,e) => PrettyBlock(2,true,[],
           [PrettyString"~",
            PrettyString"(",pp_exp (depth-1) e,PrettyString")"])
      | Unop(UMinus,e) => PrettyBlock(2,true,[],
           [PrettyString"-",
            PrettyString"(",pp_exp (depth-1) e,PrettyString")"])
      | Unop(ChrOp,e) => PrettyBlock(2,true,[],
           [PrettyString"chr",
            PrettyString"(",pp_exp (depth-1) e,PrettyString")"])
      | Unop(OrdOp,e) => PrettyBlock(2,true,[],
           [PrettyString"ord",
            PrettyString"(",pp_exp (depth-1) e,PrettyString")"])
      | Unop(Signed,e) => PrettyBlock(2,true,[],
           [PrettyString"signed",
            PrettyString"(",pp_exp (depth-1) e,PrettyString")"])
      | Unop(Unsigned,e) => PrettyBlock(2,true,[],
           [PrettyString"unsigned",
            PrettyString"(",pp_exp (depth-1) e,PrettyString")"])
      | Unop(Unbounded,e) => PrettyBlock(2,true,[],
           [PrettyString"unbounded",
            PrettyString"(",pp_exp (depth-1) e,PrettyString")"])
      | Binop(And,e1,e2) => pp_binop depth ("and",e1,e2)
      | Binop(Imp,e1,e2) => pp_binop depth ("==>",e1,e2)
      | Binop(ArithmeticRShift,e1,e2) => pp_binop depth ("a>>",e1,e2)
      | Binop(BitAnd,e1,e2) => pp_binop depth ("&",e1,e2)
      | Binop(BitOr,e1,e2) => pp_binop depth ("|",e1,e2)
      | Binop(BitXOR,e1,e2) => pp_binop depth ("^",e1,e2)
      | Binop(Equal,e1,e2) => pp_binop depth ("=",e1,e2)
      | Binop(Exponent,e1,e2) => pp_binop depth ("exp",e1,e2)
      | Binop(Greater,e1,e2) => pp_binop depth (">",e1,e2)
      | Binop(GreaterEqual,e1,e2) => pp_binop depth (">=",e1,e2)
      | Binop(Divide,e1,e2) => pp_binop depth ("/",e1,e2)
      | Binop(Less,e1,e2) => pp_binop depth ("<",e1,e2)
      | Binop(LessEqual,e1,e2) => pp_binop depth ("<=",e1,e2)
      | Binop(LogicalLShift,e1,e2) => pp_binop depth ("<<",e1,e2)
      | Binop(LogicalRShift,e1,e2) => pp_binop depth ("l>>",e1,e2)
      | Binop(Minus,e1,e2) => pp_binop depth ("-",e1,e2)
      | Binop(Modulo,e1,e2) => pp_binop depth ("%",e1,e2)
      | Binop(Multiply,e1,e2) => pp_binop depth ("*",e1,e2)
      | Binop(NotEqual,e1,e2) => pp_binop depth ("!=",e1,e2)
      | Binop(Or,e1,e2) => pp_binop depth ("or",e1,e2)
      | Binop(Plus,e1,e2) => pp_binop depth ("+",e1,e2)
      | Binop(CastWidth,e1,e2) => pp_binop depth ("width",e1,e2)
      | Binop(RegexMatch,e1,e2) => pp_exp depth (Fncall(("","match"),[e1,e2]))
      | ArrayExp elist => PrettyBlock(0,true,[],
           [PrettyString"[",
            pp_list_with_style false Comma [emptyBreak] (pp_exp (depth-1)) elist,
            PrettyString"]"])
      | ArrayIndex(A,dims) => PrettyBlock(0,false,[],
           [pp_exp (depth-1) A, PrettyString"[",
            gen_pp_list Comma [emptyBreak] (pp_exp (depth-1)) dims,
            PrettyString"]"])
      | ConstrExp(qid, constr,argOpt) => 
         PrettyBlock(2,true,[],
           [PrettyString(pp_qid qid^"'"^constr), 
            PrettyBlock(0,false,[],
             case argOpt of NONE => []
               | SOME vexp => [PrettyString"(", pp_exp (depth-1) vexp,
                               PrettyString")"])])
      | Fncall (qid,args) => PrettyBlock(2,true,[],
           [PrettyString(pp_qid qid), PrettyString"(",
            PrettyBlock(0,false,[],
               [gen_pp_list Comma [emptyBreak] (pp_exp (depth-1)) args]),
            PrettyString")"])
      | Quantified (quant,bvars,body) => 
          PrettyBlock(2,true,[],
           [PrettyString(case quant of Forall => "forall " | Exists => "exists "), 
            PrettyBlock(0,false,[],
               [gen_pp_list Space [] (pp_ty_field (depth-1)) bvars]),
                pp_exp (depth-1) body])
      | RecdExp (qid,fields) => PrettyBlock(2,true,[],
           [PrettyString(pp_qid qid), PrettyString("["), 
            PrettyBlock(0,false,[], 
                        [pp_comma_list (pp_exp_field (depth-1)) fields]),
            PrettyString"]"])
      | RecdProj(recd,field) => PrettyBlock(0,false,[],
           [pp_exp (depth-1) recd,PrettyString".",PrettyString field])
  end
and pp_binop d (str,e1,e2) = 
    let open PolyML
    in PrettyBlock(2,true,[],
        [PrettyString"(",pp_exp (d-1) e1,
         Space,PrettyString str, Space,
         pp_exp (d-1) e2,PrettyString")"])
    end
and pp_exp_field d (id,exp) = 
 let open PolyML
 in PrettyBlock(0,true,[],
     [PrettyString id, PrettyString":",pp_exp (d-1) exp])
 end
and pp_ty_field d (id,ty) = 
 let open PolyML
 in PrettyBlock(0,true,[],
     [PrettyString id, PrettyString":",pp_ty (d-1) ty])
 end
;

fun pp_vdec_semi d (id,ty) = 
 let open PolyML
 in PrettyBlock(0,true,[],
     [PrettyString id, PrettyString":",pp_ty (d-1) ty,PrettyString";"])
 end;
 

fun pp_param d param = 
 let open PolyML
     fun ppp mpp d (id,ty) = 
       PrettyBlock(2,false,[],
         [PrettyString id, Space, PrettyString":",
          Space, mpp, Space, pp_ty (d-1) ty])
 in case param
     of In vdec => ppp (PrettyString"in") d vdec
      | Out vdec => ppp (PrettyString"out") d vdec
      | InOut vdec => ppp (PrettyString"inout") d vdec
 end;
   
fun pp_stmt depth stmt = 
 let open PolyML
 in if depth = 0 then PrettyString "<stmt>"
  else 
   case stmt 
    of Skip => PrettyString "Skip;"
     | Check e => 
         PrettyBlock(2,true,[],
            [PrettyString "check",Space, 
             pp_exp (depth-1) e, PrettyString";"])
     | Assign(e1,e2) => 
         PrettyBlock(2,true,[],
         [pp_exp (depth-1) e1, PrettyString " :=",
          Space, pp_exp (depth-1) e2, PrettyString";"])
     | Call((pkgName,fnName),elist) =>
          PrettyBlock(2,true,[],
            [PrettyString (pkgName^"."^fnName),PrettyBreak(0,0), 
             PrettyString"(", pp_comma_list (pp_exp (depth-1)) elist, 
             PrettyString");"])
     | IfThenElse(e,s1,s2) => 
          PrettyBlock(2,true,[],
            [PrettyString"if ", pp_exp (depth-1) e, Space,
             PrettyString"then ", pp_stmt (depth-1) s1,Space,
             PrettyString"else ", pp_stmt (depth-1) s2])
     | Case(e,rules) => 
        let fun pp_rule d (p,s) = 
         PrettyBlock(2,true,[],
          [pp_exp (d-1) p, PrettyString" =>", Space,
           pp_stmt (d-1) s])
        in
          PrettyBlock(2,true,[],
           [PrettyString "match ", pp_exp (depth-1) e, 
            PrettyString " {", Line_Break,
            gen_pp_list emptyString [Space] (pp_rule (depth-1)) rules,
            Line_Break, PrettyString "}"])
        end
     | Block stmts => 
           PrettyBlock(0,true,[],
            [PrettyString"{", Line_Break,
             PrettyBlock(2,true,[],
              [gen_pp_list emptyString [Space] (pp_stmt (depth-1)) stmts]),
             PrettyBreak(0,0),PrettyString "}"])
     | For((id,ty),e1,e2,istmt,body) => 
          PrettyBlock(4,true,[],
            [PrettyString"for", PrettyString"(", 
             PrettyBlock(2,false,[],
               [PrettyString id,Space,PrettyString":",
                Space, pp_ty (depth-1) ty, Space,
                PrettyString"=", Space, pp_exp (depth-1) e1]),
              PrettyString";", Space,
             pp_exp (depth-1) e2,
             PrettyString";", Space,
             pp_stmt (depth-1) istmt,
             PrettyString")", PrettyBreak(9999,0),
             pp_stmt (depth-1) body])
     | While(e,stmt) => 
          PrettyBlock(2,false,[],
            [PrettyString"while ", pp_exp (depth-1) e, Line_Break,
             PrettyString"do ", pp_stmt (depth-1) stmt])

 end;


fun pp_decl depth decl = 
 let open PolyML
 in if depth = 0 then PrettyString "<decl>"
  else 
   case decl
    of NumTyDecl nkind 
       => PrettyBlock(0,true,[],
           [PrettyString "numeral type = ",
            pp_ty (depth-1) (BaseTy(IntTy nkind)), PrettyString";"])
     | TyAbbrevDecl(id,ty) 
       => PrettyBlock(2,true,[],
           [PrettyString "type ", PrettyString id, PrettyString " = ",
            pp_ty (depth-1) ty, PrettyString";"])
     | RecdDecl(id,flds) 
       => let in
          PrettyBlock(2,true,[],
             [PrettyString "type ", PrettyString id, PrettyString " =", Space, 
              PrettyString "[", pp_comma_list (pp_ty_field (depth-1)) flds, 
              PrettyString"];"])
          end
     | DatatypeDecl(id,constrs) 
       => let fun pp_constr d (id,tys) = 
               PrettyBlock(0,true,[],
                  [PrettyString id, 
                   if not(null tys) then PrettyString":" else PrettyString"",
                   pp_comma_list (pp_ty (d-1)) tys])
              fun pp_constr_list d list = 
               let fun iter [] = []
                 | iter [x] = [pp_constr (d-1) x]
                 | iter (h::t) = pp_constr (d-1) h :: 
                                 PrettyString" |" :: Space::iter t
               in
                 PrettyBlock(0,true,[],iter list)
                end
          in PrettyBlock(2,true,[],
             [PrettyString "datatype ", PrettyString id, PrettyString " =", Space, 
              pp_constr_list (depth-1) constrs, 
              PrettyString";"])
          end
     | GraphDecl(id,nty,ety) 
       => PrettyBlock(2,true,[],
             [PrettyString ("graphtype "^id), Space, PrettyString"= (",
              PrettyString "nodeLabel = ", pp_ty (depth-1) nty, Comma,Space,
              PrettyString "edgeLabel = ", pp_ty (depth-1) ety,PrettyString");"])
     | VarDecl vdec
       => PrettyBlock(0,true,[],
             [PrettyString "var", Space, pp_ty_field (depth-1) vdec,
              PrettyString";"])
     | ConstDecl(id,ty,exp) 
       => PrettyBlock(2,true,[],
             [PrettyString "const ", 
              pp_ty_field (depth-1) (id,ty),
              PrettyString" = ", Space, 
              pp_exp (depth-1) exp, PrettyString";"])
     | SizedDataDecl(id,ty,e1,NONE) 
       => PrettyBlock(2,true,[],
             [PrettyString "sized ", 
              pp_ty_field (depth-1) (id,ty),
              PrettyString" (", pp_exp (depth-1) e1, PrettyString");"])
     | SizedDataDecl(id,ty,e1,SOME e2) 
       => PrettyBlock(2,true,[],
             [PrettyString "sized ", 
              pp_ty_field (depth-1) (id,ty),
              PrettyString" (", pp_exp (depth-1) e1, PrettyString") :=",
              Space, pp_exp (depth-1) e2, PrettyString ";"])
     | SizedGraphDecl(id,ty,e1,e2) 
       => PrettyBlock(2,true,[],
             [PrettyString "sized ", 
              pp_ty_field (depth-1) (id,ty),
              PrettyString" (", pp_exp (depth-1) e1, Comma,
                                pp_exp (depth-1) e2, PrettyString");"])
     | EfnDecl(id,params,retvalOpt) 
       => PrettyBlock(2,true,[],
             [PrettyString "imported function", Space, 
              PrettyString id,Space, 
              PrettyString"(", pp_comma_list (pp_param (depth-1)) params,
              PrettyString")", 
              PrettyBlock(2,true,[],
                case retvalOpt 
                 of NONE => []
                  | SOME vdec => [PrettyString" returns ",
                                  pp_ty_field (depth-1) vdec]),
              PrettyString";"])
     | FnDecl(id,params,retvalOpt,locals,stmts) 
       => let fun pp_params() = PrettyBlock (2,true,[],
                   [PrettyString id,Space, 
                    PrettyString"(", 
                    PrettyBlock(0,false,[],
                         [pp_comma_list (pp_param(depth-1)) params]),
                    PrettyString")", 
                    PrettyBlock (2,true,[],
                         case retvalOpt 
                          of NONE => []
                           | SOME vdec => [PrettyString" returns ",
                                           pp_ty_field (depth-1) vdec])])
             fun pp_body() = PrettyBlock(0,true,[],
                    iter_pp emptyString  [Line_Break] (pp_stmt (depth-1)) stmts)
              fun pp_top_stmt [] = pp_body()
                | pp_top_stmt locals =
                    PrettyBlock(0,false,[],
                      [PrettyString"var",
                       PrettyBreak (1,0),
                       PrettyBlock(0,true,[],
                         iter_pp emptyString [Line_Break] (pp_vdec_semi (depth-1)) locals),
                       Line_Break,
                       PrettyString"in",
                       Line_Break,
                       pp_body()])
          in
           PrettyBlock(2,false,[],
             [PrettyString "function ", pp_params(), 
              PrettyString" {", Line_Break,
              PrettyString " ",
              pp_top_stmt locals,
              Line_Break, 
              PrettyString"}"])
          end
     | SpecDecl(id,vdecs,stmts) 
        => PrettyBlock(2,true,[],
             [PrettyString "spec ", PrettyString id,
              PrettyString " = {", PrettyBreak(999,2),
              PrettyBlock(4,true,[],
                case vdecs
                 of [] => []
                  | otherwise => 
                      [PrettyString"var", Line_Break,
                       gen_pp_list emptyString [Line_Break] (pp_vdec_semi (depth-1)) vdecs,
                       Line_Break, PrettyString"in ",Line_Break]),
                         gen_pp_list emptyString [Line_Break] (pp_stmt (depth-1)) stmts,
              Line_Break, PrettyString"}"])
     | CommentDecl lines
        => PrettyBlock(0,true,[],
           [PrettyString "/*-----------------------------------------------------------------", 
            Line_Break, PrettyString"-- ",
            gen_pp_list Line_Break [PrettyString"--"] PrettyString lines,
            Line_Break,
            PrettyString "-----------------------------------------------------------------*/"])

 end

val _ = PolyML.addPrettyPrinter (fn i => fn () => fn ty => pp_ty i ty);
val _ = PolyML.addPrettyPrinter (fn i => fn () => fn e => pp_exp i e);
val _ = PolyML.addPrettyPrinter (fn i => fn () => fn stmt => pp_stmt i stmt);
val _ = PolyML.addPrettyPrinter (fn i => fn () => fn decl => pp_decl i decl);

end (* AST *)


