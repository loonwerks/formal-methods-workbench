(*---------------------------------------------------------------------------*)
(* Translation from AADL (in Json syntax) to an AST form, and then to HOL    *)
(*---------------------------------------------------------------------------*)

structure AADL :> AADL =
struct

open Lib Feedback HolKernel boolLib bossLib MiscLib AST Json;

local open 
   stringLib stringSimps wordsLib integer_wordLib fcpLib 
   regexpLib regexpSyntax
in end;

 type ty = AST.ty;
 type exp = AST.exp;
		 
  type decls = 
  (* pkgName *)  string * 
  (* enums *)    (string * string list) list *
  (* recds *)    (string * (string * ty) list) list *
  (* fns *)      (string * (string * ty) list * exp) list *
  (* filters *)  (string * (string * string * string) list * (string * exp)) list
  ;

val ERR = Feedback.mk_HOL_ERR "AADL";

(*---------------------------------------------------------------------------*)
(* Json to AST (types, expressions, function definitions, and filters)       *)
(*---------------------------------------------------------------------------*)

fun dest_tyqid did =
 case Lib.front_last (String.tokens (equal #".") did)
  of ([],recdName) => NamedTy(current_theory(),recdName)
   | ([recdName],"Impl") => NamedTy(current_theory(),recdName)
   | otherwise => raise ERR "dest_tyqid"
            ("unexpected record type name qualification: "^Lib.quote did)

fun get_tyinfo tyinfo =
 case tyinfo
  of [("kind", String "recordType"),
      ("recordType",
       AList [("kind", String "typeId"), ("name", String dotid)])]
     => dest_tyqid dotid
  | [("kind", String "recordType"),    (* CHECK: superseded by previous? *)
     ("recordType",
          AList [("kind", String "nestedDotId"), ("name", String dotid)])]
     => dest_tyqid dotid
  | [("kind", String "typeId"), ("name", String tyname)]
    => dest_tyqid tyname
  | [("kind", String "nestedDotId"), ("name", String tyname)]  (* CHECK: supersed by previous? *)
    => dest_tyqid tyname
  | [("kind", String "primType"), ("primType", String "bool")]
    => BaseTy BoolTy
  | otherwise => raise ERR "get_tyinfo"
                  "not a record type or bool type (only cases handled)";

fun dest_ty ty =
 case ty
  of AList[("kind", String "type"), ("type", AList tyinfo)] => get_tyinfo tyinfo
  | otherwise => raise ERR "dest_ty" "expected a type expression"
;

fun mk_dotid did =
  case Lib.front_last (String.tokens (equal #".") did)
  of ([],v) => VarExp v
   | (v::t,proj) => rev_itlist (C (curry RecdProj)) (t @[proj]) (VarExp v)
;

fun mk_unop (opr,e) =
 let val oexp =
       case opr
        of "not" => Not
         | "-"   => UMinus
         | other => raise ERR "mk_unop"
               ("unknown unary operator "^Lib.quote other)
 in Unop(oexp,e)
 end

fun mk_binop (opr,e1,e2) =
 let val oexp =
       case opr
        of "+"   => Plus
         | "-"   => Minus
         | "*"   => Multiply
         | "<"   => Less
         | "<="  => LessEqual
         | ">"   => Greater
         | ">="  => GreaterEqual
	 | "=>"  => Imp
         | "="   => Equal
         | "and" => And
         | "or"  => Or
         | other => raise ERR "mk_binop"
               ("unknown binary operator "^Lib.quote other)
 in Binop(oexp,e1,e2)
 end

fun mk_intLit i = ConstExp(IntLit{value=i, kind=AST.Int NONE});

fun mk_int str =
  case Int.fromString str
   of SOME i => mk_intLit i
    | NONE =>
       raise ERR "mk_int" ("expected an int constant, but got: "^Lib.quote str)

fun mk_bool_const str =
 case Bool.fromString str
  of SOME b => ConstExp (BoolLit b)
   | NONE => raise ERR "mk_bool_const" "expected true or false";

fun mk_fncall(fname, args) = 
  case Lib.front_last (String.tokens (equal #".") fname)
   of (_,fname') => Fncall((current_theory(),fname'),args);

fun mk_nullary_constr(cname, ty) =
 case ty
  of NamedTy tyqid => ConstrExp(tyqid,cname,NONE)
   |  otherwise => raise ERR "mk_nullary_constr"
       ("unable to determine type of constructor "^Lib.quote cname)

fun dest_exp e =
 case e
  of AList [("kind", String "binary"),
            ("left", le), ("op", String opr), ("right",re)]
       => mk_binop (opr,dest_exp le, dest_exp re)
   | AList [("kind", String "unary"), ("operand", e), ("op", String opr)]
       => mk_unop (opr,dest_exp e)
   | AList [("kind", String "nestedDotId"), ("name", String dotid)]
       => mk_dotid dotid
   |  AList [("kind", String "boolLit"), ("value", String bstr)]
       => mk_bool_const bstr
   | AList [("kind", String "intLit"), ("value", String intstr)]
       => mk_int intstr
   |  AList [("kind", String "fnCall"),
             ("function",
               AList
                [("kind", String "nestedDotId"),
                 ("name", String fname)]),
             ("args", List args)]
       => mk_fncall(fname,map dest_exp args)
   | AList [("kind", String "aadlEnum"),
            ("type", AList tyinfo), ("value", String constrname)]
      => mk_nullary_constr (constrname,get_tyinfo tyinfo)
   | other => raise ERR "dest_exp" "unexpected expression form";

fun dest_param param =
 case param
  of AList [("kind", String "param"),
            ("name", String pname), ("type", ty)] => (pname,dest_ty ty)
   | otherwise => raise ERR "dest_param" "unexpected input";

fun build_fndef (fname,params,body) =
 let val params' = map dest_param params
     val body' = dest_exp body
 in (fname,params',body')
 end;

fun mangle body =
 case body 
  of AList [("kind", _), ("left",_), ("op",_),("right",e)] => e
   | otherwise => raise ERR "mengle" "unexpected input format"
			
fun mk_fun_def json =
 case json
  of AList [("kind", String "funDef"),
            ("name", String fname),
            ("params", List params),
            ("expr", body)] => 
     build_fndef (fname,params,
      if fname = "WELL_FORMED_MESSAGE" then mangle body else body)
  |  otherwise => raise ERR "mk_fun_def" "unexpected input";

fun get_fndefs elt2 =
 case elt2
  of ("agree",
      List [AList [("statements", List fundefs)]]) => mapfilter mk_fun_def fundefs
   |  otherwise => raise ERR "get_fndefs" "unexpected input";

(*---------------------------------------------------------------------------*)
(* ad hoc                                                                    *)
(*---------------------------------------------------------------------------*)

val _ = defaultNumKind := AST.Int NONE;

fun fldty pkgName str =
 case str
  of "Base_Types::Integer" => BaseTy(IntTy (!defaultNumKind))
   | "Base_Types::Boolean" => BaseTy(BoolTy)
   | other =>
      let val chunks = String.tokens (fn ch => (ch = #":" orelse ch = #".")) str
          val chunks' = if last chunks = "Impl" then butlast chunks else chunks
          val ty_pkgName = 
              hd chunks' handle _ => raise ERR "fldty" "malformed type"
	  val chunks'' = tl chunks'
(*          val chunks'' = if hd chunks' = pkgName then tl chunks' else chunks' *)
      in case chunks''
          of [name] => NamedTy("",name)
           | other => raise ERR "fldty" ("unknown field type"^Lib.quote str)
      end;

fun dest_subcomp pkgName sc =
 case sc
  of AList [("name", String fldname), ("category", String "data"),
            ("classifier", String tystr)]
      => (fldname,fldty pkgName tystr)
   | otherwise => raise ERR "dest_subcomp" "expected a record field";

fun is_recd names decl = 
 case decl
  of AList ((name, String name_impl)::("subcomponents", List subs)::_)
      => name^".Impl" = name_impl andalso mem name names
  |  otherwise => false;

fun recd_decl pkgName names decl =
 case decl
  of AList ((name, String name_impl)::("subcomponents", List subs)::_)
      => if name^".Impl" = name_impl andalso mem name names
          then (name, map (dest_subcomp pkgName) subs)
          else raise ERR "recd_decl" "expected a record implementation"
  | other => raise ERR "recd_decl" "expected a record declaration";

fun dropString (String s) = s
  | dropString otherwise = raise ERR "dropString" "not a json String application";

fun enum_decl decl =
 case decl
  of AList [("name", String ename), ("type", String "data"), ("features", _),
            ("properties",
             List [AList [("name", String "Data_Representation"),
                          ("value", String "Enum")],
                   AList [("name", String "Enumerators"),
                          ("value", List names)]]),
            ("agree",_)]
      => (ename, map dropString names)
  | other => raise ERR "enum_decl" "expected an enum declaration";

fun mk_enum_decl (name,ids) =
    DatatypeDecl(name, map (fn c => (c,[])) ids);

fun data_decl_name
  (AList(("name", String dname)::("type", String "data"):: _)) = dname

fun get_tydecls pkgName elt3 =
 case elt3
  of ("components", List comps) =>
    let val data_tynames = mapfilter data_decl_name comps;
        val recd_decls = mapfilter (recd_decl pkgName data_tynames) comps
        val enum_decls = mapfilter enum_decl comps
    in (map RecdDecl recd_decls,
        map mk_enum_decl enum_decls)
    end;

fun get_decls jpkg =
 case jpkg
  of AList [("package", String pkgName),
            elt2 as ("agree", _),
            elt3 as ("components",List comps)]
      => let val fndecls = get_fndefs elt2
             val (recd_decls, enum_decls) = get_tydecls pkgName elt3
         in (enum_decls,recd_decls,fndecls)
         end
  | otherwise => raise ERR "get_decls" "unexpected format";

(*
val AList [("package", String "SW"),
           elt2 as ("agree", _),
           elt3 as ("components",List comps)] = jpkg;

val [c1, c2, c3, c4, c5, c6, c7, c8, c9,
     c10, c11, c12, c13, c14, c15, c16, c17, c18] = comps
*)


fun fldty pkgName tystr =
 case tystr
  of "Base_Types::Integer" => BaseTy(IntTy (!defaultNumKind))
   | "Base_Types::Boolean" => BaseTy(BoolTy)
   | other =>
      let val chunks = String.tokens (fn ch => (ch = #":" orelse ch = #".")) tystr
          val chunks' = if last chunks = "Impl" then butlast chunks else chunks
          val chunks'' = if length chunks' = 2 then tl chunks' else chunks' 
(*           val chunks'' = if hd chunks' = pkgName then tl chunks' else chunks' *)
      in case chunks''
          of [name] => NamedTy(pkgName,name)
           | other => raise ERR "fldty" ("unknown field type"^Lib.quote tystr)
      end;

fun dest_subcomp pkgName subcomp =
 case subcomp
  of AList [("name", String fldname), ("category", String "data"),
            ("classifier", String tystr)]
      => (fldname,fldty pkgName tystr)
   | otherwise => raise ERR "dest_subcomp" "expected a record field";

fun recd_decl pkgName names decl =
 case decl
  of AList ((name, String name_impl)::("subcomponents", List subcomps)::_)
      => if name^".Impl" = name_impl 
            andalso mem name names 
            andalso not(null subcomps)
              then (name, map (dest_subcomp pkgName) subcomps)
              else raise ERR "recd_decl" "expected a record implementation"
  | other => raise ERR "recd_decl" "expected a record declaration";

fun dropString (String s) = s
  | dropString otherwise = raise ERR "dropString" "not a json String application";

fun enum_decl decl =
 case decl
  of AList [("name", String ename), ("type", String "data"), ("features", _),
            ("properties",
             List [AList [("name", String "Data_Representation"),
                          ("value", String "Enum")],
                   AList [("name", String "Enumerators"),
                          ("value", List names)]]),
            ("agree",_)]
      => (ename, map dropString names)
  | other => raise ERR "enum_decl" "expected an enum declaration";

fun data_decl_name (AList(("name", String dname)::("type", String "data"):: _)) = dname

fun get_tydecls pkgName comps =
 let val data_tynames = mapfilter data_decl_name comps
     val recd_decls = mapfilter (recd_decl pkgName data_tynames) comps
     val enum_decls = mapfilter enum_decl comps
 in 
   (recd_decls, enum_decls)
 end;

fun get_port p =
  case p
   of AList [("port",
              AList [("name", String pname),
                     ("type", String conn_style), ("flow", String flowdir)])]
      => (pname,flowdir,conn_style)
    | otherwise => raise ERR "get_port" "unexpected port format"

fun get_filter_guarantee rname g =
    case g
     of AList [("kind", String "guarantee"),
               ("name", String gname),
               ("label", String gdoc),
               ("expr", gprop)]
        => if rname = gname
            then (gdoc, dest_exp gprop)
           else raise ERR "get_filter_guarantee" 
		   (String.concat["expected named filter guarantee ", Lib.quote rname,
				  " but encountered ", Lib.quote gname])
     | otherwise => raise ERR "get_filter_guarantee" "unexpected input format"
;
  
fun mk_filter_guarantee [x] = x
  | mk_filter_guarantee otherwise = 
    raise ERR "mk_filter_guarantee" "expected only one guarantee";
	  
fun get_filter decl =
 case decl
  of AList
      [("name", String fname), ("type", String "thread"),
       ("features", List ports),
       ("properties",
          List [AList [("name", String "COMP_TYPE"), ("value", String "FILTER")],
                AList [("name", String "COMP_IMPL"), ("value", String "CakeML")],
                AList [("name", String "COMP_SPEC"), ("value", String rname)]]),
       ("agree", List [AList [("statements", List guarantees)]])]
      => (fname,map get_port ports,
          mk_filter_guarantee (mapfilter (get_filter_guarantee rname) guarantees))
   | otherwise => raise ERR "get_filter" "not a filter thread";

fun get_pkg_decls jpkg =
 case jpkg
  of AList [("kind", String "package"),
            ("name", String pkgName),
            elt2 as ("agree", _),
            elt3 as ("components",List comps)]
     => let val _ = if current_theory() = pkgName then () else new_theory pkgName
	    val fndecls = get_fndefs elt2
            val (recd_decls, enum_decls) = get_tydecls pkgName comps
            val filters = mapfilter get_filter comps
         in (pkgName,enum_decls,recd_decls,fndecls,filters)
         end
  | otherwise => raise ERR "get_pkg_decls" "unexpected format";

(*
val [c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, 
     c13, c14, c15, c16, c17, c18, c19, c20, c21, c22, 
     c23, c24, c25, c26, c27, c28, c29, c30, c31, c32,c33,c34,c35] = comps;
c21;
*)

(*---------------------------------------------------------------------------*)
(* AST to HOL                                                                *)
(*---------------------------------------------------------------------------*)

fun list_mk_array_type(bty,dims) = 
 let open fcpSyntax
     fun mk_arr dimty bty = mk_cart_type(bty,dimty)
 in rev_itlist mk_arr dims bty
 end

fun transTy ty =
 let open AST
 in case ty 
  of NamedTy (pkg,id) => 
      (case TypeBase.read{Thy=pkg,Tyop=id}
        of NONE => raise ERR "transTy"
                   ("Unable to find type "^id^" declared in theory "^Lib.quote pkg)
         | SOME tyinfo => TypeBasePure.ty_of tyinfo)
   | BaseTy BoolTy   => Type.bool
   | BaseTy CharTy   => stringSyntax.char_ty
   | BaseTy StringTy => stringSyntax.string_ty
   | BaseTy RegexTy  => regexpSyntax.regexp_ty
   | BaseTy FloatTy  => raise ERR "transTy" "FloatTy: not implemented"
   | BaseTy (IntTy(Nat NONE)) => numSyntax.num
   | BaseTy (IntTy(Int NONE)) => intSyntax.int_ty
   | BaseTy (IntTy(Nat(SOME w))) => wordsSyntax.mk_word_type 
                                     (fcpSyntax.mk_numeric_type (Arbnum.fromInt w))
   | BaseTy (IntTy(Int(SOME w))) => wordsSyntax.mk_int_word_type w
   | ArrayTy(ty,dims) =>
       let open fcpSyntax AST
           fun transDim (ConstExp(IntLit{value,kind})) = mk_int_numeric_type value
             | transDim (VarExp id) = mk_vartype id
             | transDim otherwise = raise ERR "transTy" 
                  "array bound must be a variable or number constant"
       in
          list_mk_array_type(transTy ty, map transDim dims)
       end
   | otherwise => raise ERR "transTy" "unknown kind of ty"
 end

fun undef s = raise ERR "transExp" ("undefined case: "^Lib.quote s);

fun lift_int {value,kind} = 
 let open AST
 in case kind
     of Nat NONE     => numSyntax.term_of_int value
      | Int NONE     => intSyntax.term_of_int (Arbint.fromInt value)
      | Nat (SOME w) => wordsSyntax.mk_wordii(value,w)
      | Int (SOME w) =>  (* does this check that value fits in w bits? *)
         let open wordsSyntax
         in if 0 <= value 
             then mk_wordii(value,w)
              else mk_word_2comp (mk_wordii(Int.abs value,w))
         end
 end;

val word8 = wordsSyntax.mk_int_word_type 8;

val gdl_mk_chr = 
 let open wordsSyntax stringSyntax
 in fn tm => 
     if type_of tm = word8
     then mk_chr(mk_w2n tm)
     else raise ERR "gdl_mk_chr" "expected arg. with type uint8"
 end;

val gdl_mk_ord = 
 let open wordsSyntax stringSyntax
 in fn tm => mk_n2w(mk_ord tm,word8)
    handle HOL_ERR _ => raise ERR "gdl_mk_ord" "expected arg. with type char"
 end

fun unop (uop,e) t = 
 let open AST 
     val ty = type_of t
     fun lift f = f t
 in case uop 
     of ChrOp  => lift gdl_mk_chr
      | OrdOp  => lift gdl_mk_ord
      | Not    => lift boolSyntax.mk_neg
      | BitNot => lift wordsSyntax.mk_word_1comp
      | UMinus => 
          if ty = intSyntax.int_ty 
              then lift intSyntax.mk_negated else
            if wordsSyntax.is_word_type ty
              then lift wordsSyntax.mk_word_2comp
              else raise ERR "unop (UMinus)" 
                   "expected type of operand to be int\
                   \ (either fixed width or unbounded)"
      | Signed => 
          if ty = numSyntax.num
              then lift intSyntax.mk_injected else
          if ty = intSyntax.int_ty 
              then lift combinSyntax.mk_I else
            if wordsSyntax.is_word_type ty
              then lift combinSyntax.mk_I 
          else raise ERR "unop (Signed)" 
               "expected type of operand to be num, int, or word"
      | Unsigned => 
          if ty = intSyntax.int_ty 
              then lift intSyntax.mk_Num else
          if ty = numSyntax.num
              then lift combinSyntax.mk_I else
          if wordsSyntax.is_word_type ty
              then lift combinSyntax.mk_I 
          else raise ERR "unop (Signed)" 
               "expected type of operand to be num, int, or word"
      | Unbounded => 
          if ty = intSyntax.int_ty orelse ty = numSyntax.num
              then lift combinSyntax.mk_I else
          if wordsSyntax.is_word_type ty
              then raise ERR "unop (Signed)" "unable to determine sign of word arg"
          else raise ERR "unop (Signed)" 
               "expected type of operand to be num, int, or word"
 end;

fun binop (bop,e1,_) t1 t2 = 
 let open AST
     fun lift f = f (t1,t2)
     val ty1 = type_of t1
     fun dispatch (numsOp,intsOp,wordsOp) =
      if ty1 = numSyntax.num then lift numsOp else 
      if ty1 = intSyntax.int_ty then lift intsOp else
      if wordsSyntax.is_word_type ty1 then lift wordsOp 
       else raise ERR "binop (dispatch)" 
            "expected type of operands to be num, int, or word"
     fun dispatchSigned (numsOp,intsOp,uwordsOp,swordsOp) =
       if ty1 = numSyntax.num then lift numsOp else 
       if ty1 = intSyntax.int_ty then lift intsOp else
       if wordsSyntax.is_word_type ty1
         then raise ERR "binop (dispatchSigned)" 
                        "unable to determine sign"
       else raise ERR "binop (dispatchSigned)" 
            "expected type of operands to be num, int, or word"
 in
  case bop
   of Equal         => lift boolSyntax.mk_eq
    | NotEqual      => lift (boolSyntax.mk_neg o boolSyntax.mk_eq)
    | Or            => lift boolSyntax.mk_disj
    | And           => lift boolSyntax.mk_conj
    | Imp           => lift boolSyntax.mk_imp
    | BitOr         => lift wordsSyntax.mk_word_or
    | BitAnd        => lift wordsSyntax.mk_word_and
    | BitXOR        => lift wordsSyntax.mk_word_xor
    | LogicalLShift => lift wordsSyntax.mk_word_lsl
    | LogicalRShift => lift wordsSyntax.mk_word_lsr
    | ArithmeticRShift => lift wordsSyntax.mk_word_asr
    | Plus => dispatch 
               (numSyntax.mk_plus,intSyntax.mk_plus,wordsSyntax.mk_word_add)
    | Minus => dispatch
                (numSyntax.mk_minus,intSyntax.mk_minus,wordsSyntax.mk_word_sub)
    | Multiply => dispatch
                   (numSyntax.mk_mult,intSyntax.mk_mult,wordsSyntax.mk_word_mul)
    | Exponent => dispatch
                   (numSyntax.mk_exp, intSyntax.mk_exp,
                    fn _ => raise ERR "binop" "exponent for word types not implemented")
    | Divide => dispatchSigned
                   (numSyntax.mk_div, intSyntax.mk_div,
                    wordsSyntax.mk_word_div, 
                    fn _ => raise ERR "binop" "signed division for word types not implemented")
    | Modulo => dispatchSigned (numSyntax.mk_mod,intSyntax.mk_mod,
                                wordsSyntax.mk_word_mod,
	                    fn _ => raise ERR "binop" "signed modulus for word types not implemented")

    | Less => dispatchSigned
                (numSyntax.mk_less, intSyntax.mk_less, 
                 wordsSyntax.mk_word_lo,wordsSyntax.mk_word_lt)
    | Greater => dispatchSigned
                   (numSyntax.mk_greater, intSyntax.mk_great, 
                    wordsSyntax.mk_word_hi,wordsSyntax.mk_word_gt)
    | LessEqual => dispatchSigned
                     (numSyntax.mk_leq, intSyntax.mk_leq,
                      wordsSyntax.mk_word_ls,wordsSyntax.mk_word_le)
    | GreaterEqual => dispatchSigned
                         (numSyntax.mk_geq,intSyntax.mk_geq,
                          wordsSyntax.mk_word_hs,wordsSyntax.mk_word_ge)
    | CastWidth => raise ERR "binop" "CastWidth not implemented"
    | RegexMatch => lift regexpSyntax.mk_regexp_matcher
 end;

fun mk_constr_const currentpkg (pkg,ty) cname =
    case Term.decls cname
     of [] => raise ERR "mk_constr_const" 
              (Lib.quote cname^" not a declared constant")
      | [c] => c
      | more_than_one => 
         (HOL_MESG ("mk_constr_const: multiple declarations for "
                    ^Lib.quote cname)
         ; hd more_than_one);

fun transExp pkgName varE exp =
  case exp
   of VarExp id => (assoc id varE handle HOL_ERR _ =>
	            raise ERR "transExp" ("free variable: "^Lib.quote id))
     | ConstExp (BoolLit b)   => boolSyntax.lift_bool ind b
     | ConstExp (CharLit c)   => stringSyntax.lift_char ind c
     | ConstExp (StringLit s) => stringSyntax.lift_string ind s
     | ConstExp (IntLit vk)   => lift_int vk
     | ConstExp (RegexLit r)  => undef "RegexLit"  (* lift_regex r *)
     | ConstExp (FloatLit f)  => undef "FloatLit"
     | Unop (node as (uop,e)) => unop node (transExp pkgName varE e)
     | Binop (node as (_,e1,e2)) => 
         let val t1 = transExp pkgName varE e1
             val t2 = transExp pkgName varE e2
         in binop node t1 t2
         end
     | RecdProj(e,id) =>   (* record projection *)
         let val t = transExp pkgName varE e
             val recdty = type_of t
             val projName = fst(dest_type recdty)^"_"^id
             val fld_proj = prim_mk_const{Name=projName,Thy=pkgName}
         in
            mk_comb(fld_proj,t)
         end
    | RecdExp(qid,fields) => undef "RecdExp"
    | ConstrExp(qid,id, NONE) => mk_constr_const pkgName qid id
    | ConstrExp(qid,id, SOME e) => undef "ConstrExp with arg"
    | Fncall ((thyname,cname),expl) => 
       (let val c = prim_mk_const{Thy=thyname,Name=cname}
        in list_mk_comb(c,map (transExp pkgName varE) expl)
        end handle e as HOL_ERR _ => raise wrap_exn "" "transExp" e)
    | ConstExp (IdConst qid) => undef "ConstExp: IdConst"
    | ArrayIndex(A,indices) => undef "ArrayIndex"
    | ArrayExp elist => undef "ArrayExp"
    | Quantified(quant,qvars,exp) => undef "Quantified"

(*---------------------------------------------------------------------------*)
(* Make regexp string acceptable to Regexp_Type parser                       *)
(*---------------------------------------------------------------------------*)

fun drop_excess_backslashery s =
    let open String
    in concatWith "\\" (tokens (equal #"\\") s)
    end;

fun subst_uminus str =
    let open Char
	fun deal (ch1 :: #"-" :: ch2 :: t) =
	    if isDigit ch2 andalso Lib.mem ch1 [#"{", #"(", #","] then
		ch1 :: #"~" :: ch2 :: deal t
	    else ch1:: #"-" :: deal (ch2::t)
	  | deal (ch::t) = ch :: deal t
	  | deal [] = []
    in
	String.implode (deal (String.explode str))
    end;

val revise_regexp_string = subst_uminus o drop_excess_backslashery;

fun exp_calls [] acc = acc
  | exp_calls (VarExp _::t) acc = exp_calls t acc
  | exp_calls (ConstExp _::t) acc = exp_calls t acc
  | exp_calls (Unop(_,e)::t) acc = exp_calls (e::t) acc
  | exp_calls (Binop(_,e1,e2)::t) acc = exp_calls (e1::e2::t) acc
  | exp_calls (ArrayExp elist::t) acc = exp_calls (elist@t) acc
  | exp_calls (ArrayIndex(_,elist)::t) acc = exp_calls (elist@t) acc
  | exp_calls (ConstrExp (_,_,NONE)::t) acc = exp_calls t acc
  | exp_calls (ConstrExp (_,_,SOME e)::t) acc = exp_calls (e::t) acc
  | exp_calls ((call as Fncall(qid,elist))::t) acc =
    exp_calls (elist@t) (call::acc)
  | exp_calls (RecdExp (qid,fields)::t) acc = exp_calls (map snd fields@t) acc
  | exp_calls (RecdProj(e,_)::t) acc = exp_calls (e::t) acc
  | exp_calls (Quantified(_,_,e)::t) acc = exp_calls (e::t) acc;

fun establish_type s e =
    let val calls = exp_calls [e] []
        fun spred exp =
	    case exp
	     of Fncall(c,args) => op_mem (curry eqExp) (VarExp s) args
              | other => false
    in case filter spred calls
	of [] => raise ERR "establish_type" "can't resolve"
         | (Fncall((thy,fname),elist)::t) =>
	   let val const = prim_mk_const{Thy=thy,Name=fname}
	       val ty = type_of const
	       val (tyl,_) = strip_fun ty
	       val alist = zip elist tyl
	   in
	       (s, mk_var(s,op_assoc (curry eqExp) (VarExp s) alist))
           end
         | otherwise => raise ERR "establish_type" "expected Fncall"
    end;

fun mk_filter_goal (info as (pkgName,enums,recds,fn_defs))
		   (fname, ports, (comment,prop)) = 
    let val outport = Lib.first (fn (_,dir,_) => (dir = "out")) ports
	val inport = Lib.first (fn (_,dir,_) => (dir = "in")) ports
        val outpname = #1 outport
        val inpname = #1 inport
        val varOut = establish_type outpname prop
        val ty = type_of (snd varOut)
        val varIn = (inpname,mk_var(inpname,ty))
        val outvar = snd varOut
        val recd_prop = transExp pkgName [varIn,varOut] prop
        val simple_prop = fst(dest_imp(snd(dest_conj recd_prop)))
        val thm = 
	    PURE_REWRITE_CONV 
              (GSYM CONJ_ASSOC::GSYM DISJ_ASSOC::fn_defs) simple_prop
         handle _ => raise ERR "mk_filter_goal" "strange filter goal"
        val (regexp_tm,encode_def,decode_def,the_goal) = splatLib.mk_correctness info thm
  in
      (fname, regexp_tm, encode_def, decode_def, the_goal)
  end

fun declare_hol_filter_goal (fname,regexp_tm,encode_def, decode_def, the_goal) =
  let val correctness = 
     store_thm(fname^"_correct", 
       ``FILTER_CORRECT ^(stringSyntax.fromMLstring fname) ^the_goal``,
       cheat)
   in 
    stdErr_print 
          (String.concat 
               ["\nFilter ", Lib.quote fname, 
                " is specified by property \n\n  ",
                thm_to_string correctness, "\n\n"])
  end;

(* TOPSORT GUNK : second fn calls the first *)

fun called_by (id1,_,_) (id2,_,body) = 
    let val callers = map snd (AST.exp_calls [body] [])
    in mem id1 callers
    end;

fun gen_hol (pkgName,enums,recds,fns,filters) =
    let val _ = stdErr_print "\nGenerating HOL theory.\n\n"
        fun declare_hol_enum (ename,cnames) =
           if Lib.can mk_type (ename,[])
            then stdErr_print 
                   ("Enumeration "^Lib.quote ename^" has been predeclared\n")
           else 
             let open Datatype ParseDatatype
                 val _ = astHol_datatype
                                [(ename,Constructors (map (C pair []) cnames))]
                 val () = splatLib.define_enum_encoding (mk_type (ename,[]))
             in
	        stdErr_print ("Declared enumeration "^Lib.quote ename^"\n")
             end
      fun declare_hol_record (rname,fields) =
          let open Datatype ParseDatatype
              fun ty2pretype pkgName (NamedTy (p,s)) = 
                    let val thy = if p=pkgName then NONE else SOME p
                    in dTyop{Tyop=s,Thy=thy,Args=[]} 
                    end
	        | ty2pretype pkgName ty = dAQ (transTy ty)
              fun mk_field(s,ty) = (s,ty2pretype pkgName ty)
          in astHol_datatype
                 [(rname,Record (map mk_field fields))]
            ; stdErr_print ("Declared record "^Lib.quote rname^"\n")
          end
      fun declare_hol_fn (name,params,body) =
          let fun mk_hol_param (s,ty) = (s, mk_var(s,transTy ty))
	      val varE = map mk_hol_param params
              val param_vars = map snd varE
              val body_tm = transExp pkgName varE body
	      val def_var =
		  mk_var(name,list_mk_fun (map type_of param_vars, type_of body_tm))
	      val def_tm = mk_eq(list_mk_comb(def_var,param_vars),body_tm)
	      val def = PURE_REWRITE_RULE [GSYM CONJ_ASSOC]
                           (new_definition(name^"_def", def_tm))
	  in
	    stdErr_print ("Defined function "^Lib.quote name^"\n")
           ;
            def
          end
      val _ = List.app declare_hol_enum enums
      val _ = List.app declare_hol_record recds
      val fns' = topsort called_by fns
      val fn_defs = MiscLib.mapfilter declare_hol_fn fns'
      val info = (pkgName,enums,recds,fn_defs)
      val filter_goals = map (mk_filter_goal info) filters
      val _ =  List.app declare_hol_filter_goal filter_goals
 in	  
     export_theory()
  end;

end

