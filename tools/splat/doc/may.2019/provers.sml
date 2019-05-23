open HolKernel Parse boolLib bossLib splatLib intLib;

open arithmeticTheory listTheory stringTheory pred_setLib
     FormalLangTheory charsetTheory regexpTheory regexpLib
     numposrepTheory splatTheory;

val int_ss = intSimps.int_ss;

(*---------------------------------------------------------------------------*)
(* Boilerplate prelude stuff                                                 *)
(*---------------------------------------------------------------------------*)

val _ = numLib.prefer_num();

val _ = overload_on ("++",``list$APPEND``);

infix byA;
val op byA = BasicProvers.byA;

val qpat_k_assum = Lib.C qpat_x_assum kall_tac;
val allhyp_mp_tac = rpt (pop_assum mp_tac);
val allhyp_kill_tac = rpt (pop_assum kall_tac);
fun qpat_keeponly_assum qpat = qpat_x_assum qpat mp_tac >> allhyp_kill_tac

fun qspec q th = th |> Q.SPEC q
fun qspec_arith q th = qspec q th |> SIMP_RULE arith_ss [];

val var_eq_tac = rpt BasicProvers.VAR_EQ_TAC;

val decide = bossLib.DECIDE;
val qdecide = decide o Parse.Term;

val ERR = mk_HOL_ERR "DEMO";

val regexp_lang_cat = el 2 (CONJUNCTS regexp_lang_def);
val regexp_lang_or = last (CONJUNCTS regexp_lang_def);

(*---------------------------------------------------------------------------*)
(* Useful regexp rewrites                                                    *)
(*---------------------------------------------------------------------------*)

val in_chset_cat = Q.prove
(`!s cs r.
     s IN regexp_lang (Cat (Chset cs) r)
       <=>
     ?c t. (s = STRING c t) /\
           (STRING c "") IN regexp_lang (Chset cs) /\ t IN regexp_lang r`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss)
        [regexp_lang_def,EQ_IMP_THM,IN_dot]
 >> metis_tac [STRCAT_EQNS])

val in_chset = Q.prove
(`!n cs s L.
     STRING c s IN regexp_lang (Chset cs) dot L
       <=>
     (STRING c "") IN regexp_lang (Chset cs) /\ s IN L`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss)
        [regexp_lang_def,EQ_IMP_THM,IN_dot]
 >> full_simp_tac list_ss []
 >> var_eq_tac
 >- metis_tac[]
 >- (qexists_tac `STRING (CHR c') ""`
     >> qexists_tac `s`
     >> rw_tac list_ss []
     >> metis_tac[])
);

val not_in_chset = Q.prove
(`!cs. "" NOTIN regexp_lang (Chset cs)`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss)
        [regexp_lang_def,EQ_IMP_THM,IN_dot]);

Theorem ORD_EQ :
 !c n. (ORD c = n) <=> ((CHR n = c) /\ n < 256)
Proof
  metis_tac [CHR_ORD,ORD_CHR_RWT,ORD_BOUND]
QED

val two_leq_length = Q.prove
(`!L. (2 <= LENGTH L) <=> ?a b t. L = a::b::t`,
  Cases_on `L` >> rw_tac list_ss [] >>
  Cases_on `t` >> rw_tac list_ss []);

val APPEND_EQ_CONS_ALT = Q.prove
(`!l1 l2 h t.
  ((l1 ++ l2 = h::t) <=> ((l1 = []) /\ (l2 = h::t)) \/ (∃lt. (l1 = h::lt) ∧ (t = lt ⧺ l2)))
  /\
  ((h::t = l1 ++ l2) <=> ((l1 = []) ∧ (l2 = h::t)) ∨ (∃lt. (l1 = h::lt) ∧ (t = lt ⧺ l2)))`,
 metis_tac [APPEND_EQ_CONS]);

val len_lem = Q.prove
(`!A B C.
   (2 <= LENGTH A) /\ (2 <= LENGTH B) /\ (3 <= LENGTH C) /\
(A ++ B ++ C = [c1;c2;c3;c4;c5;c6;c7])
  ==>
  (A = [c1;c2]) /\ (B = [c3;c4]) /\ (C = [c5;c6;c7])`,
 rw_tac list_ss [APPEND_EQ_CONS_ALT,two_leq_length]
 >> full_simp_tac list_ss [APPEND_EQ_CONS_ALT]);

(* Needs some thought *)
(*
Theorem enc_dec :
 !s. enc (LENGTH s) (dec s) = s
Proof
rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
QED
*)


Theorem decZ_eqns :
  (!s s'. decZ (STRING #"+" s') = int_of_num (dec s')) /\
  (!s s'. decZ (STRING #"-" s') = -(int_of_num (dec s')))
Proof
rw_tac list_ss [decZ_def,CHR_11]
QED

Theorem strlen_encZ :
  !w i. STRLEN (encZ w i) = 1 + STRLEN (enc w (Num(ABS i)))
Proof
rw_tac list_ss [encZ_def]
QED

Theorem dec_char :
  !c. dec [c] = ORD c
Proof
 rw_tac list_ss [dec_def,l2n_def,ord_mod_256]
QED

val [encz1,encz2,encz3,encz4,encz8] = CONJUNCTS encZ_bytes;

val [enc1,enc2,enc3,enc4,enc5,enc6,enc7,encz8]
    = CONJUNCTS (SIMP_RULE arith_ss [] enc_bytes);

(*---------------------------------------------------------------------------*)
(* Reduce charsets to constraints                                            *)
(*---------------------------------------------------------------------------*)

fun regexp_elts r =
   Regexp_Type.charset_elts
     (regexpSyntax.term_to_charset r);

fun charset_intervals r =
  let fun endpoints list = (IntInf.toInt (hd list),IntInf.toInt (last list))
  in map endpoints
      (Interval.intervals
         (map (IntInf.fromInt o Char.ord) (regexp_elts r)))
  end

fun CHECK_SPEC_TAC (t1,t2) (asl,c) =
  (if can(find_term (aconv t1)) c
    then SPEC_TAC (t1,t2)
    else NO_TAC) (asl,c)

fun GEVAL_TAC (asl,c) =
    (if null(free_vars c) then EVAL_TAC else NO_TAC) (asl,c);

fun const_bound tm =
 let open numSyntax
 in (is_less tm orelse is_leq tm)
    andalso
    is_numeral (rand tm)
 end

val ordered_pop_tac =
 rpt (PRED_ASSUM (not o const_bound) mp_tac)
  >> PRED_ASSUM const_bound mp_tac;

val prover =
 let open numSyntax stringSyntax
     val cvar = mk_var("c",num)
     val nvar = mk_var("n",num)
     val ordtm = mk_ord(mk_var("c",char_ty))
 in
    rw_tac (list_ss ++ pred_setLib.PRED_SET_ss)
      [pred_setTheory.EXTENSION, regexpTheory.regexp_lang_def,
       charsetTheory.charset_mem_def, charsetTheory.alphabet_size_def,
       EQ_IMP_THM,strlen_eq,LE_LT1]
    >> full_simp_tac list_ss [dec_char,ORD_CHR_RWT]
    >> TRY (qexists_tac `ORD c` >> rw_tac list_ss [CHR_ORD])
    >> (GEVAL_TAC ORELSE
         (ordered_pop_tac
           >> (CHECK_SPEC_TAC (cvar,nvar) ORELSE
               CHECK_SPEC_TAC (ordtm,nvar))
           >> REPEAT (CONV_TAC (numLib.BOUNDED_FORALL_CONV EVAL))
           >> gen_tac >> ACCEPT_TAC TRUTH))
 end

(*---------------------------------------------------------------------------*)
(* Takes a term of the form                                                  *)
(*                                                                           *)
(*  s IN regexp_lang (Chset (Charset a b c d))                               *)
(*                                                                           *)
(* and returns                                                               *)
(*                                                                           *)
(* |- s IN regexp_lang (Chset (Charset a b c d)) <=>                         *)
(*    STRLEN s = 1 /\ lo <= dec s <= hi                                      *)
(*                                                                           *)
(* where lo and hi are the interval endpoints. If lo = 0 then it is omitted. *)
(* If lo=hi then we just have (dec s = lo)                                   *)
(*---------------------------------------------------------------------------*)

fun pure_in_charset_conv tm =
 let open regexpSyntax stringSyntax pred_setSyntax numSyntax
     val (s,rlang) = dest_in tm
     val reg = dest_chset(dest_regexp_lang rlang)
     val ivls = charset_intervals reg
     fun ivl_to_prop (lo,hi) =
         if lo = hi then
            ``dec ^s = ^(term_of_int lo)``
         else
         if lo = 0 then
            ``dec ^s < ^(term_of_int(hi + 1))``
         else
            ``^(term_of_int lo) <= dec ^s /\ dec ^s <= ^(term_of_int hi)``
     val slen = ``STRLEN ^s = 1``
     val property = mk_eq(tm, mk_conj(slen,list_mk_disj (map ivl_to_prop ivls)))
 in
    prove(property,prover)
 end


fun dest_in_chset tm =
 let open regexpSyntax
     val (s,rlang) = pred_setSyntax.dest_in tm
 in
     dest_chset(dest_regexp_lang rlang)
 end

(*---------------------------------------------------------------------------*)
(* Make a memo-izing version of the charset-to-interval conv.                *)
(*---------------------------------------------------------------------------*)

val in_charset_conv =
 let val conv =
      Conv.memoize
        (Lib.total dest_in_chset)
        (Redblackmap.fromList Term.compare [])
(*
          (map (fn th => (dest_in_chset(lhs(concl th)),th))
               charset_interval_lems))
*)
       (K true)
       (ERR "in_charset_conv (memoized)" "")
       pure_in_charset_conv
 in fn tm =>
      let val thm = conv tm
          val left = lhs(concl thm)
      in
        if aconv tm left then
          thm
        else
          INST (fst (match_term left tm)) thm
      end
 end;

(*---------------------------------------------------------------------------*)
(* Construct a simplification set from the memoized conversion.              *)
(*---------------------------------------------------------------------------*)

val in_charset_conv_ss =
 let val csvar = mk_var("cs",regexpSyntax.charset_ty)
     val svar = mk_var("s",stringSyntax.string_ty)
     val regexp_chset_pat = ``^svar IN regexp$regexp_lang ^(regexpSyntax.mk_chset csvar)``
 in
  simpLib.std_conv_ss
    {name = "in_charset_conv",
     conv = in_charset_conv,
     pats = [regexp_chset_pat]}
 end

fun ROUND_TRIP_TAC (encode_def, decode_def, good_gps_def) =
 rw_tac list_ss [decode_def, encode_def, list_case_eq, PULL_EXISTS,good_gps_def,
                 fetch "-" "gps_component_equality"]
  >> `-256 < m.lat /\ m.lat < 256 /\
      -256 < m.lon /\ m.lon < 256 /\
      -65536 < m.alt /\ m.alt < 65536` by intLib.ARITH_TAC
  >> imp_res_tac encz1
  >> imp_res_tac encz2
  >> rw_tac (srw_ss()) []
  >> metis_tac [decz_encz]
;

fun enforce tac gl = (tac gl handle HOL_ERR _ => cheat gl);

fun AGREE_ENCODE_TAC (encode_def, decode_def, good_gps_def) =
enforce
(rw_tac list_ss [regexp_lang_cat,IN_dot,PULL_EXISTS,encode_def,EQ_IMP_THM]
  >- (rw_tac (list_ss ++ in_charset_conv_ss)
       [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,IN_dot,strlen_eq,PULL_EXISTS,dec_char]
     >> qexists_tac `encZ 1 m.lat`
     >> qexists_tac `encZ 1 m.lon`
     >> qexists_tac `enc 2 (Num (ABS m.alt))`
     >> qexists_tac `#"+"`
     >> simp_tac bool_ss [GSYM STRCAT_ASSOC,STRCAT_11]
     >> full_simp_tac list_ss [good_gps_def]
     >> rpt conj_tac
        >- metis_tac [encZ_def]
        >- (ntac 4 (pop_assum kall_tac)
            >> Cases_on `m.lat < 0` THENL [disj2_tac, disj1_tac]
            >> `Num(ABS m.lat) < 256` by intLib.ARITH_TAC
            >> rw_tac int_ss [encZ_def,enc_bytes,ORD_CHR_RWT]
            >> intLib.ARITH_TAC)
        >- (ntac 2 (pop_assum kall_tac)
            >> ntac 2 (pop_assum mp_tac)
            >> ntac 2 (pop_assum kall_tac)
            >> rpt strip_tac
            >> Cases_on `m.lon < 0` THENL [disj2_tac, disj1_tac]
            >> `Num(ABS m.lon) < 256` by intLib.ARITH_TAC
            >> rw_tac int_ss [encZ_def,enc_bytes,ORD_CHR_RWT]
            >> intLib.ARITH_TAC)
        >- EVAL_TAC
        >- (ntac 2 (pop_assum mp_tac)
            >> rpt (pop_assum kall_tac)
            >> rpt strip_tac
            >> `m.alt >= 256 * 70 \/ m.alt < 256 * 70`
                  by intLib.ARITH_TAC
            THENL [disj1_tac,disj2_tac]
            >- (`Num(ABS m.alt) < 65536` by intLib.ARITH_TAC
                >> rw_tac int_ss [enc_bytes,ORD_CHR_RWT]
                >> intLib.ARITH_TAC)
            >- (Cases_on `m.alt < 256` THENL [disj1_tac,disj2_tac]
                >- (`Num(ABS m.alt) < 256` by intLib.ARITH_TAC
                     >> rw_tac int_ss [enc_bytes,ORD_CHR_RWT]
                     >> intLib.ARITH_TAC)
                >- (`Num(ABS m.alt) < 65536` by intLib.ARITH_TAC
                     >> rw_tac int_ss [enc_bytes,ORD_CHR_RWT]
                     >> intLib.ARITH_TAC)))
    )
 (* second part *)
 >- (`2 <= LENGTH (encZ 1 m.lat) /\ 2 <= LENGTH (encZ 1 m.lon) /\ 3 <= LENGTH (encZ 2 m.alt)`
        byA (rw_tac list_ss [strlen_encZ]
             >> metis_tac [lower_enc, qdecide `2n = 1 + 1`, qdecide `3n = 2 + 1`,
                           arithmeticTheory.LE_ADD_RCANCEL])
     >> full_simp_tac (list_ss ++ in_charset_conv_ss)
              [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,
               IN_dot,strlen_eq,PULL_EXISTS,dec_char]
     >> rw_tac list_ss []
     >> full_simp_tac list_ss [dec_char,ORD_EQ]
     >> rw_tac list_ss []
     >> `?c c1 c2 c3 c4 c5 c6.
          (encZ 1 m.lat = [c;c1]) /\ (encZ 1 m.lon = [c2;c3]) /\ (encZ 2 m.alt = [c4;c5;c6])`
            by metis_tac [len_lem]
     >> full_simp_tac list_ss [encZ_def]
     >> BasicProvers.NORM_TAC list_ss [CHR_11]
     >> asm_simp_tac list_ss [good_gps_def]
     >> `(m.lat = decZ (encZ 1 m.lat)) /\
         (m.lon = decZ (encZ 1 m.lon)) /\
         (m.alt = decZ (encZ 1 m.alt))` by metis_tac [decz_encz]
     >> ntac 3 (pop_assum SUBST1_TAC)
     >> asm_simp_tac bool_ss [encZ_def]
     >> asm_simp_tac int_ss [decZ_eqns,dec_char,dec_enc]
     >> qpat_x_assum `enc 2 _ = _` (mp_tac o AP_TERM ``dec``)
     >> rw_tac list_ss [dec_enc, dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
    )
)
;

fun AGREE_DECODE_TAC (encode_def, decode_def, AGREE_ENCODE_THM) =
enforce
 (rw_tac std_ss [AGREE_ENCODE_THM]
  >> full_simp_tac (list_ss ++ in_charset_conv_ss)
       [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,IN_dot,strlen_eq,PULL_EXISTS,dec_char,ORD_EQ]
  >> rw_tac list_ss [decode_def,decZ_eqns,dec_char]
  >> rw_tac (srw_ss()) [encode_def]
  >- (qexists_tac `[#"+"; c']`
      >> qexists_tac `[#"+"; c'']`
      >> qexists_tac `[c;#"F"]`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- rw_tac list_ss [Once (GSYM MOD_PLUS),CHR_ORD]
         >- rw_tac list_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO])

  >- (qexists_tac `[#"+"; c']`
      >> qexists_tac `[#"+"; c'']`
      >> qexists_tac `[c;#"\^@"]`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- rw_tac list_ss [Once (GSYM MOD_PLUS),CHR_ORD]
         >- rw_tac list_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO])

  >- (qexists_tac `[#"+"; c']`
      >> qexists_tac `[#"+"; c'']`
      >> qexists_tac `[c;c''']`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- (rw_tac bool_ss [Once ADD_SYM,Once MULT_SYM]
              >> rw_tac list_ss [MOD_MULT,CHR_ORD])
         >- (rw_tac (srw_ss()) [ADD_DIV_RWT,LESS_DIV_EQ_ZERO]
              >> rw_tac bool_ss [Once MULT_SYM]
              >> rw_tac std_ss [MULT_DIV,ord_mod_256,CHR_ORD]))

  >- (qexists_tac `[#"+"; c']`
      >> qexists_tac `[#"-"; c'']`
      >> qexists_tac `[c;#"F"]`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- rw_tac list_ss [Once (GSYM MOD_PLUS),CHR_ORD]
         >- rw_tac list_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO])

  >- (qexists_tac `[#"+"; c']`
      >> qexists_tac `[#"-"; c'']`
      >> qexists_tac `[c;#"\^@"]`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- rw_tac list_ss [Once (GSYM MOD_PLUS),CHR_ORD]
         >- rw_tac list_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO])

  >- (qexists_tac `[#"+"; c']`
      >> qexists_tac `[#"-"; c'']`
      >> qexists_tac `[c;c''']`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- (rw_tac bool_ss [Once ADD_SYM,Once MULT_SYM]
              >> rw_tac list_ss [MOD_MULT,CHR_ORD])
         >- (rw_tac (srw_ss()) [ADD_DIV_RWT,LESS_DIV_EQ_ZERO]
              >> rw_tac bool_ss [Once MULT_SYM]
              >> rw_tac std_ss [MULT_DIV,ord_mod_256,CHR_ORD]))

  >- (qexists_tac `[#"-"; c']`
      >> qexists_tac `[#"+"; c'']`
      >> qexists_tac `[c;#"F"]`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- rw_tac list_ss [Once (GSYM MOD_PLUS),CHR_ORD]
         >- rw_tac list_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO])

  >- (qexists_tac `[#"-"; c']`
      >> qexists_tac `[#"+"; c'']`
      >> qexists_tac `[c;#"\^@"]`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- rw_tac list_ss [Once (GSYM MOD_PLUS),CHR_ORD]
         >- rw_tac list_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO])

  >- (qexists_tac `[#"-"; c']`
      >> qexists_tac `[#"+"; c'']`
      >> qexists_tac `[c;c''']`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- (rw_tac bool_ss [Once ADD_SYM,Once MULT_SYM]
              >> rw_tac list_ss [MOD_MULT,CHR_ORD])
         >- (rw_tac (srw_ss()) [ADD_DIV_RWT,LESS_DIV_EQ_ZERO]
              >> rw_tac bool_ss [Once MULT_SYM]
              >> rw_tac std_ss [MULT_DIV,ord_mod_256,CHR_ORD]))

  >- (qexists_tac `[#"-"; c']`
      >> qexists_tac `[#"-"; c'']`
      >> qexists_tac `[c;#"F"]`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- rw_tac list_ss [Once (GSYM MOD_PLUS),CHR_ORD]
         >- rw_tac list_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO])

  >- (qexists_tac `[#"-"; c']`
      >> qexists_tac `[#"-"; c'']`
      >> qexists_tac `[c;#"\^@"]`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- rw_tac list_ss [Once (GSYM MOD_PLUS),CHR_ORD]
         >- rw_tac list_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO])

  >- (qexists_tac `[#"-"; c']`
      >> qexists_tac `[#"-"; c'']`
      >> qexists_tac `[c;c''']`
      >> rw_tac int_ss [encZ_def,enc_bytes,CHR_ORD]
      >> rw_tac std_ss [STRCAT_EQNS]
      >> rw_tac list_ss [dec_def, l2n_def,ord_mod_256,ORD_CHR_RWT]
      >> rw_tac int_ss [enc_bytes]
         >- (rw_tac bool_ss [Once ADD_SYM,Once MULT_SYM]
              >> rw_tac list_ss [MOD_MULT,CHR_ORD])
         >- (rw_tac (srw_ss()) [ADD_DIV_RWT,LESS_DIV_EQ_ZERO]
              >> rw_tac bool_ss [Once MULT_SYM]
              >> rw_tac std_ss [MULT_DIV,ord_mod_256,CHR_ORD]))
);

