(*===========================================================================*)
(* Input format for a SPLAT system supporting network messages               *)
(*===========================================================================*)

structure splatLib :> splatLib =
struct

open HolKernel Parse boolLib bossLib;

open regexpSyntax pred_setSyntax Regexp_Type
     arithmeticTheory listTheory stringTheory
     charsetTheory regexpTheory splatTheory
     pred_setLib numLib stringLib regexpLib;

val ERR = Feedback.mk_HOL_ERR "splatLib";

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
  | Bool of bool * format
  | Char of char * format
  | Enumset of string * term list * format
  | Raw of string * width (* uninterpreted sequence of bytes *)
  | Padding of width
  | Packed of fieldval list;

fun format_of field =
 case field
  of Num(_,f) => f
   | Interval(_,_,f) => f
   | Bool(_,f) => f
   | Char (_,f) => f
   | Enumset(tyname,constrs,f) => ENUM (mk_type(tyname,[]))
   | Raw _ => raise ERR "format_of" "Raw not handled"
   | Padding _ => raise ERR "format_of" "Padding not handled"
   | Packed _ => raise ERR "format_of" "Packed not handled"

(*---------------------------------------------------------------------------*)
(* Width of (un)signed number in bits and bytes                              *)
(*---------------------------------------------------------------------------*)

fun exp n e = if Int.<(e,1) then Arbint.one else Arbint.*(n,exp n (Int.-(e,1)));

val bits2bytes = 
 let fun roundup (q,r) = q + (if r > 0 then 1 else 0);
 in fn n => roundup(n div 8,n mod 8)
 end

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
       let val N = exp Arbint.two (Int.-(bits,1))
       in if Arbint.~(N) <= i andalso i < N then bits else W (Int.+(bits,1))
       end
 in W 0
 end;

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
   | Bool _ => raise ERR "fieldval_to_tree" "Bool not implemented"
   | Char _ => raise ERR "fieldval_to_tree" "Char not implemented"
   | Raw _ => raise ERR "fieldval_to_tree" "Raw not implemented"
   | Padding _ =>raise ERR "fieldval_to_tree" "Padding not implemented"
   | Packed _ => raise ERR "fieldval_to_tree" "Packed not implemented"
;

fun catlist [] = Regexp_Type.EPSILON
  | catlist [x] = x
  | catlist (h::t) = Regexp_Type.Cat (h,catlist t);

(*---------------------------------------------------------------------------*)
(* A map from formats (especially format) to encoders/decoders            *)
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
         dec_enc = splatTheory.deci_enci}),
     (BINARY(SIGNED LSB,BYTEWIDTH 2),
        {enc = ``splat$enci 2``, 
         dec = ``splat$deci 2``,
         enc_def = splatTheory.enci_def,
         dec_def = splatTheory.deci_def,
         dec_enc = splatTheory.deci_enci}),
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
  | take_list (i::t) elts = 
    let val (h,elts') = tdrop i elts
    in h::take_list t elts'
    end
    handle _ => raise ERR "take_list" "";

type decls = 
  (* pkgName *)  string * 
  (* enums *)    (string * string list) list *
  (* recds *)    (string * (string * AST.ty) list) list *
  (* fns *)      thm list;

fun mk_correctness (info as (pkgName,enums,recds,fn_defs)) thm =
 let val (wfpred_app,expansion) = dest_eq(concl thm)
     val (wfpred,recdvar) = dest_comb wfpred_app
     val recdty = type_of recdvar
     val {Thy,Tyop=rtyname,Args} = dest_thy_type recdty
     val constraints = strip_conj expansion
     fun has_recd_var t = mem recdvar (free_vars t)    
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
     fun in_group tmlist tm = filter (Lib.can (find_term (aconv tm))) tmlist
     val groups = map (in_group constraints) projs  (* precise enough? *)
     val _ = if HOLset.equal
                 (HOLset.addList(Term.empty_tmset,flatten groups),
                  HOLset.addList(Term.empty_tmset,constraints))
              then ()
	      else raise ERR "mk_correctness" 
                 "lossy step in building field-specific constraints"

     (* A convoluted mapping (to eventually be made simpler by merging Splat.fieldval
        and Regexp_Type.tree):

          (lo,hi) : term * term
	  -->
	  Interval(lo,hi,format)  ; splatLib.fieldval
	  -->
	  Interval(lo,hi,dir)        ; Regexp_Type.tree
	  --> 
          regexp                     ; Regexp_Type.tree_to_regexp
	  -->
	  term                       ; regexpSyntax.mk_regexp

        From splatLib.fieldval, we generate necessary proof infrastructure
     *)
     fun mk_interval g =  (* elements of g have form relop t1 t2 *)
      let fun norm tm =
            case strip_comb tm
	      of (rel,[a,b]) =>
                  if rel = numSyntax.greater_tm
		    then list_mk_comb(numSyntax.less_tm,[b,a]) else 
                  if rel = numSyntax.geq_tm 
		    then list_mk_comb(numSyntax.leq_tm,[b,a]) else 
                  if rel = intSyntax.great_tm
		    then list_mk_comb(intSyntax.less_tm,[b,a]) else 
                  if rel = intSyntax.geq_tm 
		    then list_mk_comb(intSyntax.leq_tm,[b,a]) 
                  else tm
	       | other => raise ERR "mk_interval" "expected term of form `relop a b`"
          val blarg = rand o rator  (* binary left arg *)
          val brarg = rand          (* binary right arg *)
          fun sort [c] =
                 if mem recdvar (free_vars (rand c)) then 
                   let val hi = blarg c
		       val hity = type_of hi
		       val lo = if hity = numSyntax.num
		                 then numSyntax.zero_tm else
				if hity = intSyntax.int_ty
		                 then intSyntax.zero_tm else
				raise ERR "mk_interval (sort)" "expected numeric type"
		    in (lo,hi)
                    end
                 else raise ERR "mk_interval" "badly formed singleton interval"
            | sort [c1,c2] = 
	       if mem recdvar (free_vars (brarg c1)) andalso 
                  mem recdvar (free_vars (blarg c2))
               then (blarg c1,brarg c2)
	       else
	       if mem recdvar (free_vars (brarg c2)) andalso 
                  mem recdvar (free_vars (blarg c1))
               then (blarg c2, brarg c1)
	       else raise ERR "mk_interval(sort)" "unexpected format"
            | sort otherwise = raise ERR "mk_interval(sort)" "unexpected format"
          val (lo_tm,hi_tm) = sort (map norm g)
	  val dest_literal = 
             if type_of lo_tm = numSyntax.num
	        then Arbint.fromNat o numSyntax.dest_numeral
                else intSyntax.int_of_term
          val lo = dest_literal lo_tm
          val hi = dest_literal hi_tm
	  val sign = SIGNED LSB (* Temporary! *)
              (* if Arbint.<(lo,Arbint.zero)
	          then SIGNED LSB 
                  else UNSIGNED LSB  (* making LSB the default *)
              *)
          fun byte_width i =
              if Arbint.<(i, Arbint.zero) then sbyte_width i else ubyte_width i
	  val width = BYTEWIDTH (Int.max(byte_width lo, byte_width hi))
      in  
        Interval(lo,hi, BINARY (sign,width))
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
				   
     fun mk_fieldval x = mk_interval x handle HOL_ERR _ => mk_enumset x;

     val fvals = map mk_fieldval groups
     val fwidths = map fieldval_byte_width fvals

     (* Compute regexps for the fields *)

     val treevals = List.map (fieldval_to_tree (the_enumMap())) fvals
     val regexps = map Regexp_Type.tree_to_regexp treevals
     val the_regexp = catlist regexps
     val the_regexp_tm = regexpSyntax.mk_regexp the_regexp
     
     val codings = List.map (curry Finmap.find (the_codingMap()) o format_of) fvals

     (* Define encoder *)
     val encs = map #enc codings
     val encode_fields = map mk_comb (zip encs projs)
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
     val the_goal = mk_forall(recdvar,
        mk_eq(wfpred_app,
          pred_setSyntax.mk_in
            (mk_comb(encodeFn,recdvar),
	     mk_comb(regexp_lang_tm,the_regexp_tm))))
     (* Define decoder (simplistic for now) *)
     val vars = map (fn i => mk_var("v"^Int.toString i, stringSyntax.char_ty))
                    (upto 0 (List.foldl (op+) 0 fwidths - 1))
     val chunked_vars = take_list fwidths vars
     val decodeFn_var = mk_var("decode_"^rtyname,
                            stringSyntax.string_ty --> optionSyntax.mk_option recdty)
     val decodeFn_lhs = mk_comb(decodeFn_var, mk_var("s",stringSyntax.string_ty))
     val decode_def =
        Define
          `decode s =
             case s 
              of [v0; v1; v2; v3; v4; v5; v6; v7; v8; v9; v10; v11;
                  v12; v13; v14; v15; v16; v17; v18; v19; v20; v21]
                  => SOME <| map := 
                              <| wp1 := <| latitude := deci 1 [v0]; 
                                           longitude := deci 2 [v1;v2]; 
                                           altitude := deci 2 [v3;v4] |>; 
                                 wp2 := <| latitude := deci 1 [v5]; 
                                           longitude := deci 2 [v6;v7]; 
                                           altitude := deci 2 [v8;v9] |>;
                                 wp3 := <| latitude := deci 1 [v10]; 
                                           longitude := deci 2 [v11;v12]; 
                                           altitude := deci 2 [v13;v14] |>;
                                 wp4 := <| latitude := deci 1 [v15]; 
                                           longitude := deci 2 [v16;v17]; 
                                           altitude := deci 2 [v18;v19] |>; 
          		       |>;
                             pattern := dec_FlightPattern [v20]
                           |>
                | otherwise => NONE`
 in
     (the_regexp_tm, encodeFn_def, decode_def, the_goal)
 end

(*---------------------------------------------------------------------------*)
(* Reasoner for character sets. charset_conv converts terms of the form      *)
(*                                                                           *)
(*   regexp_lang (Chset cs)                                                  *)
(*                                                                           *)
(* into theorems of the form                                                 *)
(*                                                                           *)
(*   |- regexp_lang (Chset cs) = {#"c1", ..., #"cn"}                         *)
(*                                                                           *)
(* where c1 ... cn are the elements of cs.                                   *)
(*---------------------------------------------------------------------------*)

fun charset_term_elts (cs:term) = 
  Regexp_Type.charset_elts (regexpSyntax.term_to_charset cs);

val csvar = mk_var("cs",regexpSyntax.charset_ty);
val regexp_chset_pat = ``regexp$regexp_lang ^(regexpSyntax.mk_chset csvar)``;

fun char_tac (asl,c) = 
    let val ctm = fst(dest_eq (last (strip_conj (snd (dest_exists c)))))
    in Q.EXISTS_TAC `ORD ^ctm` >> EVAL_TAC
    end

val tactic = 
   RW_TAC (list_ss ++ pred_setLib.PRED_SET_ss)
          [pred_setTheory.EXTENSION,
           regexpTheory.regexp_lang_def,
           charsetTheory.charset_mem_def,
           charsetTheory.alphabet_size_def,EQ_IMP_THM]
    >> TRY (ntac 2 (pop_assum mp_tac)
            >> Q.ID_SPEC_TAC `c`
            >> REPEAT (CONV_TAC (numLib.BOUNDED_FORALL_CONV EVAL))
            >> rw_tac bool_ss []
            >> NO_TAC)
    >> W char_tac;

fun charset_conv tm = 
 case total (match_term regexp_chset_pat) tm
  of NONE => raise ERR "charset_conv" 
                    "expected ``regexp_lang (Chset cs)`` term"
  | SOME (theta, _) => 
     let open pred_setSyntax
         val chars = charset_term_elts (subst theta csvar)
         val char_tms = map fromMLchar chars
         val string_tms = map (fromMLstring o String.str) chars
         val the_goal = mk_eq(tm, mk_set string_tms)
  in
     prove(the_goal,tactic)
  end

val charset_conv_ss = 
  simpLib.std_conv_ss
    {name="charset_conv",
     conv = charset_conv, 
     pats = [regexp_chset_pat]}

(*---------------------------------------------------------------------------*)
(* Proves goals of the form                                                  *)
(*                                                                           *)
(*   s IN regexp_lang (Chset (Charset a b c d))                              *)
(*     <=>                                                                   *)
(*   (LENGTH s = 1) /\ dec s <= K                                            *)
(*---------------------------------------------------------------------------*)

val IN_CHARSET_NUM_TAC =
 rw_tac (list_ss ++ charset_conv_ss) [EQ_IMP_THM,strlen_eq,LE_LT1]
  >> TRY EVAL_TAC 
  >> rule_assum_tac (SIMP_RULE list_ss [dec_def, numposrepTheory.l2n_def, ord_mod_256])
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
