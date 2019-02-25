(*===========================================================================*)
(* Input format for a SPLAT system supporting network messages               *)
(*===========================================================================*)

structure splatLib :> splatLib =
struct

open HolKernel Parse boolLib bossLib MiscLib;

open regexpSyntax pred_setSyntax Regexp_Type
     arithmeticTheory listTheory stringTheory
     charsetTheory regexpTheory splatTheory
     pred_setLib numLib stringLib regexpLib;

val ERR = Feedback.mk_HOL_ERR "splatLib";

type filter_info
   = {regexp : Regexp_Type.regexp,
      encode_def : thm, 
      decode_def : thm,
      inversion : term,
      correctness : term,
      implicit_constraints : thm option};

     
structure Finmap = Redblackmap;

type ('a, 'b) fmap = ('a, 'b) Finmap.dict;

(*---------------------------------------------------------------------------*)
(* Width of a message field, in bits or in word8s                            *)
(*---------------------------------------------------------------------------*)

datatype width
  = BITWIDTH of int
  | BYTEWIDTH of int

(*---------------------------------------------------------------------------*)
(* Endianess, signedness for binary numbers                                  *)
(*---------------------------------------------------------------------------*)

datatype endian = MSB | LSB;

datatype sign
   = SIGNED of endian 
   | UNSIGNED of endian;

(*---------------------------------------------------------------------------*)
(* Digits used for text representation of numbers                            *)
(*---------------------------------------------------------------------------*)

datatype textrep = BIT | OCT | HEX | DEC;

(*---------------------------------------------------------------------------*)
(* A number (or interval) can be formatted in binary or text. If in binary,  *)
(* considerations of sign and endianess are important.                       *)
(*---------------------------------------------------------------------------*)

datatype format
  = BINARY of sign * width
  | TEXT of textrep * width
  | ENUM of hol_type;

(*---------------------------------------------------------------------------*)
(* Tedious construction of comparison functions for finite map keys          *)
(*---------------------------------------------------------------------------*)

fun compare_width (BITWIDTH i, BITWIDTH j) = Int.compare(i,j)
  | compare_width (BYTEWIDTH i, BYTEWIDTH j) = Int.compare(i,j)
  | compare_width (BYTEWIDTH _, BITWIDTH _) = LESS
  | compare_width (BITWIDTH _, BYTEWIDTH _) = GREATER;

fun compare_sign (SIGNED LSB, SIGNED MSB) = LESS
  | compare_sign (SIGNED MSB, SIGNED LSB) = GREATER
  | compare_sign (UNSIGNED LSB, UNSIGNED MSB) = LESS
  | compare_sign (UNSIGNED MSB, UNSIGNED LSB) = GREATER
  | compare_sign (UNSIGNED _, SIGNED _) = LESS
  | compare_sign (SIGNED _, UNSIGNED _) = GREATER
  | compare_sign otherwise = EQUAL;

val compare_textrep =
 let fun ord BIT = 0
       | ord OCT = 1
       | ord HEX = 2
       | ord DEC = 3
 in 
   fn (tr1,tr2) => Int.compare (ord tr1, ord tr2)
end;

fun compare_format (nf1,nf2) =
    case (nf1,nf2)
     of (BINARY (s1,w1), BINARY(s2,w2)) =>
          (case compare_sign(s1,s2)
	    of EQUAL => compare_width(w1,w2)
	     | other => other)
      | (TEXT (tr1,w1),TEXT (tr2,w2)) =>
          (case compare_textrep(tr1,tr2)
	    of EQUAL => compare_width(w1,w2)
	     | other => other)
      | (ENUM ty1, ENUM ty2)  => Type.compare (ty1,ty2)
      | (BINARY _, _) => LESS
      | (TEXT _,BINARY _) => GREATER
      | (TEXT _,ENUM _) => LESS
      | (ENUM _, _) => GREATER
;
  
(*---------------------------------------------------------------------------*)
(* Possible field values in a message                                        *)
(*---------------------------------------------------------------------------*)

datatype fieldval
  = Num of Arbint.int * format 
  | Interval of Arbint.int * Arbint.int * format
  | Char of char * format
  | Enumset of string * term list * format
  | Raw of string * width (* uninterpreted sequence of bytes *)
  | Padding of width
  | Packed of fieldval list;

fun format_of field =
 case field
  of Num(_,f) => f
   | Interval(_,_,f) => f
   | Char (_,f) => f
   | Enumset(tyname,constrs,f) => ENUM (mk_type(tyname,[]))
   | Raw _ => raise ERR "format_of" "Raw not handled"
   | Padding _ => raise ERR "format_of" "Padding not handled"
   | Packed _ => raise ERR "format_of" "Packed not handled"

(*---------------------------------------------------------------------------*)
(* Width of (un)signed number in bits and bytes                              *)
(*---------------------------------------------------------------------------*)

fun width b =
 let val base = Arbint.fromInt b
     fun W (i:Arbint.int) = 
        if Arbint.<(i,base) then (1:Int.int) else 1 + W (Arbint.div(i,base))
 in W 
 end;

val ubit_width = width 2;
val ubyte_width = width 256;

fun sbit_width i =
 let open Arbint
     fun W bits =
       let val N = twoE (Int.-(bits,1))
       in if Arbint.~(N) <= i andalso i < N then bits else W (Int.+(bits,1))
       end
 in W 0
 end;

val bits2bytes = 
 let fun roundup (q,r) = q + (if r > 0 then 1 else 0)
 in fn n => roundup(n div 8,n mod 8)
 end

fun sbyte_width i = bits2bytes(sbit_width i)


fun fieldval_width fval =
 case format_of fval
  of BINARY (_,width) => width
   | TEXT   (_,width) => width
   | ENUM   _ => BYTEWIDTH 1;

fun fieldval_byte_width fval =
 case fieldval_width fval
  of BYTEWIDTH i => i
   | BITWIDTH i => bits2bytes i;

fun fieldval_bit_width fval =
 case fieldval_width fval
  of BYTEWIDTH i => 8 * i 
   | BITWIDTH i => i;

fun dtrans LSB = Regexp_Type.LSB
  | dtrans MSB = Regexp_Type.MSB;

fun a2i ai = 
    Option.valOf (IntInf.fromString (Arbint.toString ai));
    
type enumMap = (string, (term*int) list) fmap;

(*---------------------------------------------------------------------------*)
(* An enum type needs a formatting map from the enum constants to numbers.   *)
(* The domain of the map is the name of the enum type.                       *)
(*---------------------------------------------------------------------------*)

val base_enumMap : enumMap =
  let open boolSyntax
  in 
    Finmap.fromList String.compare [("bool", [(F,0), (T,1)])]
  end;

local
    val emap = ref base_enumMap
in 
 fun the_enumMap() = !emap
 fun insert_enum (k,v) =
     emap := Finmap.insert (the_enumMap(), k,v)
end

fun insert_enum_type (tyname,cnames) =
  let val ty = mk_type(tyname,[])
      val constrs = map (fn s => mk_const(s,ty)) cnames
      val econstrs = map swap (enumerate 0 constrs)
  in 
     insert_enum(tyname,econstrs)
  end

fun fieldval_to_tree enumMap fv =
 case fv
  of Num (i,BINARY(UNSIGNED dir,BYTEWIDTH bw)) =>
       Regexp_Type.Const(a2i i, dtrans dir)
   | Num (i,BINARY(SIGNED dir,BYTEWIDTH bw)) => 
       Regexp_Type.Const(a2i i, dtrans dir)
   | Num otherwise => raise ERR "fieldval_to_tree" "Unexpected Num subcase"
   | Interval(i,j,BINARY(UNSIGNED dir,BYTEWIDTH bw)) =>
       Regexp_Type.Interval (a2i i, a2i j, dtrans dir)
   | Interval(i,j,BINARY(SIGNED dir,BYTEWIDTH bw)) =>
       Regexp_Type.Interval (a2i i, a2i j, dtrans dir)
   | Interval otherwise => raise ERR "fieldval_to_tree" "Unexpected Interval subcase"
   | Enumset (tyname,elts,ENUM ty) =>
      (case Finmap.peek (enumMap,tyname)
        of SOME clist =>
            let val ilist = List.map (fn e => op_assoc aconv e clist) elts
                val chars = List.map Char.chr ilist
            in
	     Regexp_Type.Cset (Regexp_Type.charset_of chars)
	    end
      | NONE => raise ERR "fieldval_to_tree" 
                    ("enumerated type "^Lib.quote tyname^" not registered"))
   | Enumset otherwise => raise ERR "fieldval_to_tree" "Unexpected Enumset format"
   | Char _ => raise ERR "fieldval_to_tree" "Char not implemented"
   | Raw _ => raise ERR "fieldval_to_tree" "Raw not implemented"
   | Padding _ => raise ERR "fieldval_to_tree" "Padding not implemented"
   | Packed _ => raise ERR "fieldval_to_tree" "Packed not implemented"
;

(*---------------------------------------------------------------------------*)
(* A map from formats (especially format) to encoders/decoders               *)
(*---------------------------------------------------------------------------*)

type coding = {enc : term, 
               dec : term,
               enc_def : thm, 
               dec_def : thm,
               dec_enc : thm};

type codingMap = (format, coding) fmap;

val base_codingMap = 
  Finmap.fromList compare_format
   [ (BINARY(UNSIGNED LSB,BYTEWIDTH 1),
        {enc = ``splat$enc 1``, 
         dec = ``splat$dec``,
         enc_def = splatTheory.enc_def,
         dec_def = splatTheory.dec_def,
         dec_enc = splatTheory.dec_enc}),
     (BINARY(UNSIGNED LSB,BYTEWIDTH 2),
        {enc = ``splat$enc 2``, 
         dec = ``splat$dec``,
         enc_def = splatTheory.enc_def,
         dec_def = splatTheory.dec_def,
         dec_enc = splatTheory.dec_enc}),
     (BINARY(SIGNED LSB,BYTEWIDTH 1),
        {enc = ``splat$enci 1``, 
         dec = ``splat$deci 1``,
         enc_def = splatTheory.enci_def,
         dec_def = splatTheory.deci_def,
         dec_enc = el 1 (CONJUNCTS splatTheory.deci_encis)}),
     (BINARY(SIGNED LSB,BYTEWIDTH 2),
        {enc = ``splat$enci 2``, 
         dec = ``splat$deci 2``,
         enc_def = splatTheory.enci_def,
         dec_def = splatTheory.deci_def,
         dec_enc = el 2 (CONJUNCTS splatTheory.deci_encis)}),
     (BINARY(SIGNED LSB,BYTEWIDTH 8),
        {enc = ``splat$enci 8``, 
         dec = ``splat$deci 8``,
         enc_def = splatTheory.enci_def,
         dec_def = splatTheory.deci_def,
         dec_enc = el 8 (CONJUNCTS splatTheory.deci_encis)}),
     (ENUM Type.bool,
        {enc = ``splat$enc_bool``, 
         dec = ``splat$dec_bool``,
         enc_def = splatTheory.enc_bool_def,
         dec_def = splatTheory.dec_bool_def,
         dec_enc = splatTheory.dec_enc_bool})
   ];

local
    val codingmap = ref base_codingMap
in 
 fun the_codingMap() = !codingmap
 fun insert_coding (k,v) =
     codingmap := Finmap.insert (the_codingMap(), k,v)
end

fun define_enum_encoding ety =
 let val etyName = fst(dest_type ety)
     val clist = TypeBase.constructors_of ety
     val eclist = map swap (enumerate 0 clist)
     val teclist = map (I##numSyntax.term_of_int) eclist
     val ename = "num_of_"^etyName
     val dname = etyName^"_of_num"
     val efnvar = mk_var(ename,ety --> numSyntax.num)
     val dfnvar = mk_var(dname,numSyntax.num --> ety)
     fun enc_clause (constr,itm) = mk_eq(mk_comb(efnvar,constr),itm)
     val enc_clauses = map enc_clause teclist
     val num_of_enum = TotalDefn.Define `^(list_mk_conj enc_clauses)`
     val argvar = mk_var("n",numSyntax.num)
     fun cond_of (c,n) x = mk_cond(mk_eq(argvar,n),c,x)
     val (L,(b,_)) = front_last teclist
     val body = itlist cond_of L b
     val enum_of_num = TotalDefn.Define `^(mk_comb(dfnvar,argvar)) = ^body`
     val n_of_e_const = mk_const(dest_var(efnvar))
     val e_of_n_const = mk_const(dest_var(dfnvar))
     val enumvar = mk_var("c",ety)
     val stringvar = mk_var("s",stringSyntax.string_ty)
     val encoderName = "enc_"^etyName
     val decoderName = "dec_"^etyName
     val encvar = mk_var(encoderName,ety --> stringSyntax.string_ty)
     val decvar = mk_var(decoderName,stringSyntax.string_ty --> ety)
     val encoder = TotalDefn.Define `^(mk_comb(encvar,enumvar)) = enc 1 (^n_of_e_const ^enumvar)`
     val decoder = TotalDefn.Define `^(mk_comb(decvar,stringvar)) = ^e_of_n_const (dec ^stringvar)`
     val encoder_const = mk_const(dest_var(encvar))
     val decoder_const = mk_const(dest_var(decvar))
     val inversion_goal = 
       mk_forall(enumvar, 
		 mk_eq(mk_comb(decoder_const,mk_comb(encoder_const, enumvar)),
		       enumvar))
     val inversion = 
       prove (inversion_goal, 
           Cases >> rw_tac std_ss 
                      [encoder, decoder,splatTheory.dec_enc,
                       num_of_enum, enum_of_num])
     val coding = 
        {enc = encoder_const,
         dec = decoder_const,
         enc_def = encoder,
         dec_def = decoder,
         dec_enc = inversion}
 in
   insert_enum_type (etyName,map (fst o dest_const) clist)
 ; insert_coding (ENUM ety,coding)
 end
 handle e => raise wrap_exn "splatLib" "define_enum_encoding" e

(*---------------------------------------------------------------------------*)
(* A record description has the fields of the record, along with a predicate *)
(* specifying the subset of records that are well-formed.                    *)
(*---------------------------------------------------------------------------*)

type precord = {fields : (string * fieldval) list, pred : term};

fun is_comparison tm =
 let val (opr,args) = strip_comb tm
  in mem opr [numSyntax.less_tm,numSyntax.leq_tm,numSyntax.greater_tm,
              numSyntax.geq_tm, intSyntax.less_tm,intSyntax.leq_tm,
              intSyntax.great_tm,intSyntax.geq_tm]
  end	

fun mk_set_lr list =
 let fun mk_set [] acc = rev acc
       | mk_set (h::t) acc =
         if op_mem aconv h acc then mk_set t acc else mk_set t (h::acc)
 in mk_set list []
 end

fun tdrop i list = (List.take(list,i),List.drop(list,i))

fun take_list [] [] = []
  | take_list [] _ = raise ERR "take_list" "non-empty list remains"
  | take_list (i::t) elts = 
    let val (h,elts') = tdrop i elts
    in h::take_list t elts'
    end
    handle _ => raise ERR "take_list" "";

(*---------------------------------------------------------------------------*)
(* Expand out all possible nested record projections from a variable of      *)
(* record type. The sequence of paths is the order in which fields will be   *)
(* written to the format.                                                    *)
(*---------------------------------------------------------------------------*)

fun all_paths recdvar =
 let val recdty = type_of recdvar
     val {Thy,Tyop=rtyname,Args} = dest_thy_type recdty
     fun projfn_of th = fst(strip_comb(lhs(snd(strip_forall (concl th)))))
     fun grow tm =
       let val ty = type_of tm
       in if TypeBase.is_record_type ty
	  then let val pfns = map projfn_of (TypeBase.accessors_of ty)
	       in map (Lib.C (curry mk_comb) tm) pfns
	       end
          else 
          if fcpSyntax.is_cart_type ty then
              let val (bty,dty) = fcpSyntax.dest_cart_type ty
		  val d = fcpSyntax.dest_int_numeric_type dty
                  val copies = map numSyntax.term_of_int (upto 0 (d-1))
                  fun Aproj n = fcpSyntax.mk_fcp_index(tm,n)
              in
                 map Aproj copies
              end
	  else [tm]
       end
     fun genpaths paths =
       let val paths' = flatten (map grow paths)
       in if paths' = paths then paths else genpaths paths'
       end
 in
    genpaths [recdvar]
 end;

(*---------------------------------------------------------------------------*)
(* mk_interval is a convoluted mapping (to eventually be made simpler by     *)
(* merging fieldval and Regexp_Type.tree) fron intervals to regexps:         *)
(*                                                                           *)
(*       (lo,hi) : term * term                                               *)
(*	  -->                                                                *)
(*	  Interval(lo,hi,format)  ; splatLib.fieldval                        *)
(*	  -->                                                                *)
(*	  Interval(lo,hi,dir)     ; Regexp_Type.tree                         *)
(*	  -->                                                                *)
(*        regexp                  ; Regexp_Type.tree_to_regexp               *)
(*	  -->                                                                *)
(*	  term                    ; regexpSyntax.mk_regexp                   *)
(*                                                                           *)
(*---------------------------------------------------------------------------*)

fun filter_correctness thm =
 let val (wfpred_app,expansion) = dest_eq(concl thm)
     val (wfpred,recdvar) = dest_comb wfpred_app
     val (wfpred_name,wfpred_ty) = dest_const wfpred
     val recdty = type_of recdvar
     val {Thy,Tyop=rtyname,Args} = dest_thy_type recdty
     val constraints = strip_conj expansion
     fun has_recd_var t = mem recdvar (free_vars t)
     val allprojs = all_paths recdvar
     fun proj_of t =
       filter has_recd_var
	(if is_comparison t 
        then snd (strip_comb t)
        else if is_disj t  (* disjunction of equalities *)
            then flatten (map (snd o strip_comb) (strip_disj t))
            else if is_eq t then snd (strip_comb t)
                 else raise ERR "proj_of" 
                   "expected a disjunction of equalities or an arithmetic inequality")
     val projs = mk_set_lr (flatten (map proj_of constraints))
     val omitted_projs = set_diff allprojs projs
     fun in_group tmlist tm = (tm, filter (Lib.can (find_term (aconv tm))) tmlist)
     val allgroups = map (in_group constraints) allprojs 
     val groups = map (in_group constraints) projs 
     val groups' =  (* unconstrained fields get explicitly constrained *)
         if null omitted_projs
	 then groups
	 else
         let fun unconstrained proj = (* Done for integers and enums currently *)
              let val ty = type_of proj
                  open intSyntax
              in if ty = int_ty
                    then [mk_leq(term_of_int (Arbint.~(twoE 63)),proj),
                          mk_less(proj,term_of_int (twoE 63))]
	         else case Finmap.peek (the_enumMap(),fst(dest_type (type_of proj)))
                       of NONE => raise ERR "mk_correctness_goals" 
                                ("following field is not in the_enumMap(): "^term_to_string proj)
		        | SOME plist => [list_mk_disj (map (curry mk_eq proj o fst) plist)]
              end
             fun supplement (proj,[]) = (proj,unconstrained proj)
               | supplement other = other
	 in 
            map supplement allgroups
         end

     (* Add implicit constraints to the wfpred *)

     val implicit_constraints = List.mapPartial (C assoc1 groups') omitted_projs
     val (wfpred_app',iconstraints_opt) = 
	 if null implicit_constraints
	 then (wfpred_app,NONE)
	 else 
         let val implicit_constraints_tm =
                 list_mk_conj (map (list_mk_conj o snd) implicit_constraints)
	     val iconstr_name = wfpred_name^"_implicit_constraints"
	     val iconstr_app = mk_comb(mk_var(iconstr_name,wfpred_ty),recdvar)
	     val iconstr_def_tm = mk_eq(iconstr_app, implicit_constraints_tm)
	     val implicit_constraints_def = TotalDefn.Define `^iconstr_def_tm`
	     val implicit_constraints_const = 
                  mk_const(dest_var(fst(dest_comb iconstr_app)))
         in
            (mk_conj(wfpred_app,mk_comb(implicit_constraints_const,recdvar)),
             SOME implicit_constraints_def)
         end

     (* map constraints to an interval. The (lo,hi) pair denotes the inclusive
        interval {i | lo <= i <= hi} so there is some fiddling to translate
        all relations to <=.
     *)
     fun mk_interval ctr =  (* elements of c expected to have form relop t1 t2 *)
      let fun elim_gtr tm = (* elim > and >= *)
            case strip_comb tm
	      of (rel,[a,b]) =>
                  if rel = numSyntax.greater_tm
		    then (numSyntax.less_tm,b,a) else 
                  if rel = numSyntax.geq_tm 
		    then (numSyntax.leq_tm,b,a) else 
                  if rel = intSyntax.great_tm
		    then (intSyntax.less_tm,b,a) else 
                  if rel = intSyntax.geq_tm 
		    then (intSyntax.leq_tm,b,a) 
                  else if op_mem same_const rel
                          [intSyntax.leq_tm,numSyntax.leq_tm,
                           intSyntax.less_tm,numSyntax.less_tm]
                     then (rel,a,b)
                  else raise ERR "mk_interval" "unknown numeric relation"
	       | other => raise ERR "mk_interval" "expected term of form `relop a b`"
          val ctr' = map elim_gtr ctr
          fun sort [c1 as (_,a,b), c2 as (_,c,d)] = 
              let val fva = free_vars a
                  val fvb = free_vars b
                  val fvc = free_vars c
                  val fvd = free_vars d
              in 
                 if mem recdvar fvb andalso mem recdvar fvc andalso aconv b c
                   then (c1,c2)
	         else
	         if mem recdvar fvd andalso mem recdvar fva andalso aconv a d
                   then (c2,c1)
	         else raise ERR "mk_interval(sort)" "unexpected format"
              end
            | sort otherwise = raise ERR "mk_interval(sort)" "unexpected format"
          val ((rel1,lo_tm,_),(rel2,_,hi_tm)) = sort ctr'
	  fun dest_literal t = 
             (if type_of t = numSyntax.num
	        then Arbint.fromNat o numSyntax.dest_numeral
                else intSyntax.int_of_term) t
          val lo = dest_literal lo_tm
          val hi = dest_literal hi_tm
          val lo' = if op_mem same_const rel1 [numSyntax.less_tm,intSyntax.less_tm]
                      then Arbint.+(lo, Arbint.one) else lo
          val hi' = if op_mem same_const rel2 [numSyntax.less_tm,intSyntax.less_tm]
                      then Arbint.-(hi,Arbint.one) else hi
          val ctype = type_of lo_tm
	  val sign = if ctype = numSyntax.num then UNSIGNED LSB else SIGNED LSB
          val byte_width = if ctype = numSyntax.num then ubyte_width else sbyte_width
	  val width = BYTEWIDTH (Int.max(byte_width lo', byte_width hi'))
      in  
        Interval(lo',hi', BINARY (sign,width))
      end

     fun mk_enumset [g] =   (* Should be extended to finite sets of numbers *)
         let val eqns = strip_disj g
             val constlike = null o free_vars
             fun elt_of eqn = 
                let val (l,r) = dest_eq eqn 
                in if constlike l then l else 
                   if constlike r then r else 
                   raise ERR "mk_enumset (elt_of)" "expected a projection"
		end
	     val elts = map elt_of eqns
             val _ = if null elts then raise ERR "mk_enumset" "no elements" else ()
	     val enumty = type_of (hd elts)
	     val etyname = fst(dest_type enumty)
             val _ = if 256 < length (TypeBase.constructors_of enumty) 
                       then raise ERR "mk_enumset" 
                         ("enumerated type "^Lib.quote etyname^" has > 256 elements: too many") 
                       else ()
          in Enumset (etyname,elts,ENUM enumty)
	  end
       | mk_enumset other = raise ERR "mk_enumset" "expected a disjunction of equations"

				  
     fun mk_fieldval x = mk_interval (snd x) handle HOL_ERR _ => mk_enumset (snd x);

     val fvals = map mk_fieldval groups'
     val fwidths = map fieldval_byte_width fvals

     (* Compute regexps for the fields *)

     val treevals = List.map (fieldval_to_tree (the_enumMap())) fvals
     val regexps = map Regexp_Type.tree_to_regexp treevals
     val the_regexp = Regexp_Match.normalize (catlist regexps)
     val the_regexp_tm = regexpSyntax.mk_regexp the_regexp
     
     val codings = List.map (curry Finmap.find (the_codingMap()) o format_of) fvals

     (* Define encoder *)
     val encs = map #enc codings
     val encode_fields = map mk_comb (zip encs allprojs)
     val encode_fields_list = listSyntax.mk_list(encode_fields,stringLib.string_ty)
     val encodeFn_var = mk_var("encode_"^rtyname,recdty --> stringLib.string_ty)
     val encodeFn_lhs = mk_comb(encodeFn_var,recdvar)
     val encodeFn_rhs = listSyntax.mk_flat encode_fields_list
     val encodeFn_def_term = mk_eq(encodeFn_lhs,encodeFn_rhs)
     val encodeFn_def = TotalDefn.Define `^encodeFn_def_term`
     val encodeFn = mk_const(dest_var encodeFn_var)

     val regexp_lang_tm = 
       mk_thy_const{Name = "regexp_lang", Thy = "regexp", 
          Ty = regexpSyntax.regexp_ty --> stringSyntax.string_ty --> Type.bool}

     val correctness_goal = mk_forall(recdvar,
        mk_eq(wfpred_app',
          pred_setSyntax.mk_in
            (mk_comb(encodeFn,recdvar),
	     mk_comb(regexp_lang_tm,the_regexp_tm))))

     (* Define decoder *)
     val vars = map (fn i => mk_var("v"^Int.toString i, stringSyntax.char_ty))
                    (upto 0 (List.foldl (op+) 0 fwidths - 1))
     val decs = map #dec codings
     val chunked_vars = take_list fwidths vars
     fun enlist vlist = listSyntax.mk_list(vlist,stringSyntax.char_ty)
     val chunked_vars_tms = map enlist chunked_vars
     val rhs_info = zip allprojs (map mk_comb (zip decs chunked_vars_tms))
     fun rev_strip t b acc = 
         if is_var t then (rev acc, b) else
         if fcpSyntax.is_fcp_index t then 
          let val (A,i) = fcpSyntax.dest_fcp_index t
              val Aty = type_of A
              val Avar = mk_var("A",Aty)
              val indexOp = mk_abs(Avar,fcpSyntax.mk_fcp_index(Avar,i))
          in rev_strip A b (indexOp::acc)
	  end
         else 
         let val (M,N) = dest_comb t
	 in rev_strip N b (M::acc)
	 end
     fun booger (p,x) = rev_strip p x []
     val rhs_info' = map booger rhs_info

     fun parts [] = []
       | parts ((p as ([_],v))::t) = [p]::parts t
       | parts ((h as (segs1,_))::t) = 
	 let fun P (segs2,_) = 
                   if null segs1 orelse null segs2 then false else tl segs1 = tl segs2
             val (L1,L2) = Lib.partition P (h::t)
	 in L1 :: parts L2
	 end

     fun mk_recd_app rty args = 
       case TypeBase.constructors_of rty
        of [constr] => list_mk_comb (constr,args)
         | otherwise => raise ERR "mk_recd_app" "expected to find a record constructor"
     
     fun maybe_shrink [] = raise ERR "maybe_shrink" "empty partition"
       | maybe_shrink (partn as [([_],_)]) = partn  (* fully shrunk *)
       | maybe_shrink (partn as (apath::_)) = 
          let val segs = fst apath
              val proj_ty = type_of (hd segs)
              val recdty = dom proj_ty
              val args = map snd partn
              val recd_app = mk_recd_app recdty args
       in 
           [(tl segs,recd_app)]
       end
       handle e => raise wrap_exn "splatLib" "maybe_shrink" e

     fun mk_recd paths =
      if Lib.all (equal 1 o length o fst) paths
         then mk_recd_app recdty (map snd paths)
      else 
      let val partns = parts paths
          val partns' = map maybe_shrink partns
	  val paths' = flatten partns'
      in
          if length paths' < length paths
	  then mk_recd paths'
	  else 
            if paths' = paths
            then raise ERR "mk_recd" "irreducible path"
          else if length paths' = length paths
            then raise ERR "mk_recd" "length of paths not reduced"
            else raise ERR "mk_recd" "length of some path(s) increased"
      end

     val decodeFn_name = "decode_"^rtyname
     val decodeFn_ty = stringSyntax.string_ty --> optionSyntax.mk_option recdty
     val decodeFn_var = mk_var(decodeFn_name,decodeFn_ty)
     val fvar = mk_var("s",stringSyntax.string_ty)
     val decodeFn_lhs = mk_comb(decodeFn_var, fvar)

     val pat = listSyntax.mk_list(vars,stringSyntax.char_ty)
     val valid_rhs = optionSyntax.mk_some(mk_recd rhs_info')
     val rules = [(pat,valid_rhs), (``otherwise:string``,optionSyntax.mk_none recdty)]
     val rhs = TypeBase.mk_pattern_fn rules
     val decodeFn_rhs = Term.beta_conv (mk_comb(rhs,fvar))
     val decodeFn_def = Define `^(mk_eq(decodeFn_lhs,decodeFn_rhs))`
     val decodeFn = mk_const(decodeFn_name,decodeFn_ty)

    val inversion_goal = mk_forall(recdvar,
        mk_imp(wfpred_app',
               mk_eq(mk_comb(decodeFn,mk_comb(encodeFn,recdvar)),
                     optionSyntax.mk_some recdvar)))
 in
     {regexp      = the_regexp,
      encode_def  = encodeFn_def,
      decode_def  = decodeFn_def,
      inversion   = inversion_goal,
      correctness = correctness_goal,
      implicit_constraints = iconstraints_opt}
 end
 handle e => raise wrap_exn "splatLib" "mk_correctness_goals" e;

(*---------------------------------------------------------------------------*)
(* Proves goals of the form                                                  *)
(*                                                                           *)
(*   s IN regexp_lang (Chset (Charset a b c d))                              *)
(*     <=>                                                                   *)
(*   (LENGTH s = 1) /\ dec s <= K                                            *)
(*---------------------------------------------------------------------------*)

val IN_CHARSET_NUM_TAC =
 rw_tac (list_ss ++ regexpLib.charset_conv_ss) [EQ_IMP_THM,strlen_eq,LE_LT1]
  >> TRY EVAL_TAC 
  >> rule_assum_tac 
        (SIMP_RULE list_ss [dec_def, numposrepTheory.l2n_def, ord_mod_256])
  >> pop_assum mp_tac
  >> Q.SPEC_TAC (`ORD c`, `n`)
  >> REPEAT (CONV_TAC (numLib.BOUNDED_FORALL_CONV EVAL))
  >> rw_tac bool_ss [];


(*---------------------------------------------------------------------------*)
(* prove_constraints                                                         *)
(*      : enumMap * codingMap                                                *)
(*         -> record                                                         *)
(*         -> {coders : coding,                                              *)
(*             regexp : regexp,                                              *)
(*             correctness : thm}                                            *)
(*---------------------------------------------------------------------------*)

fun prove_constraints (enumE,codingE) recd =
    raise ERR "prove_constraints" "not implemented"

end
