open HolKernel Parse boolLib bossLib splatLib;

open arithmeticTheory listTheory stringTheory pred_setLib
     FormalLangTheory charsetTheory regexpTheory regexpLib
     numposrepTheory splatTheory;

(*---------------------------------------------------------------------------*)
(* Boilerplate prelude stuff                                                 *)
(*---------------------------------------------------------------------------*)

val _ = numLib.prefer_num();

overload_on ("++",``list$APPEND``);

Globals.priming := SOME "";

infix byA;
val op byA = BasicProvers.byA;

val qpat_k_assum = Lib.C qpat_x_assum kall_tac;

fun qspec q th = th |> Q.SPEC q 
fun qspec_arith q th = qspec q th |> SIMP_RULE arith_ss [];

val var_eq_tac = rpt BasicProvers.VAR_EQ_TAC;

val decide = bossLib.DECIDE;
val qdecide = decide o Parse.Term;

val regexp_lang_cat = el 2 (CONJUNCTS regexp_lang_def);
val regexp_lang_or = last (CONJUNCTS regexp_lang_def);

fun strip_cat tm =
 let open regexpSyntax
 in case total dest_cat tm
     of NONE => [tm]
      | SOME (r,s) => r :: strip_cat s
 end;


(*---------------------------------------------------------------------------*)
(* Declare simple record and define wellformedness                           *)
(*---------------------------------------------------------------------------*)

val _ = new_theory "num_coord";

val _ = 
 Hol_datatype
   `dms = <| degrees : num ; 
             minutes : num ; 
             seconds : num |>`;

val good_dms_def =
  Define
    `good_dms recd <=>
         0 <= recd.degrees /\ recd.degrees <= 90 /\
         0 <= recd.minutes /\ recd.minutes <= 59 /\
         0 <= recd.seconds /\ recd.seconds <= 5999`;

val enc_degrees_def =
    Define `enc_degrees d = enc 1 d`;

val enc_minutes_def =
    Define `enc_minutes m = enc 1 m`;

val enc_seconds_def =
    Define `enc_seconds s = enc 2 s`;

(*---------------------------------------------------------------------------*)
(* Encode/decode dms elts                                                    *)
(*---------------------------------------------------------------------------*)

val enc_dms_def =
    Define
    `enc_dms recd =
       CONCAT [enc_degrees recd.degrees;
               enc_minutes recd.minutes;
               enc_seconds recd.seconds]`;

val dec_dms_def =
 Define 
  `dec_dms s =
    case s 
     of [dch; mch; sch1; sch2] => 
        SOME <| degrees := dec [dch]; 
                minutes := dec [mch]; 
                seconds := dec [sch1; sch2] |>
      | otherwise => NONE`;
	     
val dec_enc_dms = Q.store_thm
("dec_enc_dms",
 `!m. good_dms m ==> (dec_dms (enc_dms m) = SOME m)`,
 rw_tac list_ss [good_dms_def,dec_dms_def, enc_dms_def,
                 enc_degrees_def, enc_minutes_def, enc_seconds_def]
  >> `(?a. enc 1 m.degrees = [a]) /\
      (?b. enc 1 m.minutes = [b]) /\
      (?c d. enc 2 m.seconds = [c;d])`
      by rw_tac list_ss [splatTheory.enc_bytes]
  >> rw_tac list_ss [fetch "-" "dms_component_equality"]
  >> metis_tac [splatTheory.dec_enc]);

(*---------------------------------------------------------------------------*)
(* Regexp expressing the interval constraints                                *)
(*---------------------------------------------------------------------------*)

val dms_regexp = Regexp_Type.fromQuote `\i{0,90}\i{0,59}\i{0,5999}`;

val dms_regexp_term = regexpSyntax.mk_regexp dms_regexp;

(*---------------------------------------------------------------------------*)
(* lift to level of msg format                                               *)
(*---------------------------------------------------------------------------*)

val IN_CHARSET_NUM_TAC =
 rw_tac (list_ss ++ regexpLib.charset_conv_ss) [EQ_IMP_THM,strlen_eq,LE_LT1]
  >> TRY EVAL_TAC 
  >> rule_assum_tac 
        (SIMP_RULE list_ss [dec_def, numposrepTheory.l2n_def, ord_mod_256])
  >> TRY (POP_ASSUM ACCEPT_TAC)
  >> rpt (qpat_x_assum `_ < ORD c` mp_tac ORELSE qpat_x_assum `ORD c < _` mp_tac)
  >> Q.SPEC_TAC (`ORD c`, `n`)
  >> REPEAT (CONV_TAC (numLib.BOUNDED_FORALL_CONV EVAL))
  >> rw_tac bool_ss [];

val ilem1A = Q.prove
(`!s. s IN regexp_lang 
             (Chset (Charset 0xFFFFFFFFFFFFFFFFw 0x7FFFFFFw 0w 0w))
      <=> 
     (LENGTH s = 1) /\ dec s <= 90`,
 IN_CHARSET_NUM_TAC);

val ilem2A = Q.prove
(`!s. s IN regexp_lang (Chset (Charset 0xFFFFFFFFFFFFFFFw 0w 0w 0w))
       <=> 
      (LENGTH s = 1) /\ dec s <= 59`,
 IN_CHARSET_NUM_TAC);

val ilemDOT = Q.prove
(`!s. s IN regexp_lang 
            (Chset 
              (Charset 0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFFFFFw 
                       0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFFFFFw))
       <=>
      (LENGTH s = 1) /\ dec s < 256`,
 IN_CHARSET_NUM_TAC);

val ilemZERO = Q.prove
(`!s. s IN regexp_lang (Chset (Charset 0x1w 0w 0w 0w))
       <=>
      (LENGTH s = 1) /\ (dec s = 0)`,
 IN_CHARSET_NUM_TAC);

val ilem4A = Q.prove
(`!s. s IN regexp_lang (Chset (Charset 0x7FFFFEw 0w 0w 0w))
      <=> 
     (LENGTH s = 1) /\ 0 < dec s /\ dec s < 23`,
 IN_CHARSET_NUM_TAC);

val ilem5A = Q.prove
(`!s. s IN regexp_lang 
            (Chset 
              (Charset 0xFFFFFFFFFFFFFFFFw 0xFFFFFFFFFFFFw 0w 0w))
      <=> 
     (LENGTH s = 1) /\ dec s < 112`,
 IN_CHARSET_NUM_TAC);

val ilem6A = Q.prove
(`!s. s IN regexp_lang (Chset (Charset 0x800000w 0w 0w 0w))
      <=> 
      (LENGTH s = 1) /\ (dec s = 23)`,
 IN_CHARSET_NUM_TAC);

val ilems = [ilem1A,ilem2A,ilemDOT,ilemZERO,ilem4A,ilem5A,ilem6A]

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
 >- (qexists_tac `STRING (CHR c1) ""`
     >> qexists_tac `s`
     >> rw_tac list_ss []
     >> metis_tac[])
);

val not_in_chset = Q.prove
(`!cs. "" NOTIN regexp_lang (Chset cs)`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
        [regexp_lang_def,EQ_IMP_THM,IN_dot]);

val strcat_enc1_in_chset = Q.prove
(`!n cs s L. 
    n < 256 ==>
    (STRCAT (enc 1 n) s IN regexp_lang (Chset cs) dot L
       <=> 
    enc 1 n IN regexp_lang (Chset cs) /\ s IN L)`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
        [regexp_lang_def,EQ_IMP_THM,IN_dot,enc_bytes]
 >> full_simp_tac list_ss []
 >- metis_tac[]
 >- (qexists_tac `STRING (CHR c) ""`
     >> qexists_tac `s`
     >> rw_tac list_ss []
     >> metis_tac[]));

val lift_suc_thm = last (CONJUNCTS ADD_CLAUSES);
		
val lem = Q.prove
(`!A B C. 1n <= LENGTH A /\ 1n <= LENGTH B /\ 2n <= LENGTH C /\
          (A ++ B ++ C = [c1;c2;c3;c4]) 
         ==> 
          (A = [c1]) /\ (B = [c2]) /\ (C = [c3;c4])`,
rpt gen_tac 
 >> strip_tac
 >> `LENGTH (A ++ B ++ C) = LENGTH [c1;c2;c3;c4]` by metis_tac[]
 >> Cases_on `A` 
 >> full_simp_tac list_ss [lift_suc_thm]
 >> `LENGTH t = 0` by decide_tac
 >> Cases_on `B` 
 >> full_simp_tac list_ss [lift_suc_thm]
 >> `LENGTH t1 = 0` by decide_tac
 >> rw_tac list_ss []
 >> metis_tac [listTheory.LENGTH_NIL,listTheory.APPEND]);

val lem_eq = Q.prove
(`!A B C. 
   1n <= LENGTH A /\ 1n <= LENGTH B /\ 2n <= LENGTH C
    ==> 
   ((A ++ B ++ C = [c1;c2;c3;c4]) <=> (A = [c1]) /\ (B = [c2]) /\ (C = [c3;c4]))`,
 rw_tac list_ss [EQ_IMP_THM] >> metis_tac [lem]);


val AGREE_ENCODE_PROP = Q.store_thm
("AGREE_ENCODE_PROP",
 `!m:dms. good_dms m <=> enc_dms(m) IN regexp_lang ^dms_regexp_term`,
 rw_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
	   [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,
            enc_dms_def, enc_degrees_def, enc_minutes_def, enc_seconds_def,EQ_IMP_THM]
 >> full_simp_tac bool_ss [Once (GSYM STRCAT_ASSOC)]
 >- (full_simp_tac bool_ss [good_dms_def]
     >> rw_tac list_ss [strcat_enc1_in_chset]  (* split into enc-level proofs *)
     >- rw_tac list_ss (dec_enc::enc_bytes::ilems)
     >- rw_tac list_ss (dec_enc::enc_bytes::ilems)
     >- (rw_tac list_ss (in_chset::enc_bytes::ilems)
         >> Cases_on `m.seconds < 256`
         >> (rw_tac list_ss [dec_enc,GSYM enc_bytes] >> intLib.ARITH_TAC)))
 >- (full_simp_tac list_ss 
        ([regexp_lang_or,regexp_lang_cat,regexpTheory.LIST_UNION_def,IN_dot,strlen_eq] @ ilems)
     >> var_eq_tac
     >> `(enc 1 m.degrees = [c]) /\ (enc 1 m.minutes = [c1]) /\ (enc 2 m.seconds = [c2 ; c3])`
          by metis_tac [lem,lower_enc,STRCAT_ASSOC,STRCAT_EQNS]
     >> `m.degrees <= 90` by metis_tac [dec_enc,good_dms_def]
     >> `m.minutes <= 59` by metis_tac [dec_enc,good_dms_def]
     >> `m.seconds = dec [c2 ; c3]` by metis_tac [dec_enc]
     >> full_simp_tac list_ss [dec_def, l2n_def,ord_mod_256,good_dms_def])
);


val AGREE_DECODE_PROP = Q.store_thm
("AGREE_DECODE_PROP",
 `!s. s IN regexp_lang ^dms_regexp_term ==> good_dms (THE (dec_dms s))`,
 rw_tac std_ss [AGREE_ENCODE_PROP]
  >> Cases_on `s`
  >> full_simp_tac (list_ss ++ pred_setLib.PRED_SET_ss) 
      (ilems @ [regexp_lang_cat,regexp_lang_or,LIST_UNION_def,IN_dot,not_in_chset,strlen_eq])
  >> rw_tac list_ss [dec_dms_def,optionTheory.THE_DEF,PULL_EXISTS]
  >> rw_tac list_ss [enc_dms_def, enc_degrees_def, enc_minutes_def, enc_seconds_def]
  >> qexists_tac `[c2 ; c3]`
  >> qexists_tac `c`
  >> qexists_tac `c1`
  >> rw_tac list_ss [lem_eq,lower_enc,enc_bytes]
  >> full_simp_tac list_ss [dec_def, l2n_def,ord_mod_256,CHR_ORD]
  >> rw_tac list_ss [enc_bytes,stringTheory.CHR_ORD,LESS_DIV_EQ_ZERO]
  >- (pop_assum (mp_tac o Q.AP_TERM `CHR`) >> rw_tac list_ss [CHR_ORD])
  >- (rw_tac bool_ss [Once ADD_SYM,Once MULT_SYM]
      >> rw_tac arith_ss [MOD_MULT,CHR_ORD])
  >- (rw_tac bool_ss [Once ADD_SYM,Once MULT_SYM]
      >> rw_tac arith_ss [DIV_MULT,CHR_ORD])
  >- rw_tac arith_ss [Once (GSYM MOD_PLUS),CHR_ORD]
  >- (rw_tac arith_ss [ADD_DIV_RWT,LESS_DIV_EQ_ZERO,CHR_ORD]
      >> pop_assum (mp_tac o Q.AP_TERM `CHR`) >> rw_tac list_ss [CHR_ORD])
);

val _ = export_theory();
 
