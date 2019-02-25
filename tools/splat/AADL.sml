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

 type qid = string * string;
 type ty = AST.ty;
 type exp = AST.exp;
 type tyEnv = (ty * ty) list;

 datatype tydec
    = EnumDec of qid * string list
    | RecdDec of qid * (string * ty) list
    | ArrayDec of qid * ty;

 datatype tmdec
    = ConstDec of qid * ty * exp
    | FnDec of qid * (string * ty) list * ty * exp;

 datatype filter = FilterDec of qid * (string * ty * string * string) list * (string * exp)
								      
 type decls = 
  (* pkgName *)  string * 
  (* types *)    (tydec list * 
  (* consts *)    tmdec list *
  (* filters *)   filter list)
  ;

val ERR = Feedback.mk_HOL_ERR "AADL";

(*---------------------------------------------------------------------------*)
(* Json to AST (types, expressions, function definitions, and filters)       *)
(*---------------------------------------------------------------------------*)

fun dest_qid s =
 let val chunks = String.tokens (fn c => equal #"." c orelse equal #":" c) s
 in case Lib.front_last chunks
     of ([a,b],"Impl") => (a,b)
      | ([a],"Impl") => ("",a)
      | ([],"Impl") => raise ERR "dest_qid" "unexpected format"
      | ([a],b) => (a,b)
      | ([],b)  => ("",b)
      | otherwise => raise ERR "dest_qid" "unexpected format"
 end;

fun dest_tyqid dotid = 
 case dest_qid dotid
  of ("","Integer") => BaseTy(IntTy (!defaultNumKind))
   | ("","Bool") => BaseTy BoolTy
   | ("","String") => BaseTy StringTy
   | (a,b) => NamedTy (a,b);

fun get_tyinfo tyinfo =
 case tyinfo
  of [("kind", String "recordType"),
      ("recordType",
       AList [("kind", String "typeId"), ("name", String dotid)])]
     => dest_tyqid dotid
   | [("kind", String "recordType"),
      ("recordType",
       AList [("kind", String "nestedDotId"), ("name", String dotid)])]
     => dest_tyqid dotid
   | [("kind", String "typeId"), ("name", String tyname)]
     => dest_tyqid tyname
   | [("kind", String "DoubleDotRef"), ("name", String tyname)]
     => dest_tyqid tyname
   | [("kind", String "primType"), ("primType", String "bool")] 
     => BaseTy BoolTy
   | [("kind", String "primType"), ("primType", String "int")] 
     => BaseTy(IntTy (!defaultNumKind))
   | [("kind", String "PrimType"), ("primType", String "bool")] 
     => BaseTy BoolTy
   | [("kind", String "PrimType"), ("primType", String "int")] 
     => BaseTy(IntTy (!defaultNumKind))
   | otherwise => raise ERR "get_tyinfo" "unable to construct type";

fun dest_ty ty =
 case ty
  of AList [("kind", String "type"), ("type", AList tyinfo)] => get_tyinfo tyinfo
   | AList tyinfo => get_tyinfo tyinfo
   | otherwise => raise ERR "dest_ty" "expected a type expression"
;

fun mk_dotid did =
  case Lib.front_last (String.tokens (equal #".") did)
  of ([],v) => VarExp v
   | (v::t,proj) => rev_itlist (C (curry RecdProj)) (t @ [proj]) (VarExp v)
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

fun mk_fncall(fname, args) = Fncall(dest_qid fname,args);

fun mk_nullary_constr(cname, ty) =
 case ty
  of NamedTy tyqid => ConstrExp(tyqid,cname,NONE)
   |  otherwise => raise ERR "mk_nullary_constr"
       ("unable to determine type of constructor "^Lib.quote cname)

(*---------------------------------------------------------------------------*)
(* AADL scraping.                                                            *)
(*---------------------------------------------------------------------------*)

val _ = defaultNumKind := AST.Int NONE;

fun dropString (String s) = s
  | dropString otherwise = raise ERR "dropString" "not a json String application";

fun dropImpl s =
     case String.tokens (equal #".") s
      of [x,"Impl"] => SOME x
       | otherwise => NONE;

fun dest_named_ty (NamedTy qid) = qid
  | dest_named_ty other = raise ERR "dest_named_ty" "expected a NamedTy";

fun dest_exp e =
 case e
  of AList [("kind", String "NamedElmExpr"), ("name", String s)]
       => VarExp s
   | AList [("kind", String "BoolLitExpr"), ("value", String bstr)]
       => mk_bool_const bstr
   | AList [("kind", String "IntLitExpr"), ("value", String intstr)]
       => mk_int intstr
   | AList [("kind", String "StringLitExpr"), ("value", String str)]
       => ConstExp(StringLit str)
   | AList [("kind", String "UnaryExpr"), ("operand", e), ("op", String opr)]
       => mk_unop (opr,dest_exp e)
   | AList [("kind", String "BinaryExpr"),
            ("left", le), ("op", String opr), ("right",re)]
       => mk_binop (opr,dest_exp le, dest_exp re)
   | AList [("kind", String "RecordLitExpr"), 
            ("recordType", rty), ("value", AList jfields)]
       => RecdExp(dest_named_ty (dest_ty rty), map mk_field jfields)
   | AList [("kind", String "Selection"), ("target", target), ("field", String fname)]
       => RecdProj (dest_exp target, fname)
   | AList [("kind", String "CallExpr"),
             ("function", AList [("kind", String _), ("name", String fname)]),
             ("args", List args)]
       => mk_fncall(fname,map dest_exp args)
   | AList [("kind", String "AadlEnumerator"),
            ("type", AList tyinfo), ("value", String constrname)]
       => mk_nullary_constr (constrname,get_tyinfo tyinfo)
   | AList [("kind", String "ForallExpr"),
            ("binding", String bvarname),
            ("array", jarr),
            ("expr", jexp)]
       => mk_fncall ("Array_Forall",[VarExp bvarname, dest_exp jarr, dest_exp jexp])
   | other => raise ERR "dest_exp" "unexpected expression form"
and
mk_field (fname,e) = (fname, dest_exp e);


fun dest_param param =
 case param
  of AList [("name", String pname), ("type", ty)] => (pname,dest_ty ty)
   | otherwise => raise ERR "dest_param" "unexpected input";

fun mk_fun_def pkgName json =
 case json
  of AList [("kind", String "FnDef"),
            ("name", String fname),
            ("args", List params),
            ("type", ty),
            ("expr", body)] => 
     FnDec((pkgName,fname), map dest_param params, dest_ty ty, dest_exp body)
   | otherwise => raise ERR "mk_fun_def" "unexpected input";

fun mk_const_def pkgName json =
 case json
  of AList [("kind", String "ConstStatement"),
            ("name", String cname),
            ("type", ty),
            ("expr", body)] => ConstDec((pkgName,cname), dest_ty ty, dest_exp body)
  |  otherwise => raise ERR "mk_const_def" "unexpected input";

fun mk_def pkgName json = 
  mk_const_def pkgName json handle HOL_ERR _ => 
  mk_fun_def pkgName json   handle HOL_ERR _ => 
  raise ERR "mk_def" "unexpected syntax"; 

fun mk_defs pkgName annex =  (* package annex *)
 case annex
  of AList [("name", String agree),
            ("kind", String "AnnexLibrary"), 
            ("parsedAnnexLibrary",
	     AList [("statements", List decls)])] => mapfilter (mk_def pkgName) decls
   | otherwise => raise ERR "get_fndefs" "unexpected annex format";

fun fldty tystr =
 case dest_qid tystr
  of ("Base_Types","Integer") => BaseTy(IntTy (!defaultNumKind))
   | ("Base_Types","Boolean") => BaseTy(BoolTy)
   | ("Base_Types","String") => BaseTy(StringTy)
   | (pkg,tyname) => NamedTy(pkg,tyname);

fun dest_field pkgName subcomp =
 case subcomp
  of AList [("name", String fldname), 
            ("kind", String "Subcomponent"),
            ("category", String "data"),
            ("classifier", String tystr)] => (fldname,fldty tystr)
   | otherwise => raise ERR "dest_field" "expected a record field";

fun recd_decl pkgName names decl =
 case decl
  of AList (("name", String name_impl) ::
            ("kind",String "ComponentImplementation") ::
            ("category", String "data") ::
	    ("subcomponents", List subcomps)::_)
     => (case dropImpl name_impl
	  of NONE => raise ERR "recd_decl" "expected .Impl suffix"
           | SOME name =>
	      if mem name names andalso not(null subcomps)
              then RecdDec(dest_qid name, map (dest_field pkgName) subcomps)
              else raise ERR "recd_decl" "expected a record implementation"
	)
   | AList [("name", String name_impl),
            ("kind", String "ComponentImplementation"), 
            ("category", String "data"),
            ("extends", AList orig_recd_spec),
            ("subcomponents", substs)]
     => (case dropImpl name_impl
	  of NONE => raise ERR "recd_decl" "expected .Impl suffix"
           | SOME name =>
	     let val List orig_subcomps = assoc "subcomponents" orig_recd_spec
		 val orig_fields = map (dest_field pkgName) orig_subcomps
                 val List field_substs = substs
		 val new_fields = map (dest_field pkgName) field_substs
                 fun replace_field (name,ty) =
		    case assoc1 name new_fields
                     of NONE => (name,ty)
                      | SOME field => field
                 val fields = map replace_field orig_fields
             in RecdDec(dest_qid name,fields)
	     end)
   | other => raise ERR "recd_decl" "expected a record declaration";

fun enum_decl decl =
 case decl
  of AList [("name", String ename), 
            ("kind",String "ComponentType"),
            ("category", String "data"), 
            ("properties",
             List [AList [("name", String "Data_Model::Data_Representation"),
                          ("kind", String "PropertyAssociation"),
                          ("value", String "Enum")],
                   AList [("name", String "Data_Model::Enumerators"),
                          ("kind", String "PropertyAssociation"),
                          ("value", List names)]])]
      => EnumDec(dest_qid ename, map dropString names)
  | other => raise ERR "enum_decl" "expected an enum declaration";

fun array_decl decl =
 case decl
  of AList [("name", String name),
            ("kind",String "ComponentType"),
            ("category", String "data"),
	    ("properties", List
             [AList [("name", String "Data_Model::Data_Representation"),
                     ("kind", String "PropertyAssociation"), 
                     ("value", String "Array")],
              AList [("name", String "Data_Model::Base_Type"),
                     ("kind", String "PropertyAssociation"),
                     ("value", List [String baseTyName])],
              AList [("name", String "Data_Model::Dimension"),
                     ("kind", String "PropertyAssociation"),
                     ("value", List [String dimString])]])]
     => let val basety = get_tyinfo [("kind", String "typeId"), 
                                     ("name", String baseTyName)]
            val dim = mk_uintLit(valOf(Int.fromString dimString))
        in ArrayDec(dest_qid name, ArrayTy(basety,[dim]))
        end
  | other => raise ERR "array_decl" "expected an array declaration";

fun data_decl_name (AList(("name", String dname) ::
                          ("kind", String "ComponentType") ::
                          ("category", String "data") :: _)) = dname

fun get_tydecl pkgName names thing =
   enum_decl thing handle HOL_ERR _ => 
   recd_decl pkgName names thing handle HOL_ERR _ => 
   array_decl thing;

fun get_tydecls pkgName complist =
 let val data_tynames = mapfilter data_decl_name complist
 in mapfilter (get_tydecl pkgName data_tynames) complist
 end;

fun get_port port =
 case port
  of AList [("name", String pname),
            ("kind", String conn_style), 
            ("classifier", String tyqidstring),
            ("direction", String flowdir)]
      => (pname,dest_tyqid tyqidstring,flowdir,conn_style)
    | otherwise => raise ERR "get_port" "unexpected port format"

fun get_filter_guarantee rname g =
    case g
     of AList [("kind", String "GuaranteeStatement"),
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
      [("name", String fname), 
       ("kind",String "ComponentType"),
       ("category", String "thread"),
       ("features", List ports),
       ("properties",
          List [AList [("name", String "CASE_Properties::COMP_TYPE"), _, 
                       ("value", String "FILTER")],
                AList [("name", String "CASE_Properties::COMP_IMPL"), _,
                       ("value", String "CakeML")],
                AList [("name", String "CASE_Properties::COMP_SPEC"), _, 
                       ("value", String rname)]]),
       ("annexes", List [AList [("name", String "agree"),
                                ("kind", String "AnnexSubclause"),
                                ("parsedAnnexSubclause",
                                 AList [("statements", List guarantees)])]])]
      => FilterDec(dest_qid fname, map get_port ports,
          mk_filter_guarantee (mapfilter (get_filter_guarantee rname) guarantees))
   | otherwise => raise ERR "get_filter" "not a filter thread";

fun dest_publist plist =
 let fun dest_with ("with", List wlist) = wlist
       | dest_with other = raise ERR "dest_with" ""
     fun dest_renames ("renames", List rlist) = rlist
       | dest_renames other = raise ERR "dest_renames" ""
     fun dest_comps ("components", List clist) = clist
       | dest_comps other = raise ERR "dest_comps" ""
     fun dest_annexes ("annexes", List alist) = alist
       | dest_annexes other = raise ERR "dest_annexes" ""
 in 
    (List.concat (mapfilter dest_with plist), 
     List.concat (mapfilter dest_renames plist), 
     List.concat (mapfilter dest_comps plist),
     List.concat (mapfilter dest_annexes plist))
 end

fun get_data_model pkg =
 case pkg 
  of AList [("name", String dmName), 
            ("kind", String "PropertySet"),
            ("properties", proplist)] => (dmName,proplist)
  |  otherwise => raise ERR "get_data_model" "unexpected format"
;
	    
fun scrape pkg =
 case pkg
  of AList [("name", String pkgName),
            ("kind", String "AadlPackage"),
            ("public", AList publist)]
     => let val (withs, renamings, complist,annexlist) = dest_publist publist
            val tydecls = get_tydecls pkgName complist
	    val fndecls = List.concat (mapfilter (mk_defs pkgName) annexlist)
            val filters = mapfilter get_filter complist
        in 
           (pkgName,(tydecls, fndecls, filters))
        end
  | otherwise => raise ERR "scrape" "unexpected format";

fun dest_with ("with", List wlist) = map dropString wlist
  | dest_with other = raise ERR "dest_with" "";

fun scrape_pkgs (List pkgs) =
    let fun uses (A as AList [("name", String AName),
                              ("kind", String "AadlPackage"),
                              ("public", AList publist)],
                  B as AList [("name", String BName),
                              ("kind", String "AadlPackage"),_]) = 
             let val Auses = List.concat (mapfilter dest_with publist)
             in mem BName Auses
             end
          | uses otherwise = false
        val opkgs = topsort (curry uses) pkgs
        val declist = mapfilter scrape opkgs
(*        val datalist = mapfilter get_data_model opkgs *)
    in
	rev declist
    end
  | scrape_pkgs otherwise = raise ERR "scrape_pkgs" "unexpected format";


(*---------------------------------------------------------------------------*)
(* AST to HOL                                                                *)
(*---------------------------------------------------------------------------*)

fun list_mk_array_type(bty,dims) = 
 let open fcpSyntax
     fun mk_arr dimty bty = mk_cart_type(bty,dimty)
 in rev_itlist mk_arr dims bty
 end

fun transTy tyEnv ty =
 let open AST
 in case ty 
  of NamedTy (pkg,id) => 
      (case Lib.op_assoc1 (curry AST.eqTy) ty tyEnv
        of SOME ty' => transTy tyEnv ty'
         | NONE => 
           let val pkgName = current_theory()
           in case TypeBase.read{Thy=pkgName,Tyop=id}
               of SOME tyinfo => TypeBasePure.ty_of tyinfo
                | NONE => raise ERR "transTy"
                  ("Unable to find type "^id^" declared in theory "^Lib.quote pkg)
	   end)
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
       let fun transDim (ConstExp(IntLit{value,kind})) = fcpSyntax.mk_int_numeric_type value
             | transDim (VarExp id) = mk_vartype id
             | transDim otherwise = raise ERR "transTy" 
                  "array bound must be a variable or number constant"
       in
          list_mk_array_type(transTy tyEnv ty, map transDim dims)
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

fun organize_fields progfields tyinfo_fields =
 let fun reorg [] _ = []
       | reorg ((s,_)::t) list = 
          let val (x,list') = Lib.pluck (equal s o fst) list
          in x::reorg t list'
          end
 in 
  reorg progfields tyinfo_fields 
 end;

datatype expect = Unknown | Expected of hol_type;

fun mk_id varE ety id = 
 case assoc1 id varE 
  of SOME (_,v) => v
   | NONE =>
     case ety
      of Expected ty => 
           (mk_const(id,ty) handle HOL_ERR _ => mk_var(id,ty))
       | Unknown => 
           case Term.decls id
            of [const] => const
             | [] => raise ERR "transExp" ("unknown free variable: "^Lib.quote id)
             | otherwise => raise ERR "transExp" 
               ("multiple choices for resolving free variable: "^Lib.quote id);

fun transExp pkgName varE ety exp =
  case exp
   of VarExp id => mk_id varE ety id
    | ConstExp (BoolLit b)   => boolSyntax.lift_bool ind b
    | ConstExp (CharLit c)   => stringSyntax.lift_char ind c
    | ConstExp (StringLit s) => stringSyntax.lift_string ind s
    | ConstExp (IntLit vk)   => lift_int vk
    | ConstExp (RegexLit r)  => undef "RegexLit"  (* lift_regex r *)
    | ConstExp (FloatLit f)  => undef "FloatLit"
    | Unop (node as (uop,e)) => unop node (transExp pkgName varE Unknown e)
    | Binop (node as (_,e1,e2)) => 
         let val t1 = transExp pkgName varE Unknown e1
             val t2 = transExp pkgName varE Unknown e2
         in binop node t1 t2
         end
    | RecdProj(e,id) =>   (* record projection *)
         let val t = transExp pkgName varE Unknown e
             val recdty = type_of t
             val projName = fst(dest_type recdty)^"_"^id
             val fld_proj = prim_mk_const{Name=projName,Thy=pkgName}
         in
            mk_comb(fld_proj,t)
         end
    | RecdExp(qid,fields) =>
      let val rty = mk_type (snd qid,[]);
      in case TypeBase.fetch rty
          of NONE => raise ERR "transExp" 
                     ("failed attempt to construct record with type "^Lib.quote (snd qid))
	   | SOME rtyinfo => 
             let val fieldnames = map fst fields
                 val tyfields = TypeBasePure.fields_of rtyinfo
                 val tyfields' = organize_fields fields tyfields
                 val expectedtys = map (Expected o snd) tyfields'
                 val field_exps = map2 (transExp pkgName varE) expectedtys (map snd fields)
             in TypeBase.mk_record (rty,zip fieldnames field_exps)
             end
      end
    | ConstrExp(qid,id, NONE) => mk_constr_const pkgName qid id
    | ConstrExp(qid,id, SOME e) => undef "ConstrExp with arg"
    | Fncall ((_,"Array_Forall"),[VarExp bv,e2,e3]) => 
      let open fcpSyntax
	  val A = transExp pkgName varE Unknown e2
          val (elty,dimty) = dest_cart_type (type_of A)
          val v = mk_var(bv,elty)
          val varE' = (bv,v)::varE
          val Pbody = transExp pkgName varE' (Expected bool) e3
          val fcp_every_tm' = inst [alpha |-> dimty, beta |-> elty] fcp_every_tm
      in list_mk_comb(fcp_every_tm',[mk_abs(v,Pbody), A])
      end
    | Fncall ((_,"Array_Forall"),_) => raise ERR "transExp" "Array_Forall: unexpected syntax"
    | Fncall ((_,"Array_Exists"),[VarExp bv,e2,e3]) => 
      let open fcpSyntax
	  val A = transExp pkgName varE Unknown e2
          val (elty,dimty) = dest_cart_type (type_of A)
          val v = mk_var(bv,elty)
          val varE' = (bv,v)::varE
          val Pbody = transExp pkgName varE' (Expected bool) e3
          val fcp_exists_tm' = inst [alpha |-> dimty, beta |-> elty] fcp_exists_tm
      in list_mk_comb(fcp_exists_tm',[mk_abs(v,Pbody), A])
      end
    | Fncall ((_,"Array_Exists"),_) => raise ERR "transExp" "Array_Exists: unexpected syntax"
    | Fncall ((thyname,cname),expl) => 
       (let val thyname' = if thyname = "" then pkgName else thyname
            val c = prim_mk_const{Thy=thyname',Name=cname}
        in list_mk_comb(c,map (transExp pkgName varE Unknown) expl)
        end handle e as HOL_ERR _ => raise wrap_exn "" "transExp" e)
    | ConstExp (IdConst qid) => undef "ConstExp: IdConst"
    | ArrayIndex(A,indices) => undef "ArrayIndex"
    | ArrayExp elist => undef "ArrayExp"
    | Quantified(quant,qvars,exp) => undef "Quantified"

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


(* TOPSORT GUNK : second thing mentions the first *)

fun called_by (FnDec((_,id),_,_,_)) (FnDec(_,_,_,exp)) = 
      mem id (map snd (AST.exp_calls [exp] []))
  | called_by (FnDec((_,id),_,_,_)) (ConstDec (_,_,exp)) = 
      mem id (map snd (AST.exp_calls [exp] []))
  | called_by (ConstDec((_,id),_,_)) (FnDec (_,_,_,exp)) = mem id (expIds exp [])
  | called_by (ConstDec((_,id),_,_)) (ConstDec (_,_,exp)) = mem id (expIds exp []);

fun declare_hol_enum ((pkgName,ename),cnames) =
    if Lib.can mk_type (ename,[])
    then stdErr_print ("Enumeration "^Lib.quote ename^" has been predeclared\n")
    else 
    let open Datatype ParseDatatype
        val _ = astHol_datatype [(ename,Constructors (map (C pair []) cnames))]
        val () = splatLib.define_enum_encoding (mk_type (ename,[]))
    in
     stdErr_print ("Declared enumeration "^Lib.quote ename^"\n")
    end;

(*---------------------------------------------------------------------------*)
(* Puts type alpha in place of null. Morally, I should put a different type  *)
(* variable at each occurrence, but that can come later. There is also an    *)
(* assumption that all declarations of records, enums, etc, are taking place *)
(* in the current theory, so the pkgName is ignored.                         *)
(*---------------------------------------------------------------------------*)

fun declare_hol_record tyEnv ((_,rname),fields) =
    let open Datatype ParseDatatype
        fun ty2pretype ty =
	    case ty
	     of NamedTy ("","null") => dVartype "'a"
              | NamedTy ("Base_Types",_) => dAQ (transTy tyEnv ty)
(*              | NamedTy qid => 
                 (case op_assoc1 (curry tyEq) ty tyEnv
                   of SOME ty' => dAQ (ty'
                   |   => dTyop{Tyop=s,Thy=NONE,Args=[]} 
*)
              | ty => dAQ (transTy tyEnv ty)
        fun mk_field(s,ty) = (s,ty2pretype ty)
    in 
      astHol_datatype [(rname,Record (map mk_field fields))]
    ; stdErr_print ("Declared record "^Lib.quote rname^"\n")
    end

(*
fun declare_hol_array (name, ArrayTy(bty,[dim])) =
    let val dim_tm = transExp (current_theory()) [] Unknown dim
        val base_ty = transTy bty
        open Datatype ParseDatatype
    in 
	astHol_datatype
         [(name, Record [("size", dAQ numSyntax.num),
                        ("elts", dAQ (listSyntax.mk_list_type base_ty))])]
      ;
        stdErr_print ("Declared array "^Lib.quote name^"\n")
    end
  | declare_hol_array otherwise = raise ERR "declare_hol_array" "unexpected syntax";
*)

(*---------------------------------------------------------------------------*)
(* Array decs get added to a tyEnv (really just an arrayEnv for now). Hack:  *)
(* the parser does not alway give a package name to named types, and this    *)
(* matters when mapping named types to array types, so we just add the       *)
(* anonymous pkgName qid to the domain of the env.                           *)
(*---------------------------------------------------------------------------*)

fun declare_hol_type (EnumDec enum) tyEnv = (declare_hol_enum enum; tyEnv)
  | declare_hol_type (RecdDec recd) tyEnv = (declare_hol_record tyEnv recd; tyEnv)
  | declare_hol_type (ArrayDec (qid,ty)) tyEnv = 
      let val ty' = substTyTy (map op|-> tyEnv) ty
          val anon_pkg_qid = ("",snd qid)
      in (NamedTy qid, ty') :: (NamedTy anon_pkg_qid,ty') :: tyEnv
      end

fun declare_hol_fn tyEnv ((_,name),params,ty,body) =
    let fun mk_hol_param (s,ty) = (s, mk_var(s,transTy tyEnv ty))
        val varE = map mk_hol_param params
        val param_vars = map snd varE
        val ety = Expected (transTy tyEnv ty)
        val pkgName = current_theory()
        val body_tm = transExp pkgName varE ety body
        val def_var = mk_var(name,
                             list_mk_fun (map type_of param_vars, type_of body_tm))
        val def_tm = mk_eq(list_mk_comb(def_var,param_vars),body_tm)
	val def = PURE_REWRITE_RULE [GSYM CONJ_ASSOC]
                           (new_definition(name^"_def", def_tm))
    in
       stdErr_print ("Defined function "^Lib.quote name^"\n")
     ; def
    end

fun declare_hol_term tyEnv (ConstDec (qid,ty,exp)) = declare_hol_fn tyEnv (qid,[],ty,exp)
  | declare_hol_term tyEnv (FnDec fninfo) = declare_hol_fn tyEnv fninfo;

fun underscore(a,b) = String.concat[a,"_",b];

fun mk_filter_spec (thyName,tyEnv,fn_defs) 
		   (FilterDec ((pkgName,fname), ports, (comment,prop))) = 
    let val outport = Lib.first (fn (_,_,dir,_) => (dir = "out")) ports
	val inport = Lib.first (fn (_,_,dir,_) => (dir = "in")) ports
        val iname = #1 inport
        val oname = #1 outport
        val ty = transTy tyEnv (#2 outport)
        val varIn = (iname,mk_var(iname,ty))
        val varOut = (oname,mk_var(oname,ty))
        val spec = transExp thyName [varIn,varOut] (Expected bool) prop
        val wf_message_thm = PURE_REWRITE_CONV fn_defs spec
        val array_forall_expanded = 
             wf_message_thm
               |> SIMP_RULE (srw_ss()) [splatTheory.fcp_every_thm]
               |> SIMP_RULE arith_ss 
                    [arithmeticTheory.BOUNDED_FORALL_THM, GSYM CONJ_ASSOC,GSYM DISJ_ASSOC]
    in
      save_thm (underscore(pkgName,fname), 
                array_forall_expanded)
    end
    handle e => raise wrap_exn "AADL" "mk_filter_spec" e;
;

val is_datatype = 
    same_const (prim_mk_const{Thy="bool",Name="DATATYPE"}) o rator o concl;

fun mk_aadl_defs thyName tyEnv (pkgName,(tydecs,tmdecs,filters)) =
    let val tyEnv' = rev_itlist declare_hol_type tydecs tyEnv
        val tydecls = List.filter (is_datatype o snd) (theorems thyName)
        val tmdecs' = topsort called_by tmdecs
        val fn_defs = MiscLib.mapfilter (declare_hol_term tyEnv') tmdecs'
        val info = (thyName,tyEnv',fn_defs)
        val filter_specs = map (mk_filter_spec info) filters
    in	  
      (tyEnv', map snd tydecls, fn_defs, filter_specs)
    end;

fun pkgs2hol thyName list =
 let fun iter [] acc = acc
       | iter (pkg::t) (tyE,tyD,tmD,fS) =
          let val (tyEnv',tydefs,fndefs,filtspecs) = mk_aadl_defs thyName tyE pkg
          in iter t (tyEnv', tydefs@tyD, fndefs@tmD, filtspecs@fS)
          end
 in iter list ([],[],[],[])
 end;


end
